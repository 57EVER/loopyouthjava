package com.loopyouth.service.impl;

import com.loopyouth.entity.GoodsBrowser;
import com.loopyouth.entity.GoodsInfo;
import com.loopyouth.entity.Information;
import com.loopyouth.entity.UserInfo;
import com.loopyouth.repository.GoodsBrowserRepository;
import com.loopyouth.repository.GoodsInfoRepository0;
import com.loopyouth.repository.InformationRepository;
import com.loopyouth.repository.UserInfoRepository;
import com.loopyouth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserInfoRepository userInfoRepository;
    private final GoodsBrowserRepository goodsBrowserRepository;
    private final GoodsInfoRepository0 goodsInfoRepository;
    private final InformationRepository informationRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    @Transactional
    public UserInfo register(String uname, String upwd, String uemail) {
        UserInfo user = new UserInfo();
        user.setUname(uname);
        user.setUpwd(sha1Encrypt(upwd));
        user.setUemail(uemail);
        return userInfoRepository.save(user);
    }

    @Override
    public UserInfo login(String uname, String upwd) {
        UserInfo user = userInfoRepository.findByUname(uname).orElse(null);
        if (user == null) {
            return null;
        }
        String encrypted = sha1Encrypt(upwd);
        if (encrypted.equals(user.getUpwd())) {
            return user;
        }
        return null;
    }

    @Override
    public UserInfo findById(Integer id) {
        return userInfoRepository.findById(id).orElse(null);
    }

    @Override
    public UserInfo findByUname(String uname) {
        return userInfoRepository.findByUname(uname).orElse(null);
    }

    @Override
    public long countByUname(String uname) {
        return userInfoRepository.countByUname(uname);
    }

    @Override
    public long countByUemail(String uemail) {
        return userInfoRepository.countByUemail(uemail);
    }

    @Override
    @Transactional
    public void updateAddress(Integer userId, String ushou, String uaddress, String uyoubian, String uphone) {
        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUshou(ushou);
            user.setUaddress(uaddress);
            user.setUyoubian(uyoubian);
            user.setUphone(uphone);
            userInfoRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void updateProfile(Integer userId, String usex, String uage, String upersonInf, MultipartFile logo) {
        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUsex(usex);
            user.setUage(uage);
            user.setUpersonInf(upersonInf);
            if (logo != null && !logo.isEmpty()) {
                user.setUlogo(saveUploadFile(logo, "images"));
            }
            userInfoRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void changePassword(Integer userId, String newPwd) {
        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setUpwd(sha1Encrypt(newPwd));
            userInfoRepository.save(user);
        }
    }

    @Override
    public List<GoodsBrowser> getBrowseHistory(Integer userId) {
        return goodsBrowserRepository.findByUser_IdOrderByBrowserTimeDesc(userId);
    }

    @Override
    @Transactional
    public void recordBrowse(Integer userId, Integer goodsId) {
        GoodsBrowser existing = goodsBrowserRepository.findByUser_IdAndGood_Id(userId, goodsId).orElse(null);
        if (existing != null) {
            existing.setBrowserTime(LocalDateTime.now());
            goodsBrowserRepository.save(existing);
        } else {
            GoodsBrowser browser = new GoodsBrowser();
            UserInfo user = userInfoRepository.findById(userId).orElse(null);
            GoodsInfo goods = goodsInfoRepository.findById(goodsId).orElse(null);
            browser.setUser(user);
            browser.setGood(goods);
            browser.setBrowserTime(LocalDateTime.now());
            goodsBrowserRepository.save(browser);

            List<GoodsBrowser> allBrowsed = goodsBrowserRepository.findByUser_IdOrderByBrowserTimeDesc(userId);
            if (allBrowsed.size() > 5) {
                for (int i = 5; i < allBrowsed.size(); i++) {
                    goodsBrowserRepository.delete(allBrowsed.get(i));
                }
            }
        }
    }

    @Override
    public List<Information> getAllInformation() {
        return informationRepository.findAll();
    }

    @Override
    public List<Information> getInformationBySenderAndReceiver(String cusername1, String cusername) {
        return informationRepository.findByCusername1AndCusername(cusername1, cusername);
    }

    // 🔥🔥🔥 核心修复逻辑：双向合并聊天记录，完美解决单向对话和红点去重问题 🔥🔥🔥
    @Override
    public List<Object[]> getDistinctSenders(Integer userId) {
        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        if (user == null) return new ArrayList<>();
        String myName = user.getUname();

        // 1. 获取别人发给我的消息（作为 Receiver）
        List<Object[]> received = informationRepository.findDistinctSendersByUserId(userId);

        // 2. 获取我发给别人的消息（作为 Sender）
        List<Information> sent = informationRepository.findByCusername(myName);

        // 3. 用 Map 去重合并（Key: 联系人用户名, Value: 是否已读 ccheck）
        Map<String, Boolean> contactMap = new HashMap<>();

        // 遍历我主动发给别人的（对方是 cusername1）
        // 因为是我发出的，所以对我来说没有“未读”状态，状态默认为 true (已读)
        for (Information info : sent) {
            contactMap.put(info.getCusername1(), true);
        }

        // 遍历别人发给我的（对方是 cusername）
        for (Object[] obj : received) {
            String contactName = (String) obj[0];
            Boolean isRead = (obj[1] != null) ? (Boolean) obj[1] : true;

            if (contactMap.containsKey(contactName)) {
                // 如果联系人已存在，只要他发给我的消息里有一条是未读(false)，就标记整个会话为未读(false)
                Boolean currentStatus = contactMap.get(contactName);
                contactMap.put(contactName, currentStatus && isRead);
            } else {
                contactMap.put(contactName, isRead);
            }
        }

        // 4. 转换回 List<Object[]> 格式返回给前端页面渲染
        List<Object[]> result = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : contactMap.entrySet()) {
            result.add(new Object[]{entry.getKey(), entry.getValue()});
        }

        return result;
    }

    @Override
    public List<Information> getInformationByUserId(Integer userId) {
        return informationRepository.findByCinformation_Id(userId);
    }

    @Override
    @Transactional
    public void sendMessage(String ctitle, String cusername, String cusername1, String content, Integer receiverId) {
        Information info = new Information();
        info.setCtitle(ctitle);
        info.setCusername(cusername);
        info.setCusername1(cusername1);
        info.setCcontentChart(content);
        info.setIsDelete(false);
        info.setCcheck(false);
        info.setDatePublish(LocalDateTime.now());

        UserInfo receiver = userInfoRepository.findById(receiverId).orElse(null);
        info.setCinformation(receiver);

        informationRepository.save(info);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(String cusername, Integer receiverUserId) {
        List<Information> messages = informationRepository.findByCusername(cusername);
        for (Information msg : messages) {
            if (msg.getCinformation().getId().equals(receiverUserId)) {
                msg.setCcheck(true);
                informationRepository.save(msg);
            }
        }
    }

    @Override
    public List<UserInfo> findAllUsers() {
        return userInfoRepository.findAll();
    }

    private String sha1Encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 not available", e);
        }
    }

    private String saveUploadFile(MultipartFile file, String subDir) {
        try {
            Path dirPath = Paths.get(uploadPath, subDir);
            Files.createDirectories(dirPath);
            String filename = file.getOriginalFilename();
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());
            return subDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
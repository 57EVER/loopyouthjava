package com.loopyouth.service.impl;

import com.loopyouth.entity.*;
import com.loopyouth.repository.*;
import com.loopyouth.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final AuthUserRepository authUserRepo;
    private final UserInfoRepository userRepo;
    private final GoodsInfoRepository0 goodsRepo;
    private final OrderInfoRepository orderRepo;
    private final GoodsContentRepository commentRepo;

    public AdminServiceImpl(AuthUserRepository authUserRepo,
                            UserInfoRepository userRepo,
                            GoodsInfoRepository0 goodsRepo,
                            OrderInfoRepository orderRepo,
                            GoodsContentRepository commentRepo) {
        this.authUserRepo = authUserRepo;
        this.userRepo = userRepo;
        this.goodsRepo = goodsRepo;
        this.orderRepo = orderRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    @Transactional
    public AuthUser login(String username, String password) {
        AuthUser admin = authUserRepo.findByUsername(username).orElse(null);
        if (admin == null) return null;
        if (!Boolean.TRUE.equals(admin.getIsActive())) return null;
        if (!Boolean.TRUE.equals(admin.getIsSuperuser()) && !Boolean.TRUE.equals(admin.getIsStaff())) return null;
        if (!verifyDjangoPassword(password, admin.getPassword())) return null;
        admin.setLastLogin(LocalDateTime.now());
        authUserRepo.save(admin);
        return admin;
    }

    private boolean verifyDjangoPassword(String rawPassword, String encoded) {
        // Django format: pbkdf2_sha256$<iterations>$<salt>$<b64hash>
        String[] parts = encoded.split("\\$");
        if (parts.length != 4 || !"pbkdf2_sha256".equals(parts[0])) return false;
        try {
            int iterations = Integer.parseInt(parts[1]);
            String salt = parts[2];
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
            int keyLength = expectedHash.length * 8;

            KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt.getBytes("UTF-8"), iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] computedHash = factory.generateSecret(spec).getEncoded();

            if (computedHash.length != expectedHash.length) return false;
            int diff = 0;
            for (int i = 0; i < computedHash.length; i++) {
                diff |= computedHash[i] ^ expectedHash[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userRepo.count());
        stats.put("goodsCount", goodsRepo.count());
        stats.put("orderCount", orderRepo.count());
        stats.put("commentCount", commentRepo.count());
        return stats;
    }

    @Override
    public Page<UserInfo> getUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isBlank()) {
            return userRepo.adminSearch(keyword.trim(), pageable);
        }
        return userRepo.findAllByOrderByIdDesc(pageable);
    }

    @Override
    @Transactional
    public void toggleUserBan(Integer userId) {
        UserInfo user = userRepo.findById(userId).orElseThrow();
        user.setUnamePassOrfail(!user.getUnamePassOrfail());
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void toggleUserVerify(Integer userId) {
        UserInfo user = userRepo.findById(userId).orElseThrow();
        user.setUcheckPassOrfail(!user.getUcheckPassOrfail());
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userRepo.deleteById(userId);
    }

    @Override
    public Page<GoodsInfo> getGoods(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isBlank()) {
            return goodsRepo.adminSearch(keyword.trim(), pageable);
        }
        return goodsRepo.findAllByOrderByIdDesc(pageable);
    }

    @Override
    @Transactional
    public void deleteGoods(Integer goodsId) {
        GoodsInfo goods = goodsRepo.findById(goodsId).orElseThrow();
        goods.setIsDelete(true);
        goodsRepo.save(goods);
    }

    @Override
    public Page<OrderInfo> getOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepo.findAllByOrderByOdateDesc(pageable);
    }

    @Override
    public Page<GoodsContent> getComments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "datePublish"));
        return commentRepo.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId) {
        GoodsContent comment = commentRepo.findById(commentId).orElseThrow();
        comment.setIsDelete(true);
        commentRepo.save(comment);
    }
}

package com.loopyouth.service;

import com.loopyouth.entity.GoodsBrowser;
import com.loopyouth.entity.Information;
import com.loopyouth.entity.UserInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserInfo register(String uname, String upwd, String uemail);

    UserInfo login(String uname, String upwd);

    UserInfo findById(Integer id);

    UserInfo findByUname(String uname);

    long countByUname(String uname);

    long countByUemail(String uemail);

    void updateAddress(Integer userId, String ushou, String uaddress, String uyoubian, String uphone);

    void updateProfile(Integer userId, String usex, String uage, String upersonInf, MultipartFile logo);

    void changePassword(Integer userId, String newPwd);

    List<GoodsBrowser> getBrowseHistory(Integer userId);

    void recordBrowse(Integer userId, Integer goodsId);

    List<Information> getAllInformation();

    List<Information> getInformationBySenderAndReceiver(String cusername1, String cusername);

    List<Object[]> getDistinctSenders(Integer userId);

    List<Information> getInformationByUserId(Integer userId);

    void sendMessage(String ctitle, String cusername, String cusername1, String content, Integer receiverId);

    void markMessagesAsRead(String cusername, Integer receiverUserId);

    List<UserInfo> findAllUsers();
}

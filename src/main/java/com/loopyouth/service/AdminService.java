package com.loopyouth.service;

import com.loopyouth.entity.*;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface AdminService {

    AuthUser login(String username, String password);

    Map<String, Object> getDashboardStats();

    Page<UserInfo> getUsers(int page, int size, String keyword);

    void toggleUserBan(Integer userId);

    void toggleUserVerify(Integer userId);

    void deleteUser(Integer userId);

    Page<GoodsInfo> getGoods(int page, int size, String keyword);

    void deleteGoods(Integer goodsId);

    Page<OrderInfo> getOrders(int page, int size);

    Page<GoodsContent> getComments(int page, int size);

    void deleteComment(Integer commentId);
}

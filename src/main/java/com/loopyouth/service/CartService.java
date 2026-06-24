package com.loopyouth.service;

import com.loopyouth.entity.CartInfo;

import java.util.List;

public interface CartService {

    List<CartInfo> findByUserId(Integer userId);

    long countByUserId(Integer userId);

    void addToCart(Integer userId, Integer goodsId, int count);

    void editCartCount(Integer cartId, int count);

    boolean deleteCart(Integer cartId);

    CartInfo findById(Integer cartId);
}

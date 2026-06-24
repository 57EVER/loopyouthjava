package com.loopyouth.service.impl;

import com.loopyouth.entity.CartInfo;
import com.loopyouth.entity.GoodsInfo;
import com.loopyouth.entity.UserInfo;
import com.loopyouth.repository.CartInfoRepository;
import com.loopyouth.repository.GoodsInfoRepository0;
import com.loopyouth.repository.UserInfoRepository;
import com.loopyouth.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartInfoRepository cartInfoRepository;
    private final UserInfoRepository userInfoRepository;
    private final GoodsInfoRepository0 goodsInfoRepository;

    @Override
    public List<CartInfo> findByUserId(Integer userId) {
        return cartInfoRepository.findByUser_Id(userId);
    }

    @Override
    public long countByUserId(Integer userId) {
        return cartInfoRepository.countByUser_Id(userId);
    }

    @Override
    @Transactional
    public void addToCart(Integer userId, Integer goodsId, int count) {
        CartInfo existing = cartInfoRepository.findByUser_IdAndGoods_Id(userId, goodsId).orElse(null);
        if (existing != null) {
            existing.setCount(existing.getCount() + count);
            cartInfoRepository.save(existing);
        } else {
            CartInfo cart = new CartInfo();
            UserInfo user = userInfoRepository.findById(userId).orElse(null);
            GoodsInfo goods = goodsInfoRepository.findById(goodsId).orElse(null);
            cart.setUser(user);
            cart.setGoods(goods);
            cart.setCount(count);
            cartInfoRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public void editCartCount(Integer cartId, int count) {
        CartInfo cart = cartInfoRepository.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setCount(count);
            cartInfoRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public boolean deleteCart(Integer cartId) {
        try {
            cartInfoRepository.deleteById(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public CartInfo findById(Integer cartId) {
        return cartInfoRepository.findById(cartId).orElse(null);
    }
}

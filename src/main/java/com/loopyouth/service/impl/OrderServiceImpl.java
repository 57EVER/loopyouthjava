package com.loopyouth.service.impl;

import com.loopyouth.entity.*;
import com.loopyouth.repository.*;
import com.loopyouth.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderInfoRepository orderInfoRepository;
    private final OrderDetailInfoRepository orderDetailInfoRepository;
    private final CartInfoRepository cartInfoRepository;
    private final GoodsInfoRepository0 goodsInfoRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public Page<OrderInfo> findOrdersByUserId(Integer userId, int page, int size) {
        return orderInfoRepository.findByUser_IdOrderByOdateDesc(userId, PageRequest.of(page - 1, size));
    }

    @Override
    @Transactional
    public String createOrder(Integer userId, String cartIds, BigDecimal total, String address) {
        UserInfo user = userInfoRepository.findById(userId).orElse(null);
        if (user == null) {
            return "USER_NOT_FOUND";
        }

        LocalDateTime now = LocalDateTime.now();
        String oid = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + userId;

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOid(oid);
        orderInfo.setUser(user);
        orderInfo.setOdate(now);
        orderInfo.setOIsPay(false);
        orderInfo.setOtotal(total);
        orderInfo.setOaddress(address);
        orderInfoRepository.save(orderInfo);

        String[] ids = cartIds.split(",");
        for (String cartIdStr : ids) {
            if (cartIdStr.isEmpty()) continue;

            Integer cartId = Integer.parseInt(cartIdStr.trim());
            CartInfo cart = cartInfoRepository.findById(cartId).orElse(null);
            if (cart == null) continue;

            GoodsInfo goods = cart.getGoods();

            if (cart.getCount() > goods.getGkucun()) {
                throw new RuntimeException("STOCK_NOT_ENOUGH");
            }

            goods.setGkucun(goods.getGkucun() - cart.getCount());
            goodsInfoRepository.save(goods);

            OrderDetailInfo detail = new OrderDetailInfo();
            detail.setOrder(orderInfo);
            detail.setGoods(goods);
            detail.setPrice(goods.getGprice());
            detail.setCount(cart.getCount());
            detail.setUsername(user.getUname());
            detail.setShopername(goods.getGunit());
            detail.setDatatime(now);
            orderDetailInfoRepository.save(detail);

            cartInfoRepository.delete(cart);
        }

        return "OK";
    }

    @Override
    public List<OrderDetailInfo> findAllOrderDetails() {
        return orderDetailInfoRepository.findAll();
    }

    @Override
    public List<OrderDetailInfo> findOrderDetailsBySeller(String shopername) {
        return orderDetailInfoRepository.findByShopernameOrderByDatatimeDesc(shopername);
    }
}

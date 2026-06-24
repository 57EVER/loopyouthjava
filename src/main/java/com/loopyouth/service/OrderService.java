package com.loopyouth.service;

import com.loopyouth.entity.OrderDetailInfo;
import com.loopyouth.entity.OrderInfo;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    Page<OrderInfo> findOrdersByUserId(Integer userId, int page, int size);

    String createOrder(Integer userId, String cartIds, BigDecimal total, String address);

    List<OrderDetailInfo> findAllOrderDetails();

    List<OrderDetailInfo> findOrderDetailsBySeller(String shopername);
}

package com.loopyouth.repository;

import com.loopyouth.entity.OrderDetailInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailInfoRepository extends JpaRepository<OrderDetailInfo, Integer> {

    List<OrderDetailInfo> findByShopernameOrderByDatatimeDesc(String shopername);

    List<OrderDetailInfo> findAll();
}

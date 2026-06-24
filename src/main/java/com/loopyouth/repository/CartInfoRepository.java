package com.loopyouth.repository;

import com.loopyouth.entity.CartInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartInfoRepository extends JpaRepository<CartInfo, Integer> {

    List<CartInfo> findByUser_Id(Integer userId);

    long countByUser_Id(Integer userId);

    Optional<CartInfo> findByUser_IdAndGoods_Id(Integer userId, Integer goodsId);
}

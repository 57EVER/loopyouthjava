package com.loopyouth.repository;

import com.loopyouth.entity.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, String> {

    Page<OrderInfo> findByUser_IdOrderByOdateDesc(Integer userId, Pageable pageable);

    Page<OrderInfo> findAllByOrderByOdateDesc(Pageable pageable);
}

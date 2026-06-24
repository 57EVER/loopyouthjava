package com.loopyouth.repository;

import com.loopyouth.entity.GoodsContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsContentRepository0 extends JpaRepository<GoodsContent, Integer> {

    Page<GoodsContent> findByCgoodsname_IdOrderByDatePublishDesc(Integer goodsId, Pageable pageable);

    long countByCgoodsname_Id(Integer goodsId);

    GoodsContent findFirstByCusername(String username);
}

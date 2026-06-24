package com.loopyouth.repository;

import com.loopyouth.entity.GoodsInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsInfoRepository0 extends JpaRepository<GoodsInfo, Integer> {

    @Query("SELECT g FROM GoodsInfo g WHERE g.gtype.id = :typeId ORDER BY g.id DESC")
    List<GoodsInfo> findTopByTypeLatest(@Param("typeId") Integer typeId, Pageable pageable);

    @Query("SELECT g FROM GoodsInfo g WHERE g.gtype.id = :typeId ORDER BY g.gclick DESC")
    List<GoodsInfo> findTopByTypeHottest(@Param("typeId") Integer typeId, Pageable pageable);

    Page<GoodsInfo> findByGtype_IdOrderByIdDesc(Integer typeId, Pageable pageable);

    Page<GoodsInfo> findByGtype_IdOrderByGpriceDesc(Integer typeId, Pageable pageable);

    Page<GoodsInfo> findByGtype_IdOrderByGclickDesc(Integer typeId, Pageable pageable);

    List<GoodsInfo> findByGunit(String sellerName);

    @Query("SELECT g FROM GoodsInfo g WHERE g.gtitle LIKE %:keyword% OR g.gcontent LIKE %:keyword% OR g.gjianjie LIKE %:keyword% ORDER BY g.gclick ASC")
    Page<GoodsInfo> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT g FROM GoodsInfo g ORDER BY g.gclick ASC")
    List<GoodsInfo> findRecommend(Pageable pageable);

    Page<GoodsInfo> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT g FROM GoodsInfo g WHERE g.gtitle LIKE %:keyword% OR g.gunit LIKE %:keyword% ORDER BY g.id DESC")
    Page<GoodsInfo> adminSearch(@Param("keyword") String keyword, Pageable pageable);
}

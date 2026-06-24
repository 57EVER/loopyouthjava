package com.loopyouth.repository;

import com.loopyouth.entity.GoodsInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsInfoRepository extends JpaRepository<GoodsInfo, Integer> {

    // ========== 前台查询：严格加上 AND g.isDelete = false ==========

    @Query("SELECT g FROM GoodsInfo g WHERE g.gtype.id = :typeId AND g.isDelete = false ORDER BY g.id DESC")
    List<GoodsInfo> findTopByTypeLatest(@Param("typeId") Integer typeId, Pageable pageable);

    @Query("SELECT g FROM GoodsInfo g WHERE g.gtype.id = :typeId AND g.isDelete = false ORDER BY g.gclick DESC")
    List<GoodsInfo> findTopByTypeHottest(@Param("typeId") Integer typeId, Pageable pageable);

    // JPA 衍生方法加上 AndIsDeleteFalse
    Page<GoodsInfo> findByGtype_IdAndIsDeleteFalseOrderByIdDesc(Integer typeId, Pageable pageable);

    Page<GoodsInfo> findByGtype_IdAndIsDeleteFalseOrderByGpriceDesc(Integer typeId, Pageable pageable);

    Page<GoodsInfo> findByGtype_IdAndIsDeleteFalseOrderByGclickDesc(Integer typeId, Pageable pageable);

    List<GoodsInfo> findByGunitAndIsDeleteFalse(String sellerName);

    // 搜索：一定要用括号把 OR 括起来，再与 isDelete 进行 AND 运算
    @Query("SELECT g FROM GoodsInfo g WHERE (g.gtitle LIKE %:keyword% OR g.gcontent LIKE %:keyword% OR g.gjianjie LIKE %:keyword%) AND g.isDelete = false ORDER BY g.gclick ASC")
    Page<GoodsInfo> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT g FROM GoodsInfo g WHERE g.isDelete = false ORDER BY g.gclick ASC")
    List<GoodsInfo> findRecommend(Pageable pageable);


    // ========== 后台查询：保留原样，允许查出已删除的商品 ==========

    Page<GoodsInfo> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT g FROM GoodsInfo g WHERE g.gtitle LIKE %:keyword% OR g.gunit LIKE %:keyword% ORDER BY g.id DESC")
    Page<GoodsInfo> adminSearch(@Param("keyword") String keyword, Pageable pageable);
}

package com.loopyouth.repository;

import com.loopyouth.entity.GoodsContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsContentRepository extends JpaRepository<GoodsContent, Integer> {

    // 🔥 修复1：前台获取评论列表时，必须加上 AndIsDeleteFalse
    Page<GoodsContent> findByCgoodsname_IdAndIsDeleteFalseOrderByDatePublishDesc(Integer goodsId, Pageable pageable);

    // 🔥 修复2：如果前台有显示“评论总数”，统计时也要排除已删除的
    long countByCgoodsname_IdAndIsDeleteFalse(Integer goodsId);

    // 🔥 修复3：按用户名查找时排除已删除的
    GoodsContent findFirstByCusernameAndIsDeleteFalse(String username);

    // （可选）如果你后台管理系统需要查出所有评论（包含已删除的），可以补充一个不带条件的方法给后台专用
    Page<GoodsContent> findAllByOrderByDatePublishDesc(Pageable pageable);
}
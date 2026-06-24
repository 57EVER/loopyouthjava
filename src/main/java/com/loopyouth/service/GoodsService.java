package com.loopyouth.service;

import com.loopyouth.entity.GoodsContent;
import com.loopyouth.entity.GoodsInfo;
import com.loopyouth.entity.TypeInfo;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GoodsService {

    List<TypeInfo> findAllTypes();

    List<GoodsInfo> findLatestByType(Integer typeId, int limit);

    List<GoodsInfo> findHottestByType(Integer typeId, int limit);

    Page<GoodsInfo> findByTypeWithSort(Integer typeId, String sort, int page, int size);

    GoodsInfo findGoodsById(Integer id);

    void incrementClick(Integer goodsId);

    Page<GoodsContent> findCommentsByGoodsId(Integer goodsId, int page, int size);

    void addComment(String ctitle, MultipartFile cpic, String cusername, String clogo, String content, Integer goodsId);

    Page<GoodsInfo> searchGoods(String keyword, int page, int size);

    List<GoodsInfo> getRecommendGoods(int limit);

    void publishGoods(String gtitle, MultipartFile gpic, String gunit, String gprice,
                      String gjianjie, String gkucun, String gcontent, Integer gtypeId);

    List<GoodsInfo> findBySeller(String sellerName);
}

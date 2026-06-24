package com.loopyouth.service.impl;

import com.loopyouth.entity.GoodsContent;
import com.loopyouth.entity.GoodsInfo;
import com.loopyouth.entity.TypeInfo;
import com.loopyouth.repository.GoodsContentRepository;
import com.loopyouth.repository.GoodsInfoRepository;
import com.loopyouth.repository.TypeInfoRepository;
import com.loopyouth.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final TypeInfoRepository typeInfoRepository;
    private final GoodsInfoRepository goodsInfoRepository;
    private final GoodsContentRepository goodsContentRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public List<TypeInfo> findAllTypes() {
        return typeInfoRepository.findAll();
    }

    @Override
    public List<GoodsInfo> findLatestByType(Integer typeId, int limit) {
        return goodsInfoRepository.findTopByTypeLatest(typeId, PageRequest.of(0, limit));
    }

    @Override
    public List<GoodsInfo> findHottestByType(Integer typeId, int limit) {
        return goodsInfoRepository.findTopByTypeHottest(typeId, PageRequest.of(0, limit));
    }

    @Override
    public Page<GoodsInfo> findByTypeWithSort(Integer typeId, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return switch (sort) {
            // 调用更新后的带 AndIsDeleteFalse 的方法
            case "2" -> goodsInfoRepository.findByGtype_IdAndIsDeleteFalseOrderByGpriceDesc(typeId, pageable);
            case "3" -> goodsInfoRepository.findByGtype_IdAndIsDeleteFalseOrderByGclickDesc(typeId, pageable);
            default -> goodsInfoRepository.findByGtype_IdAndIsDeleteFalseOrderByIdDesc(typeId, pageable);
        };
    }

    @Override
    public GoodsInfo findGoodsById(Integer id) {
        return goodsInfoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void incrementClick(Integer goodsId) {
        GoodsInfo goods = goodsInfoRepository.findById(goodsId).orElse(null);
        if (goods != null) {
            goods.setGclick(goods.getGclick() + 1);
            goodsInfoRepository.save(goods);
        }
    }

    @Override
    public Page<GoodsContent> findCommentsByGoodsId(Integer goodsId, int page, int size) {
        // 🔥 修改这里：把原来的方法名替换为带 AndIsDeleteFalse 的新方法名
        return goodsContentRepository.findByCgoodsname_IdAndIsDeleteFalseOrderByDatePublishDesc(goodsId, PageRequest.of(page - 1, size));
    }

    @Override
    @Transactional
    public void addComment(String ctitle, MultipartFile cpic, String cusername, String clogo, String content, Integer goodsId) {
        GoodsContent comment = new GoodsContent();
        comment.setCtitle(ctitle);
        comment.setCusername(cusername);
        comment.setClogo(clogo);
        comment.setCuserContent(content);
        comment.setDatePublish(LocalDateTime.now());
        comment.setIsDelete(false);

        GoodsInfo goods = goodsInfoRepository.findById(goodsId).orElse(null);
        comment.setCgoodsname(goods);

        if (cpic != null && !cpic.isEmpty()) {
            comment.setCpic(saveUploadFile(cpic, "df_goods/image"));
        }

        goodsContentRepository.save(comment);
    }

    @Override
    public Page<GoodsInfo> searchGoods(String keyword, int page, int size) {
        return goodsInfoRepository.searchByKeyword(keyword, PageRequest.of(page - 1, size));
    }

    @Override
    public List<GoodsInfo> getRecommendGoods(int limit) {
        return goodsInfoRepository.findRecommend(PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public void publishGoods(String gtitle, MultipartFile gpic, String gunit, String gprice,
                             String gjianjie, String gkucun, String gcontent, Integer gtypeId) {
        GoodsInfo goods = new GoodsInfo();
        goods.setGtitle(gtitle);
        goods.setGunit(gunit);
        goods.setGprice(new BigDecimal(gprice));
        goods.setGjianjie(gjianjie);
        goods.setGkucun(Integer.parseInt(gkucun));
        goods.setGcontent(gcontent);
        goods.setIsDelete(false);
        goods.setGclick(0);

        TypeInfo type = typeInfoRepository.findById(gtypeId).orElse(null);
        goods.setGtype(type);

        if (gpic != null && !gpic.isEmpty()) {
            goods.setGpic(saveUploadFile(gpic, "df_goods/image"));
        }

        goodsInfoRepository.save(goods);
    }

    @Override
    public List<GoodsInfo> findBySeller(String sellerName) {
        // 调用更新后的带 AndIsDeleteFalse 的方法
        return goodsInfoRepository.findByGunitAndIsDeleteFalse(sellerName);
    }

    private String saveUploadFile(MultipartFile file, String subDir) {
        try {
            LocalDate now = LocalDate.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String dir = subDir + "/" + datePath;
            Path dirPath = Paths.get(uploadPath, dir);
            Files.createDirectories(dirPath);

            String filename = file.getOriginalFilename();
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toAbsolutePath().toFile());

            return dir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "df_goods_goodsinfo")
@Getter
@Setter
@NoArgsConstructor
public class GoodsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "gtitle", length = 20, nullable = false, unique = true)
    private String gtitle;

    @Column(name = "gpic", length = 100)
    private String gpic;

    @Column(name = "gprice", precision = 7, scale = 2, nullable = false)
    private BigDecimal gprice;

    @Column(name = "gunit", length = 20, nullable = false)
    private String gunit;

    @Column(name = "gclick", nullable = false)
    private Integer gclick = 0;

    @Column(name = "gjianjie", length = 200, nullable = false)
    private String gjianjie;

    @Column(name = "gkucun", nullable = false)
    private Integer gkucun = 0;

    @Column(name = "gcontent", columnDefinition = "longtext", nullable = false)
    private String gcontent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gtype_id", nullable = false)
    private TypeInfo gtype;

    @OneToMany(mappedBy = "cgoodsname", fetch = FetchType.LAZY)
    private List<GoodsContent> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "goods", fetch = FetchType.LAZY)
    private List<CartInfo> cartList = new ArrayList<>();

    @OneToMany(mappedBy = "good", fetch = FetchType.LAZY)
    private List<GoodsBrowser> browserList = new ArrayList<>();

    @OneToMany(mappedBy = "goods", fetch = FetchType.LAZY)
    private List<OrderDetailInfo> orderDetailList = new ArrayList<>();
}

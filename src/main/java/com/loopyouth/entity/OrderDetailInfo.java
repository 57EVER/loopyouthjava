package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "df_order_orderdetailinfo")
@Getter
@Setter
@NoArgsConstructor
public class OrderDetailInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id", nullable = false)
    private GoodsInfo goods;

    @Column(name = "username", length = 20, nullable = false)
    private String username;

    @Column(name = "shopername", length = 20, nullable = false)
    private String shopername = "";

    @Column(name = "datatime", nullable = false)
    private LocalDateTime datatime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderInfo order;

    @Column(name = "price", precision = 6, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "count", nullable = false)
    private Integer count;

    @PrePersist
    public void prePersist() {
        if (datatime == null) {
            datatime = LocalDateTime.now();
        }
    }
}

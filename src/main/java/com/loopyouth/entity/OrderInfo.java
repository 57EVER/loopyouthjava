package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "df_order_orderinfo")
@Getter
@Setter
@NoArgsConstructor
public class OrderInfo {

    @Id
    @Column(name = "oid", length = 20)
    private String oid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @Column(name = "odate", nullable = false)
    private LocalDateTime odate;

    @Column(name = "oIsPay", nullable = false)
    private Boolean oIsPay = false;

    @Column(name = "ototal", precision = 8, scale = 2, nullable = false)
    private BigDecimal ototal;

    @Column(name = "oaddress", length = 150, nullable = false)
    private String oaddress;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderDetailInfo> detailList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (odate == null) {
            odate = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        odate = LocalDateTime.now();
    }
}

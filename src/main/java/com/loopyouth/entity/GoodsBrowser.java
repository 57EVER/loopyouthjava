package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "df_user_goodsbrowser")
@Getter
@Setter
@NoArgsConstructor
public class GoodsBrowser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "browser_time", nullable = false)
    private LocalDateTime browserTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "good_id", nullable = false)
    private GoodsInfo good;

    @PrePersist
    public void prePersist() {
        if (browserTime == null) {
            browserTime = LocalDateTime.now();
        }
    }
}

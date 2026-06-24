package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "df_goods_contentchart")
@Getter
@Setter
@NoArgsConstructor
public class ContentChart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "ctitle", length = 20, nullable = false)
    private String ctitle;

    @Column(name = "cusername", length = 20, nullable = false)
    private String cusername;

    @Column(name = "cusername1", length = 20, nullable = false)
    private String cusername1;

    @Column(name = "ccontent_chart", columnDefinition = "longtext", nullable = false)
    private String ccontentChart;

    @Column(name = "date_publish", nullable = false)
    private LocalDateTime datePublish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ccontent_id", nullable = false)
    private GoodsContent ccontent;

    @PrePersist
    public void prePersist() {
        if (datePublish == null) {
            datePublish = LocalDateTime.now();
        }
    }
}

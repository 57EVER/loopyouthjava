package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "df_goods_goodscontent")
@Getter
@Setter
@NoArgsConstructor
public class GoodsContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "ctitle", length = 20, nullable = false)
    private String ctitle;

    @Column(name = "cpic", length = 100)
    private String cpic;

    @Column(name = "cusername", length = 20, nullable = false)
    private String cusername;

    @Column(name = "clogo", length = 200, nullable = false)
    private String clogo;

    @Column(name = "cuser_content", columnDefinition = "longtext", nullable = false)
    private String cuserContent;

    @Column(name = "date_publish", nullable = false)
    private LocalDateTime datePublish;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cgoodsname_id", nullable = false)
    private GoodsInfo cgoodsname;

    @OneToMany(mappedBy = "ccontent", fetch = FetchType.LAZY)
    private List<ContentChart> replyList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (datePublish == null) {
            datePublish = LocalDateTime.now();
        }
    }
}

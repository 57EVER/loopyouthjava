package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "df_goods_typeinfo")
@Getter
@Setter
@NoArgsConstructor
public class TypeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "isDelete", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "ttitle", length = 20, nullable = false)
    private String ttitle;

    @OneToMany(mappedBy = "gtype", fetch = FetchType.LAZY)
    private List<GoodsInfo> goodsList = new ArrayList<>();
}

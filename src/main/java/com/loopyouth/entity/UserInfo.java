package com.loopyouth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "df_user_userinfo")
@Getter
@Setter
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uname", length = 20, nullable = false, unique = true)
    private String uname;

    @Column(name = "usex", length = 10, nullable = false)
    private String usex = "";

    @Column(name = "uage", length = 10, nullable = false)
    private String uage = "";

    @Column(name = "upersonInf", length = 200, nullable = false)
    private String upersonInf = "";

    @Column(name = "ulogo", length = 100, nullable = false)
    private String ulogo = "default.jpg";

    @Column(name = "upwd", length = 40, nullable = false)
    private String upwd;

    @Column(name = "uemail", length = 254, nullable = false, unique = true)
    private String uemail;

    @Column(name = "urealname", length = 20, nullable = false)
    private String urealname = "";

    @Column(name = "uzhengjian_type", length = 20, nullable = false)
    private String uzhengjianType = "";

    @Column(name = "uzhengjian_tel", length = 18, nullable = false)
    private String uzhengjianTel = "";

    @Column(name = "uzhengjian_img", length = 100, nullable = false)
    private String uzhengjianImg = "";

    @Column(name = "ucheck_passOrfail", nullable = false)
    private Boolean ucheckPassOrfail = false;

    @Column(name = "ushou", length = 20, nullable = false)
    private String ushou = "";

    @Column(name = "uaddress", length = 100, nullable = false)
    private String uaddress = "";

    @Column(name = "uyoubian", length = 6, nullable = false)
    private String uyoubian = "";

    @Column(name = "uphone", length = 11, nullable = false)
    private String uphone = "";

    @Column(name = "uname_passOrfail", nullable = false)
    private Boolean unamePassOrfail = true;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<GoodsBrowser> browserList = new ArrayList<>();

    @OneToMany(mappedBy = "cinformation", fetch = FetchType.LAZY)
    private List<Information> informationList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<CartInfo> cartList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<OrderInfo> orderList = new ArrayList<>();
}

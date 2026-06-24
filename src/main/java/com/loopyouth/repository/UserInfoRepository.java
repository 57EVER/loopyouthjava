package com.loopyouth.repository;

import com.loopyouth.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    Optional<UserInfo> findByUname(String uname);

    Optional<UserInfo> findByUemail(String uemail);

    long countByUname(String uname);

    long countByUemail(String uemail);

    Page<UserInfo> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT u FROM UserInfo u WHERE u.uname LIKE %:keyword% OR u.uemail LIKE %:keyword% ORDER BY u.id DESC")
    Page<UserInfo> adminSearch(@Param("keyword") String keyword, Pageable pageable);
}

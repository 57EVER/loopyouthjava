package com.loopyouth.repository;

import com.loopyouth.entity.GoodsBrowser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsBrowserRepository extends JpaRepository<GoodsBrowser, Integer> {

    Optional<GoodsBrowser> findByUser_IdAndGood_Id(Integer userId, Integer goodId);

    List<GoodsBrowser> findByUser_IdOrderByBrowserTimeDesc(Integer userId);

    long countByUser_Id(Integer userId);
}

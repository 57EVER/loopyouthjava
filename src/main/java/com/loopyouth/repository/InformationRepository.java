package com.loopyouth.repository;

import com.loopyouth.entity.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InformationRepository extends JpaRepository<Information, Integer> {

    List<Information> findAll();

    List<Information> findByCusername1AndCusername(String cusername1, String cusername);

    @Query("SELECT DISTINCT i.cusername, i.ccheck FROM Information i WHERE i.cinformation.id = :userId ORDER BY i.cusername")
    List<Object[]> findDistinctSendersByUserId(@Param("userId") Integer userId);

    List<Information> findByCinformation_Id(Integer userId);

    List<Information> findByCusername(String cusername);
}

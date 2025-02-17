package com.oms.dao;

import com.oms.pojo.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkuDao extends JpaRepository<SKU, Long> {
    List<SKU> findBySkuNameStartingWith(String name);
}
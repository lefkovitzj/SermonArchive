package com.lefkovitzj.sermonarchive.repository;

import com.lefkovitzj.sermonarchive.entity.Church;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChurchRepository extends JpaRepository<Church, Integer> {
    List<Church> findByNameContaining(String username);
    Church findByName(String username);
    boolean existsByName(String username);
}

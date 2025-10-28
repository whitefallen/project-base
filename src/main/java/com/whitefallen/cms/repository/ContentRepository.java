package com.whitefallen.cms.repository;

import com.whitefallen.cms.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    
    List<Content> findByTitleContainingIgnoreCase(String title);
}

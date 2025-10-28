package com.whitefallen.cms.repository;

import com.whitefallen.cms.model.Content;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContentRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ContentRepository contentRepository;
    
    @Test
    void whenSaveContent_thenContentIsPersisted() {
        // Given
        Content content = new Content("Test Title", "Test Body");
        
        // When
        Content saved = contentRepository.save(content);
        
        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Title");
        assertThat(saved.getBody()).isEqualTo("Test Body");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
    
    @Test
    void whenFindById_thenReturnContent() {
        // Given
        Content content = new Content("Test Title", "Test Body");
        Content saved = entityManager.persistAndFlush(content);
        
        // When
        Optional<Content> found = contentRepository.findById(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Title");
    }
    
    @Test
    void whenFindAll_thenReturnAllContent() {
        // Given
        entityManager.persist(new Content("Title 1", "Body 1"));
        entityManager.persist(new Content("Title 2", "Body 2"));
        entityManager.flush();
        
        // When
        List<Content> allContent = contentRepository.findAll();
        
        // Then
        assertThat(allContent).hasSize(2);
    }
    
    @Test
    void whenFindByTitleContaining_thenReturnMatchingContent() {
        // Given
        entityManager.persist(new Content("Spring Boot Tutorial", "Body 1"));
        entityManager.persist(new Content("Java Tutorial", "Body 2"));
        entityManager.persist(new Content("Spring Security Guide", "Body 3"));
        entityManager.flush();
        
        // When
        List<Content> found = contentRepository.findByTitleContainingIgnoreCase("spring");
        
        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Content::getTitle)
            .containsExactlyInAnyOrder("Spring Boot Tutorial", "Spring Security Guide");
    }
    
    @Test
    void whenDeleteContent_thenContentIsRemoved() {
        // Given
        Content content = new Content("Test Title", "Test Body");
        Content saved = entityManager.persistAndFlush(content);
        
        // When
        contentRepository.deleteById(saved.getId());
        
        // Then
        Optional<Content> found = contentRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}

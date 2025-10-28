package com.whitefallen.cms.service;

import com.whitefallen.cms.dto.ContentRequest;
import com.whitefallen.cms.dto.ContentResponse;
import com.whitefallen.cms.model.Content;
import com.whitefallen.cms.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {
    
    @Mock
    private ContentRepository contentRepository;
    
    @InjectMocks
    private ContentService contentService;
    
    private Content testContent;
    
    @BeforeEach
    void setUp() {
        testContent = new Content("Test Title", "Test Body");
        testContent.setId(1L);
        testContent.setCreatedAt(LocalDateTime.now());
        testContent.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void whenGetAllContent_thenReturnAllContent() {
        // Given
        List<Content> contentList = Arrays.asList(testContent);
        when(contentRepository.findAll()).thenReturn(contentList);
        
        // When
        List<ContentResponse> result = contentService.getAllContent();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Title");
        verify(contentRepository, times(1)).findAll();
    }
    
    @Test
    void whenGetContentById_thenReturnContent() {
        // Given
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        
        // When
        ContentResponse result = contentService.getContentById(1L);
        
        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        verify(contentRepository, times(1)).findById(1L);
    }
    
    @Test
    void whenGetContentByIdNotFound_thenThrowException() {
        // Given
        when(contentRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> contentService.getContentById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Content not found with id: 999");
    }
    
    @Test
    void whenCreateContent_thenReturnCreatedContent() {
        // Given
        ContentRequest request = new ContentRequest("New Title", "New Body");
        when(contentRepository.save(any(Content.class))).thenReturn(testContent);
        
        // When
        ContentResponse result = contentService.createContent(request);
        
        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        verify(contentRepository, times(1)).save(any(Content.class));
    }
    
    @Test
    void whenUpdateContent_thenReturnUpdatedContent() {
        // Given
        ContentRequest request = new ContentRequest("Updated Title", "Updated Body");
        Content updatedContent = new Content("Updated Title", "Updated Body");
        updatedContent.setId(1L);
        
        when(contentRepository.findById(1L)).thenReturn(Optional.of(testContent));
        when(contentRepository.save(any(Content.class))).thenReturn(updatedContent);
        
        // When
        ContentResponse result = contentService.updateContent(1L, request);
        
        // Then
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getBody()).isEqualTo("Updated Body");
        verify(contentRepository, times(1)).findById(1L);
        verify(contentRepository, times(1)).save(any(Content.class));
    }
    
    @Test
    void whenUpdateContentNotFound_thenThrowException() {
        // Given
        ContentRequest request = new ContentRequest("Updated Title", "Updated Body");
        when(contentRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> contentService.updateContent(999L, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Content not found with id: 999");
    }
    
    @Test
    void whenDeleteContent_thenContentIsDeleted() {
        // Given
        when(contentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(contentRepository).deleteById(1L);
        
        // When
        contentService.deleteContent(1L);
        
        // Then
        verify(contentRepository, times(1)).existsById(1L);
        verify(contentRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void whenDeleteContentNotFound_thenThrowException() {
        // Given
        when(contentRepository.existsById(anyLong())).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> contentService.deleteContent(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Content not found with id: 999");
    }
    
    @Test
    void whenSearchContent_thenReturnMatchingContent() {
        // Given
        List<Content> contentList = Arrays.asList(testContent);
        when(contentRepository.findByTitleContainingIgnoreCase("test")).thenReturn(contentList);
        
        // When
        List<ContentResponse> result = contentService.searchContent("test");
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Title");
        verify(contentRepository, times(1)).findByTitleContainingIgnoreCase("test");
    }
}

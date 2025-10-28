package com.whitefallen.cms.service;

import com.whitefallen.cms.dto.ContentRequest;
import com.whitefallen.cms.dto.ContentResponse;
import com.whitefallen.cms.model.Content;
import com.whitefallen.cms.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContentService {
    
    private final ContentRepository contentRepository;
    
    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }
    
    public List<ContentResponse> getAllContent() {
        return contentRepository.findAll().stream()
            .map(ContentResponse::fromContent)
            .collect(Collectors.toList());
    }
    
    public ContentResponse getContentById(Long id) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        return ContentResponse.fromContent(content);
    }
    
    public ContentResponse createContent(ContentRequest request) {
        Content content = new Content(request.getTitle(), request.getBody());
        Content savedContent = contentRepository.save(content);
        return ContentResponse.fromContent(savedContent);
    }
    
    public ContentResponse updateContent(Long id, ContentRequest request) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        
        content.setTitle(request.getTitle());
        content.setBody(request.getBody());
        
        Content updatedContent = contentRepository.save(content);
        return ContentResponse.fromContent(updatedContent);
    }
    
    public void deleteContent(Long id) {
        if (!contentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Content not found with id: " + id);
        }
        contentRepository.deleteById(id);
    }
    
    public List<ContentResponse> searchContent(String title) {
        return contentRepository.findByTitleContainingIgnoreCase(title).stream()
            .map(ContentResponse::fromContent)
            .collect(Collectors.toList());
    }
}

package com.whitefallen.cms.controller;

import com.whitefallen.cms.dto.ContentRequest;
import com.whitefallen.cms.dto.ContentResponse;
import com.whitefallen.cms.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {
    
    private final ContentService contentService;
    
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }
    
    @GetMapping
    public ResponseEntity<List<ContentResponse>> getAllContent(
            @RequestParam(required = false) String search) {
        
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(contentService.searchContent(search));
        }
        return ResponseEntity.ok(contentService.getAllContent());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getContentById(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }
    
    @PostMapping
    public ResponseEntity<ContentResponse> createContent(@Valid @RequestBody ContentRequest request) {
        ContentResponse created = contentService.createContent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody ContentRequest request) {
        ContentResponse updated = contentService.updateContent(id, request);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}

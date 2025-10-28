package com.whitefallen.cms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whitefallen.cms.dto.ContentRequest;
import com.whitefallen.cms.dto.ContentResponse;
import com.whitefallen.cms.service.ContentService;
import com.whitefallen.cms.service.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentController.class)
class ContentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ContentService contentService;
    
    private ContentResponse testContentResponse;
    
    @BeforeEach
    void setUp() {
        testContentResponse = new ContentResponse(
            1L,
            "Test Title",
            "Test Body",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
    
    @Test
    void whenGetAllContent_thenReturnContentList() throws Exception {
        // Given
        List<ContentResponse> contentList = Arrays.asList(testContentResponse);
        when(contentService.getAllContent()).thenReturn(contentList);
        
        // When & Then
        mockMvc.perform(get("/api/content")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Title")))
                .andExpect(jsonPath("$[0].body", is("Test Body")));
        
        verify(contentService, times(1)).getAllContent();
    }
    
    @Test
    void whenGetContentById_thenReturnContent() throws Exception {
        // Given
        when(contentService.getContentById(1L)).thenReturn(testContentResponse);
        
        // When & Then
        mockMvc.perform(get("/api/content/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Title")))
                .andExpect(jsonPath("$.body", is("Test Body")));
        
        verify(contentService, times(1)).getContentById(1L);
    }
    
    @Test
    void whenGetContentByIdNotFound_thenReturn404() throws Exception {
        // Given
        when(contentService.getContentById(999L))
            .thenThrow(new ResourceNotFoundException("Content not found with id: 999"));
        
        // When & Then
        mockMvc.perform(get("/api/content/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Content not found with id: 999")));
    }
    
    @Test
    void whenCreateContent_thenReturnCreatedContent() throws Exception {
        // Given
        ContentRequest request = new ContentRequest("New Title", "New Body");
        when(contentService.createContent(any(ContentRequest.class))).thenReturn(testContentResponse);
        
        // When & Then
        mockMvc.perform(post("/api/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Title")))
                .andExpect(jsonPath("$.body", is("Test Body")));
        
        verify(contentService, times(1)).createContent(any(ContentRequest.class));
    }
    
    @Test
    void whenCreateContentWithInvalidData_thenReturn400() throws Exception {
        // Given
        ContentRequest request = new ContentRequest("", ""); // Invalid: empty fields
        
        // When & Then
        mockMvc.perform(post("/api/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
        
        verify(contentService, never()).createContent(any(ContentRequest.class));
    }
    
    @Test
    void whenUpdateContent_thenReturnUpdatedContent() throws Exception {
        // Given
        ContentRequest request = new ContentRequest("Updated Title", "Updated Body");
        ContentResponse updatedResponse = new ContentResponse(
            1L,
            "Updated Title",
            "Updated Body",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(contentService.updateContent(eq(1L), any(ContentRequest.class))).thenReturn(updatedResponse);
        
        // When & Then
        mockMvc.perform(put("/api/content/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.body", is("Updated Body")));
        
        verify(contentService, times(1)).updateContent(eq(1L), any(ContentRequest.class));
    }
    
    @Test
    void whenDeleteContent_thenReturnNoContent() throws Exception {
        // Given
        doNothing().when(contentService).deleteContent(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/content/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        verify(contentService, times(1)).deleteContent(1L);
    }
    
    @Test
    void whenSearchContent_thenReturnMatchingContent() throws Exception {
        // Given
        List<ContentResponse> contentList = Arrays.asList(testContentResponse);
        when(contentService.searchContent("test")).thenReturn(contentList);
        
        // When & Then
        mockMvc.perform(get("/api/content")
                .param("search", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Title")));
        
        verify(contentService, times(1)).searchContent("test");
        verify(contentService, never()).getAllContent();
    }
}

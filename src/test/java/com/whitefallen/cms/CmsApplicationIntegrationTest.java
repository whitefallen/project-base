package com.whitefallen.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whitefallen.cms.dto.ContentRequest;
import com.whitefallen.cms.dto.ContentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CmsApplicationIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void contextLoads() {
        assertThat(mockMvc).isNotNull();
    }
    
    @Test
    void testCompleteContentLifecycle() throws Exception {
        // Create content
        ContentRequest createRequest = new ContentRequest("Integration Test Title", "Integration Test Body");
        
        MvcResult createResult = mockMvc.perform(post("/api/content")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Integration Test Title")))
                .andExpect(jsonPath("$.body", is("Integration Test Body")))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();
        
        String responseBody = createResult.getResponse().getContentAsString();
        ContentResponse createdContent = objectMapper.readValue(responseBody, ContentResponse.class);
        Long contentId = createdContent.getId();
        
        // Get content by ID
        mockMvc.perform(get("/api/content/" + contentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(contentId.intValue())))
                .andExpect(jsonPath("$.title", is("Integration Test Title")));
        
        // Update content
        ContentRequest updateRequest = new ContentRequest("Updated Title", "Updated Body");
        
        mockMvc.perform(put("/api/content/" + contentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(contentId.intValue())))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.body", is("Updated Body")));
        
        // Get all content - should have at least one
        mockMvc.perform(get("/api/content")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        
        // Search content
        mockMvc.perform(get("/api/content")
                .param("search", "Updated")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Updated Title")));
        
        // Delete content
        mockMvc.perform(delete("/api/content/" + contentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        
        // Verify deletion - should return 404
        mockMvc.perform(get("/api/content/" + contentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

package com.aiops.aiops_backend.messages;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessagesControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessagesRepository repository;

    @BeforeEach
    void clearMessages() {
        repository.deleteAll();
    }

    @Test
    void createListUpdatePatchAndDeleteMessage() throws Exception {
        String location = mvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"USER","content":" Hello "}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.content", is("Hello")))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("Hello")));

        mvc.perform(get("/api/messages?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].role", is("USER")));

        mvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"ASSISTANT","content":"Replacement"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("ASSISTANT")))
                .andExpect(jsonPath("$.content", is("Replacement")));

        mvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"content":"Partial update"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("ASSISTANT")))
                .andExpect(jsonPath("$.content", is("Partial update")));

        mvc.perform(delete(location)).andExpect(status().isNoContent());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidRequestsAndMissingMessages() throws Exception {
        mvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"SYSTEM","content":"Hello"}
                                """))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"USER","content":" "}
                                """))
                .andExpect(status().isBadRequest());

        mvc.perform(patch("/api/messages/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/api/messages/999999"))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/messages?page=-1"))
                .andExpect(status().isBadRequest());
    }
}

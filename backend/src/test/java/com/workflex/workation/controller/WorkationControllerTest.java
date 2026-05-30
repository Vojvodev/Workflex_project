package com.workflex.workation.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.workflex.workation.repository.WorkationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkationRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void listIsEmptyBeforeImport() throws Exception {
        mockMvc.perform(get("/workflex/workation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void importThenListReturnsWorkations() throws Exception {
        mockMvc.perform(post("/workflex/workation/import"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported").value(5));

        mockMvc.perform(get("/workflex/workation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[?(@.workationId == 'w1')].employee").value("Steffen Jacobs"))
                .andExpect(jsonPath("$[?(@.workationId == 'w1')].start").value("2024-01-02"));
    }
}

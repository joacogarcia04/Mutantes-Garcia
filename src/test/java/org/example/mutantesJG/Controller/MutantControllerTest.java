package org.example.mutantesJG.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mutantesJG.controller.MutantController;
import org.example.mutantesJG.dto.DnaRequest;
import org.example.mutantesJG.dto.StatsResponse;
import org.example.mutantesJG.service.MutantService;
import org.example.mutantesJG.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MutantController.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /mutant retorna 200 OK si es mutante")
    void testCheckMutant_ReturnOk() throws Exception {
        DnaRequest request = new DnaRequest();
        request.setDna(new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"});

        when(mutantService.analyzeDna(any())).thenReturn(true);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant retorna 403 Forbidden si es humano")
    void testCheckMutant_ReturnForbidden() throws Exception {
        DnaRequest request = new DnaRequest();
        request.setDna(new String[]{"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"});

        when(mutantService.analyzeDna(any())).thenReturn(false);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /mutant retorna 400 Bad Request si el ADN es inválido")
    void testCheckMutant_ReturnBadRequest() throws Exception {
        DnaRequest request = new DnaRequest();
        request.setDna(new String[]{}); // Array vacío, debería fallar la validación @NotEmpty

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /stats retorna estadísticas correctamente")
    void testGetStats_ReturnOk() throws Exception {
        StatsResponse mockResponse = new StatsResponse(40L, 100L, 0.4);
        when(statsService.getStats()).thenReturn(mockResponse);

        mockMvc.perform(get("/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }
}
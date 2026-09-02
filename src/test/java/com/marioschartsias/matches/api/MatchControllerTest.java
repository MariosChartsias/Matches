package com.marioschartsias.matches.api;

import com.marioschartsias.matches.api.dto.MatchResponse;
import com.marioschartsias.matches.api.error.GlobalExceptionHandler;
import com.marioschartsias.matches.domain.Sport;
import com.marioschartsias.matches.exception.ResourceNotFoundException;
import com.marioschartsias.matches.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MatchControllerTest {

    @Mock
    private MatchService matchService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MatchController(matchService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createsAMatch() throws Exception {
        MatchResponse response = new MatchResponse(
                1L,
                "OSFP - PAO",
                LocalDate.of(2026, 9, 10),
                LocalTime.of(20, 30),
                "OSFP",
                "PAO",
                Sport.FOOTBALL,
                List.of()
        );
        when(matchService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/matches/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sport").value("FOOTBALL"));
    }

    @Test
    void returnsValidationDetails() throws Exception {
        String request = """
                {
                  "description": "",
                  "matchDate": "2026-09-10",
                  "matchTime": "20:30:00",
                  "teamA": "OSFP",
                  "teamB": "OSFP",
                  "sport": "FOOTBALL"
                }
                """;

        mockMvc.perform(post("/api/v1/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.description").exists())
                .andExpect(jsonPath("$.validationErrors.teamPairValid").exists());

        verifyNoInteractions(matchService);
    }

    @Test
    void returnsNotFoundForAnUnknownMatch() throws Exception {
        when(matchService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Match with id 99 was not found"));

        mockMvc.perform(get("/api/v1/matches/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Match with id 99 was not found"))
                .andExpect(jsonPath("$.path").value("/api/v1/matches/99"));
    }

    private String validRequest() {
        return """
                {
                  "description": "OSFP - PAO",
                  "matchDate": "2026-09-10",
                  "matchTime": "20:30:00",
                  "teamA": "OSFP",
                  "teamB": "PAO",
                  "sport": "FOOTBALL"
                }
                """;
    }
}


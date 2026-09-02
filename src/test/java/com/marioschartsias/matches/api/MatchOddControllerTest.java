package com.marioschartsias.matches.api;

import com.marioschartsias.matches.api.dto.MatchOddResponse;
import com.marioschartsias.matches.api.error.GlobalExceptionHandler;
import com.marioschartsias.matches.exception.DuplicateResourceException;
import com.marioschartsias.matches.service.MatchOddService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MatchOddControllerTest {

    @Mock
    private MatchOddService matchOddService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MatchOddController(matchOddService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createsAnOddForAMatch() throws Exception {
        MatchOddResponse response = new MatchOddResponse(3L, 1L, "X", new BigDecimal("1.500"));
        when(matchOddService.create(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/matches/1/odds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"specifier": "X", "odd": 1.500}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/matches/1/odds/3"))
                .andExpect(jsonPath("$.matchId").value(1))
                .andExpect(jsonPath("$.specifier").value("X"));
    }

    @Test
    void returnsConflictForADuplicateSpecifier() throws Exception {
        when(matchOddService.create(any(), any()))
                .thenThrow(new DuplicateResourceException("Specifier X already exists for match 1"));

        mockMvc.perform(post("/api/v1/matches/1/odds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"specifier": "X", "odd": 1.500}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Specifier X already exists for match 1"));
    }
}

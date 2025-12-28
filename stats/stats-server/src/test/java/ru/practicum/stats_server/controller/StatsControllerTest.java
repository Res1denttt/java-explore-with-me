package ru.practicum.stats_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats_server.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HitService hitService;

    private HitDto hitDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().minusDays(1);
        end = LocalDateTime.now();
        hitDto = new HitDto("app1", "/api/test", "127.0.0.1", LocalDateTime.now());
    }

    @Test
    void testSaveHit_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hitDto)))
                .andExpect(status().isCreated());

        verify(hitService, times(1)).save(any(HitDto.class));
    }

    @Test
    void testFindStats_shouldReturnList() throws Exception {
        ResponseStatsDto statsDto = new ResponseStatsDto("app1", "/api/test", 5L);
        when(hitService.find(start, end, List.of("/api/test"), true))
                .thenReturn(List.of(statsDto));

        mockMvc.perform(get("/stats")
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .param("uris", "/api/test")
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].app").value("app1"))
                .andExpect(jsonPath("$[0].uri").value("/api/test"))
                .andExpect(jsonPath("$[0].hits").value(5));

        verify(hitService, times(1)).find(start, end, List.of("/api/test"), true);
    }

    @Test
    void testFindStats_startAfterEnd_shouldReturnBadRequest() throws Exception {
        LocalDateTime invalidStart = end.plusDays(1);

        mockMvc.perform(get("/stats")
                        .param("start", invalidStart.toString())
                        .param("end", end.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Дата начала диапазона не может быть позже даты конца"));
    }
}

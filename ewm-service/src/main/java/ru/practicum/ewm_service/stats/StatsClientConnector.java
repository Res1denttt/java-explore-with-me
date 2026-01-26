package ru.practicum.ewm_service.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsClientConnector {
    private final StatsClient statsClient;

    public Map<Long, Long> getViews(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.debug("Stats request sending to stats-server: start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        return statsClient.getStats(
                        start,
                        end,
                        uris,
                        unique)
                .stream()
                .collect(Collectors.toMap(
                        response -> Long.parseLong(response.getUri().split("/")[2]),
                        ResponseStatsDto::getHits
                ));
    }

    public void saveHit(String appName, String uri, String ip, LocalDateTime timestamp) {
        log.debug("Hit sending to stats-server: appName =  {}, uri = {}, ip = {}, time = {}", appName, uri, ip, timestamp);
        statsClient.saveHit(appName, uri, ip, timestamp);
        log.info("Hit sent to stats-server: uri = {}, time = {}", uri, timestamp);
    }
}

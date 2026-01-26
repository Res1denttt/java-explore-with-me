package ru.practicum.ewm_service.event.service.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.event.EventMapper;
import ru.practicum.ewm_service.event.dal.CountRequests;
import ru.practicum.ewm_service.event.dal.EventRepository;
import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.model.Event;
import ru.practicum.ewm_service.event.model.EventSortType;
import ru.practicum.ewm_service.event.model.EventState;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.request.RequestRepository;
import ru.practicum.ewm_service.request.model.RequestStatus;
import ru.practicum.ewm_service.stats.StatsClientConnector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClientConnector statsClient;
    private final EventMapper eventMapper;
    private static final String APP_NAME = "ewm-service";
    private static final String BASE_URI = "/events";

    @Override
    public List<EventShortDto> getEventsByFilters(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd, boolean onlyAvailable, EventSortType sort,
                                                  int from, int size, String ip) {
        text = text == null || text.isEmpty() ? null : "%" + text.toLowerCase() + "%";
        if (categories == null || categories.isEmpty()) categories = null;
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        int candidateCount = Math.min(size + 500, 1000);

        List<Event> events = eventRepository.findAllByPublicFilters(text, categories, paid, rangeStart, rangeEnd, EventState.PUBLISHED,
                PageRequest.of(0, candidateCount)).getContent();

        if (events.isEmpty())
            return List.of();

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Integer> requestsMap = requestRepository.findIdsAndCountConfirmedRequestsByEventIds(ids, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(CountRequests::getEventId, CountRequests::getCount));

        LocalDateTime start = events.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        Map<Long, Long> viewsMap = statsClient.getViews(
                start,
                LocalDateTime.now(),
                ids.stream().map(id -> "/events/" + id).toList(),
                true);

        List<EventShortDto> dtos = new ArrayList<>(events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        requestsMap.getOrDefault(event.getId(), 0),
                        viewsMap.getOrDefault(event.getId(), 0L)
                ))
                .toList());

        if (onlyAvailable) {
            Map<Long, Integer> requestsLimit = events.stream()
                    .collect(Collectors.toMap(Event::getId, Event::getParticipantLimit));

            dtos = dtos.stream()
                    .filter(event -> event.confirmedRequests() < requestsLimit.get(event.id()))
                    .collect(Collectors.toList());
        }

        if (sort == EventSortType.VIEWS)
            dtos.sort(Comparator.comparing(EventShortDto::views).reversed());

        statsClient.saveHit(APP_NAME, BASE_URI, ip, LocalDateTime.now());

        if (dtos.size() > size + from) {
            return dtos.subList(from, from + size);
        } else if (dtos.size() >= from) {
            return dtos.subList(from, dtos.size());
        }
        return dtos;
    }

    @Override
    public EventFullDto getEventById(long eventId, String ip) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + "was not found"));
        if (event.getState() != EventState.PUBLISHED)
            throw new NotFoundException("Event with id=" + eventId + "was not found");
        LocalDateTime now = LocalDateTime.now();
        log.debug("Hit sending to stats-server: appName =  {}, uri = {}, ip = {}, time = {}",
                APP_NAME, BASE_URI + eventId, ip, now);
        statsClient.saveHit(APP_NAME, BASE_URI + "/" + eventId, ip, now);
        log.info("Hit sent to stats-server: uri = {}, time = {}", BASE_URI + "/" + eventId, now);
        int confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        long views;
        if (event.getPublishedOn() == null) {
            views = 0;
        } else {
            LocalDateTime start = event.getPublishedOn().minusHours(1);
            LocalDateTime end = LocalDateTime.now().plusHours(1);
            List<String> uris = List.of("/events/" + eventId);
            boolean unique = true;
            views = statsClient.getViews(start, end,
                            uris, unique)
                    .values()
                    .stream()
                    .mapToLong(Long::longValue)
                    .sum();
            log.info("Received from stats-server for start = {}, end = {}, uris = {}, unique = {}, views = {}",
                    start, end, uris, unique, views);
        }
        return eventMapper.toEventFullDto(event, confirmedRequests, views);
    }
}

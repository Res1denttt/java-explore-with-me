package ru.practicum.ewm_service.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm_service.category.Category;
import ru.practicum.ewm_service.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(min = 20, max = 2000, message = "длинна аннотации должна быть от 20 до 2000 символов")
    private String annotation;

    @Column(name = "created_on", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column
    @Size(min = 20, max = 7000, message = "длинна аннотации должна быть от 20 до 7000 символов")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column
    private boolean paid;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column
    @Size(min = 3, max = 120, message = "длинна заголовка должна быть от 3 до 120 символов")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Embedded
    private Location location;
}

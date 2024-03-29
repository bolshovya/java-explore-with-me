package ru.practicum.ewm.events.model;

import lombok.*;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.location.Location;
import ru.practicum.ewm.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(name = "annotation")
    private String annotation; // newEventDto

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // +EventServiceImpl

    @Column(name = "created_on")
    private LocalDateTime createdOn; // +EventServiceImpl

    @Column(name = "description")
    private String description; // newEventDto

    @Column(name = "event_date")
    private LocalDateTime eventDate; // newEventDto

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator; // +EventServiceImpl

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location; // newEventDto

    @Column(name = "paid")
    private Boolean paid; // newEventDto

    @Column(name = "participant_limit")
    private Integer participantLimit; // newEventDto

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration; // newEventDto

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state; // +EventServiceImpl

    @Column(name = "title")
    private String title; // newEventDto
}

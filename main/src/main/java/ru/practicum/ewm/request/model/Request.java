package ru.practicum.ewm.request.model;

import lombok.*;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.request.dto.RequestState;
import ru.practicum.ewm.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "user_id")
    private User requester;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestState status;
}

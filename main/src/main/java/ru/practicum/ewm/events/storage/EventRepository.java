package ru.practicum.ewm.events.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.events.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>{

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query(value = "SELECT * FROM events e WHERE ((?1) IS NULL OR e.initiator_id IN (?1))" +
            "AND ((?2) IS NULL OR e.state IN (?2))" +
            "AND ((?3) IS NULL OR e.category_id IN (?3))" +
            "AND (e.event_date BETWEEN ?4 AND ?5)", nativeQuery = true)
    List<Event> findAllByAdmin(List<Long> users,
                               List<String> states,
                               List<Long> categories,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               Pageable pageable);

}

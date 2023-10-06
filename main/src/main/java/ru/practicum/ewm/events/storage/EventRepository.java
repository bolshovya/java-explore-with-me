package ru.practicum.ewm.events.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.dto.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {


    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Event findByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE ((:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd)) ")
    List<Event> findAllByAdmin(@Param("users") List<Long> users,
                               @Param("states") List<EventState> states,
                               @Param("categories") List<Long> categories,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               Pageable pageable);

    @Query(value = "SELECT * FROM events e " +
            "WHERE (e.state = 'PUBLISHED')" +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND ((:categories) IS NULL OR e.category_id IN (:categories))" +
            "AND ((:paid) IS NULL OR e.paid = :paid)" +
            "AND (e.event_date BETWEEN :rangeStart AND :rangeEnd)" +
            "ORDER BY e.event_date", nativeQuery = true)
    List<Event> findAllPublicOrderByEventDate(@Param("text") String text,
                              @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd,
                              Pageable pageable);

    @Query(value = "SELECT * FROM events e " +
            "WHERE (e.state = 'PUBLISHED')" +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND ((:categories) IS NULL OR e.category_id IN (:categories))" +
            "AND ((:paid) IS NULL OR e.paid = :paid)" +
            "AND (e.event_date BETWEEN :rangeStart AND :rangeEnd)" +
            "ORDER BY e.views", nativeQuery = true)
    List<Event> findAllPublicOrderByViews(@Param("text") String text,
                                              @Param("categories") List<Long> categories,
                                              @Param("paid") Boolean paid,
                                              @Param("rangeStart") LocalDateTime rangeStart,
                                              @Param("rangeEnd") LocalDateTime rangeEnd,
                                              Pageable pageable);

    @Query(value = "SELECT * FROM events e " +
            "WHERE (e.state = 'PUBLISHED')" +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND ((:categories) IS NULL OR e.category_id IN (:categories))" +
            "AND ((:paid) IS NULL OR e.paid = :paid)" +
            "AND (e.event_date BETWEEN :rangeStart AND :rangeEnd)", nativeQuery = true)
    List<Event> findAllPublic(@Param("text") String text,
                                          @Param("categories") List<Long> categories,
                                          @Param("paid") Boolean paid,
                                          @Param("rangeStart") LocalDateTime rangeStart,
                                          @Param("rangeEnd") LocalDateTime rangeEnd,
                                          Pageable pageable);
}

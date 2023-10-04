package ru.practicum.ewm.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.dto.RequestState;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "SELECT COUNT(*) FROM requests r WHERE r.event_id = :eventId AND r.requester_id = :requesterId",
            nativeQuery = true)
    Long countByEventIdAndRequesterId(
            @Param("eventId") Long eventId,
            @Param("requesterId") Long requesterId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requesterId);

    Integer countByEventIdAndStatus(Long eventId, RequestState status);

}

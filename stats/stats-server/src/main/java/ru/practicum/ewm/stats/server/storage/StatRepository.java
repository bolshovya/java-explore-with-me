package ru.practicum.ewm.stats.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.server.model.StatModel;
import ru.practicum.ewm.stats.server.model.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatModel, Long> {

    @Query(value = "SELECT s.app, s.uri, COUNT(s.ip) as hits " +
            "FROM stat AS s " +
            "WHERE ((?1) IS NULL OR s.uri IN (?1)) " +
            "AND (s.timestamp BETWEEN ?2 AND ?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStat> findCountByUri(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip) as hits " +
            "FROM stat AS s " +
            "WHERE ((?1) IS NULL OR s.uri IN (?1)) " +
            "AND (s.timestamp BETWEEN ?2 AND ?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC",
            nativeQuery = true)
    List<ViewStat> findCountByUriUnique(List<String> uris, LocalDateTime start, LocalDateTime end);
}

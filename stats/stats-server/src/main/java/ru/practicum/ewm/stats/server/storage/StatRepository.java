package ru.practicum.ewm.stats.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.dto.ViewStat;
import ru.practicum.ewm.stats.server.model.StatModel;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<StatModel, Long> {

    @Query(value = "SELECT s.app, s.uri, COUNT(*) as hits FROM stat AS s WHERE s.uri = ?1 AND (s.timestamp BETWEEN ?2 AND ?3) GROUP BY s.app, s.uri", nativeQuery = true)
    ViewStat findCountByUri(String uri, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT DISTINCT s.app, s.uri, COUNT(*) as hits FROM stat AS s WHERE s.uri = ?1 AND (s.timestamp BETWEEN ?2 AND ?3) GROUP BY s.app, s.uri", nativeQuery = true)
    ViewStat findCountByUriUnique(String uri, LocalDateTime start, LocalDateTime end);

    /*
    @Query("select new ru.practicum.ewm.stats.dto.ViewStat(s.app, s.uri, count(s.count))" +
            "FROM stat AS s "+
            "WHERE s.uri = :uri AND (s.timestamp between :start and :end) "+
            "GROUP BY s.app, s.uri "+
            "order by count(s.stat_id) desc")
    ViewStat findCountByUri2(@Param("uri") String uri,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);

     */
}

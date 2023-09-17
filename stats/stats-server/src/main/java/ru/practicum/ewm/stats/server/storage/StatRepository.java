package ru.practicum.ewm.stats.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.server.model.StatModel;

public interface StatRepository extends JpaRepository<StatModel, Long> {
}

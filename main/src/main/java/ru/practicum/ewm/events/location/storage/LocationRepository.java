package ru.practicum.ewm.events.location.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.location.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}

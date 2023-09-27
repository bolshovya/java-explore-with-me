package ru.practicum.ewm.events.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.events.Event;

public interface EventRepository extends JpaRepository<Event, Long>{
}

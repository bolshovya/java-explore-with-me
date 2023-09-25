package ru.practicum.ewm.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}

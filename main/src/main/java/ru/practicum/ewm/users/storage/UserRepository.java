package ru.practicum.ewm.users.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.users.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {


    List<User> findAllByIdIn(List<Long> ids, Pageable pageable);
}

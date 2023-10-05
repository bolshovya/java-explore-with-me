package ru.practicum.ewm.users.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.users.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {


    @Query("SELECT u FROM User u WHERE (:ids IS NULL OR u.id IN :ids)")
    List<User> findAllByIdIn(@Param("ids") List<Long> ids, Pageable pageable);

    boolean existsUserByName(String name);
}

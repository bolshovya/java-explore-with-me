package ru.practicum.ewm.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    @Transactional
    UserDto create(UserDto userDto);

    void deleteUserById(Long userId);

    List<UserDto> findAll(List<Long> ids, Integer from, Integer size);
}

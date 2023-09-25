package ru.practicum.ewm.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.UserDto;

public interface UserService {
    @Transactional
    UserDto create(UserDto userDto);

    void deleteUserById(Long userId);
}

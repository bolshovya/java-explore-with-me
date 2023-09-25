package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("UserServiceImpl: сохранение пользователя: {}", userDto);
        User userFromDb = userRepository.save(UserMapper.getUser(userDto));
        log.info("UserServiceImpl: пользователю присвоен id: {}", userFromDb.getId());
        return UserMapper.getUserDto(userFromDb);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("UserNotFoundException: пользователь с id: " + userId + " не найден"));
        log.info("UserServiceImpl: удаление пользователя с id: {}", userId);
        userRepository.deleteById(userId);
        log.info("UserServiceImpl: пользователь с id: {} удален", userId);
    }
}

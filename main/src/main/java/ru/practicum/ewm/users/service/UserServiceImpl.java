package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserMapper;
import ru.practicum.ewm.users.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
                        new NotFoundException("User with id=" + userId + " was not found"));
        log.info("UserServiceImpl: удаление пользователя с id: {}", userId);
        userRepository.deleteById(userId);
        log.info("UserServiceImpl: пользователь с id: {} удален", userId);
    }

    @Override
    public List<UserDto> findAll(List<Long> ids, Integer from, Integer size) {
        log.info("UserServiceImpl: получение пользователей с id: {}", ids);
        Pageable pageable = PageRequest.of(from / size, size);

        List<User> usersFromDb = userRepository.findAllByIdIn(ids, pageable);
        return usersFromDb.stream().map(UserMapper::getUserDto).collect(Collectors.toList());
    }
}

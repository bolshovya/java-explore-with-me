package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserAdminController {

    private final UserService userService;


    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto create(
            @Valid @RequestBody UserDto userDto) {
        log.info("UserAdminController: сохранение пользователя: {}", userDto);
        return userService.create(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserById(
            @PathVariable Long userId
    ) {
        log.info("UserAdminController: удаление пользователя с id: {}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAll(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @PositiveOrZero Integer size
    ) {
        log.info("UserAdminController: получение пользователей с id: {}", ids);
        return userService.findAll(ids, from, size);
    }

}

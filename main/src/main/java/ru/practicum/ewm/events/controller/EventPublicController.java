package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventPublicController {
}

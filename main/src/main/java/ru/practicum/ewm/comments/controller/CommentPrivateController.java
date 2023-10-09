package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentRequestUpdateDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(
            @PathVariable Long userId,
            @Valid @RequestBody NewCommentDto newCommentDto
    ) {
        log.info("CommentPrivateController: сохранение комментария: {} для пользователя: {}", newCommentDto, userId);
        return commentService.create(userId, newCommentDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllByAuthor(
            @PathVariable Long userId
    ) {
        log.info("CommentPrivateController: получение всех комментариев, добавленных пользователем с id: {}", userId);
        return commentService.getAllByAuthor(userId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto patchComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestUpdateDto commentRequestUpdateDto
    ) {
        log.info("CommentPrivateController: изменение данных комментария с id: {}", commentId);
        return commentService.patch(userId, commentId, commentRequestUpdateDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        log.info("CommentPrivateController: удаление комментария с id: {}, добавленного пользователем с id: {}",
                commentId, userId);
        commentService.delete(userId, commentId);
    }
}

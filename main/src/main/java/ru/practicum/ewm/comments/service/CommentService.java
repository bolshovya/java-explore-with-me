package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentRequestUpdateDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto create(Long userId, NewCommentDto newCommentDto);

    List<CommentDto> getAllByAuthor(Long userId);

    CommentDto patch(Long userId, Long commentId, CommentRequestUpdateDto commentRequestUpdateDto);

    void delete(Long userId, Long commentId);

    List<CommentDto> getAll(Long eventId, Integer from, Integer size);

    CommentDto getById(Long commentId);
}

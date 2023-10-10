package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentMapper;
import ru.practicum.ewm.comments.dto.CommentRequestUpdateDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.storage.CommentRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;


    @Transactional
    @Override
    public CommentDto create(Long userId, NewCommentDto newCommentDto) {
        log.info("CommentServiceImpl: сохранение комментария: {} для пользователя: {}", newCommentDto, userId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        Event event = eventRepository.findById(newCommentDto.getEvent())
                .orElseThrow(() -> new NotFoundException("Событие с id: " + newCommentDto.getEvent() + " не найдено"));

        Comment newComment = Comment.builder()
                .created(LocalDateTime.now())
                .text(newCommentDto.getText())
                .author(author)
                .event(event)
                .build();

        newComment = commentRepository.save(newComment);

        log.info("CommentServiceImpl: новому комментарию: {} присвоен id", newComment);

        return CommentMapper.getCommentDto(newComment);
    }

    @Override
    public List<CommentDto> getAllByAuthor(Long userId) {
        log.info("CommentServiceImpl: получение всех комментариев, добавленных пользователем с id: {}", userId);

        return commentRepository.findAllByAuthorId(userId).stream()
                .map(CommentMapper::getCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto patch(Long userId, Long commentId, CommentRequestUpdateDto commentRequestUpdateDto) {
        log.info("CommentServiceImpl: изменение данных комментария с id: {}", commentId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь с id: " + userId + " не является автором комментария");
        }

        comment.setText(commentRequestUpdateDto.getText());

        return CommentMapper.getCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long commentId) {
        log.info("CommentServiceImpl: удаление комментария с id: {}, добавленного пользователем с id: {}",
                commentId, userId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь с id: " + userId + " не является автором комментария с id: " + commentId);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAll(Long eventId, Integer from, Integer size) {
        log.info("CommentServiceImpl: получение всех комментариев для события с id: {}", eventId);

        Pageable pageable = PageRequest.of(from / size, size);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не найдено"));

        return commentRepository.findAllByEventIdOrderById(eventId, pageable).stream()
                .map(CommentMapper::getCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getById(Long commentId) {
        log.info("CommentServiceImpl:  получение комментария с id: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден"));

        return CommentMapper.getCommentDto(comment);
    }

}

package ru.practicum.ewm.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.dto.EventMapper;
import ru.practicum.ewm.users.dto.UserMapper;

@UtilityClass
public class CommentMapper {

    public static CommentDto getCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .event(EventMapper.getEventShortDto(comment.getEvent()))
                .author(UserMapper.getUserShortDto(comment.getAuthor()))
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}

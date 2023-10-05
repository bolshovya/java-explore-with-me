package ru.practicum.ewm.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.SortState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventParam {

    private HttpServletRequest request;

    private String text;

    private List<Long> categories;

    private Boolean paid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;

    private Boolean onlyAvailable;

    private SortState sort;

    private Integer from;

    private Integer size;

    private List<Long> users;

    private List<EventState> states;

    public Pageable getPageable() {
        return PageRequest.of(from / size, size);
    }
}

package ru.practicum.ewm.stats.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ViewStatDto {

    private String app;

    private String uri;

    private Integer hits;
}

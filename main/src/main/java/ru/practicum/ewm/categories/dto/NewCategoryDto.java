package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCategoryDto {

    @Size(min = 1, max = 50)
    private String name;
}

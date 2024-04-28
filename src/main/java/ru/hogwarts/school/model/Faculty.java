package ru.hogwarts.school.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faculty {
    private Long id;
    private String name;
    private String color;
}
package ru.hogwarts.school.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "faculty")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "faculty")
    private List<Student> studentList;

    public Faculty(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
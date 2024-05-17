package ru.hogwarts.school.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "avatar")
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "file_size")
    private long fileSize;

    @Lob
    private byte[] data;

    @OneToOne
    private Student student;

    public Avatar(String filePath, String mediaType, long fileSize, Student student) {
        this.filePath = filePath;
        this.mediaType = mediaType;
        this.fileSize = fileSize;
        this.student = student;
    }
}
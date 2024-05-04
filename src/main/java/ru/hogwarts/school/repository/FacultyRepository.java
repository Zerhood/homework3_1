package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Collection<Faculty> findByColorIgnoreCase(String color);
    Collection<Faculty> findByNameIgnoreCase(String name);
    @Query("SELECT s FROM Faculty AS f JOIN Student AS s ON f.id = s.faculty.id WHERE f.name = ?1")
    Student findByStudents(String name);
}
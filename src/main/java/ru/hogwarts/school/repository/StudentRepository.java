package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findStudentsByAge(int age);

    Collection<Student> findByAgeBetween(int min, int max);

    Student findStudentByNameIgnoreCase(String name);

    @Query("SELECT COUNT(*) FROM Student AS s")
    int getCountAllByStudents();

    @Query("SELECT AVG(s.age) FROM Student AS s")
    double getAvgAgeByAllStudents();

    @Query("FROM Student AS s ORDER BY s.id DESC LIMIT 5")
    List<Student> get5StudentsAscId();
}
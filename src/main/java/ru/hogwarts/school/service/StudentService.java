package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class StudentService {

    private StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student addStudent(Student student) {
        return repository.save(student);
    }

    public Student findStudent(long id) {
        return repository.findById(id).get();
    }

    public Student editStudent(Student student) {
        return repository.save(student);
    }

    public Student deleteStudent(long id) {
        Student result = repository.getReferenceById(id);
        if (result != null) {
            repository.delete(result);
        }
        return result;
    }

    public Collection<Student> findByAge(int age) {
        return repository.findByAge(age);
    }
}
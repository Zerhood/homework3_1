package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping(value = "/id/{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(params = "age")
    public ResponseEntity<Collection<Student>> findByAge(@RequestParam int age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping(params = {"min", "max"})
    public ResponseEntity<Collection<Student>> findByAgeBetween(@RequestParam int min, @RequestParam int max) {
        if (min > 0 && max > min) {
            return ResponseEntity.ok(studentService.findByAgeBetween(min, max));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/{student}")
    public ResponseEntity<Faculty> findByFaculty(@PathVariable String student) {
        return ResponseEntity.ok(studentService.findByFaculty(student));
    }

    @GetMapping("/getCountAllByStudents")
    public ResponseEntity<Integer> getCountAllByStudents() {
        return ResponseEntity.ok(studentService.getCountAllByStudents());
    }

    @GetMapping("/getAvgAgeByAllStudents")
    public ResponseEntity<Double> getAvgAgeByAllStudents() {
        return ResponseEntity.ok(studentService.getAvgAgeByAllStudents());
    }

    @GetMapping("/get5StudentsAscId")
    public ResponseEntity<List<Student>> get5StudentsAscId() {
        return ResponseEntity.ok(studentService.get5StudentsAscId());
    }

    @GetMapping("/getAllStudentsByNameFirstA")
    public ResponseEntity<List<String>> getAllStudentsByNameFirstA() {
        return ResponseEntity.ok(studentService.getAllStudentsByNameFirstA());
    }

    @GetMapping("/getAvgAgeStudents")
    public ResponseEntity<Double> getAvgAgeStudents() {
        return ResponseEntity.ok(studentService.getAvgAgeStudents());
    }
}
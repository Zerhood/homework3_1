package ru.hogwarts.school.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("тест контроллера студентов через TestRestTemplate")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TRT_StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();

    private final List<Student> students = new ArrayList<>(10);

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        Faculty faculty1 = createFaculty();
        Faculty faculty2 = createFaculty();

        createStudent(faculty1, faculty2);
    }

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return facultyRepository.save(faculty);
    }

    private void createStudent(Faculty... faculties) {
        students.clear();
        Stream.of(faculties)
                .forEach(faculty ->
                        students.addAll(
                                studentRepository.saveAll(Stream.generate(() -> {
                                            Student student = new Student();
                                            student.setFaculty(faculty);
                                            student.setName(faker.harryPotter().character());
                                            student.setAge(faker.random().nextInt(11, 18));
                                            return student;
                                        })
                                        .limit(5)
                                        .collect(Collectors.toList()))
                        )
                );
    }

    private String baseUrl(String uriStartsWithSlash) {
        return "http://localhost:%d%s".formatted(port, uriStartsWithSlash);
    }

    @Test
    @DisplayName("создаем студента")
    public void testCreateStudent() {
        Student student = new Student();
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(11, 18));

        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(
                baseUrl("/student"),
                student,
                Student.class);
        Student created = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).isNotNull();
        assertThat(created).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(created);
        assertThat(created.getId()).isNotNull();

        Optional<Student> fromDB = studentRepository.findById(created.getId());

        assertThat(fromDB).isPresent();
        assertThat(fromDB.get())
                .usingRecursiveComparison()
                .isEqualTo(created);
    }

    @Test
    @DisplayName("получаем студентов по позрасту min max")
    public void testFindByAgeBetween() {
        int min = faker.random().nextInt(11, 18);
        int max = faker.random().nextInt(min, 18);
        Collection<Student> expected = students.stream()
                .filter(s -> s.getAge() >= min && s.getAge() <= max)
                .toList();

        ResponseEntity<Collection<Student>> responseEntity = testRestTemplate.exchange(
                baseUrl("/student?min={min}&max={max}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("min", min, "max", max)
        );
        Collection<Student> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("получаем студентов по позрасту age")
    public void testFindByAge() {
        int age = faker.random().nextInt(11, 18);
        Collection<Student> expected = students.stream()
                .filter(s -> s.getAge() == age)
                .toList();

        ResponseEntity<Collection<Student>> responseEntity = testRestTemplate.exchange(
                baseUrl("/student?age={age}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("age", age)
        );
        Collection<Student> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("получаем студента по id")
    public void testGetStudent() {
        Student student = students.get(faker.random().nextInt(students.size()));

        ResponseEntity<Student> responseEntity = testRestTemplate
                .getForEntity(
                        baseUrl("/student/id/{id}"),
                        Student.class,
                        Map.of("id", student.getId())
                );
        Student actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(student);
    }

    @Test
    @DisplayName("удаляем студента по id")
    public void testDeleteStudent() {
        Student student = students.get(faker.random().nextInt(students.size()));

        testRestTemplate.delete(
                baseUrl("/student/{id}"),
                Map.of("id", student.getId())
        );

        Optional<Student> fromDB = studentRepository.findById(student.getId());

        assertThat(fromDB).isNotPresent();
    }

    @Test
    @DisplayName("получаем факультет студента по имени студента")
    public void testFindByFaculty() {
        Student student = students.get(faker.random().nextInt(students.size()));

        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                baseUrl("/student/{student}"),
                Faculty.class,
                Map.of("student", student.getName())
        );
        Faculty actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(student.getFaculty());
    }
}
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

@DisplayName("тест контроллера факультета через TestRestTemplate")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TRT_FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();

    private Faculty faculty1;

    private Faculty faculty2;

    private final List<Student> students = new ArrayList<>(10);

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        faculty1 = createFaculty();
        faculty2 = createFaculty();

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
    @DisplayName("создаем факультет")
    public void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().character());
        faculty.setColor(faker.color().name());
        faculty.setStudentList(new ArrayList<>());

        ResponseEntity<Faculty> responseEntity = testRestTemplate.postForEntity(
                baseUrl("/faculty"),
                faculty,
                Faculty.class);
        Faculty created = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).isNotNull();
        assertThat(created).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(faculty);
        assertThat(created.getId()).isNotNull();

        Optional<Faculty> fromDB = facultyRepository.findById(created.getId());

        assertThat(fromDB).isPresent();
        assertThat(fromDB.get())
                .usingRecursiveComparison()
                .isEqualTo(created);
    }

    @Test
    @DisplayName("получаем факультет по id")
    public void testGetFacultyInfo() {
        Long id = faculty1.getId();

        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                baseUrl("/faculty/{id}"),
                Faculty.class,
                Map.of("id", id));
        Faculty created = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).usingRecursiveComparison()
                .isEqualTo(faculty1);
    }

    @Test
    @DisplayName("удаляем факультет по id")
    public void testDeleteFaculty() {
        Long id = faculty1.getId();

        testRestTemplate.delete(
                baseUrl("/faculty/{id}"),
                Map.of("id", id)
        );

        Optional<Student> fromDB = studentRepository.findById(faculty1.getId());

        assertThat(fromDB).isNotPresent();
    }

    @Test
    @DisplayName("получаем список факультетов по цвету или названию")
    public void testFindFaculties() {
        String name = faculty1.getName();
        String color = faculty1.getColor();
        Collection<Faculty> expected = List.of(faculty1);

        ResponseEntity<Collection<Faculty>> responseEntity = testRestTemplate.exchange(
                baseUrl("/faculty?color={color}&name={name}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("color", color, "name", name)
        );
        Collection<Faculty> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("получаем список студентов по названию факультета")
    public void testFindByStudent() {
        String faculty = faculty1.getName();
        Collection<Student> expected = faculty1.getStudentList();

        ResponseEntity<Collection<Student>> responseEntity = testRestTemplate.exchange(
                baseUrl("/faculty/{faculty}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                Map.of("faculty", faculty)
        );
        Collection<Student> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }
}
package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayName("контроллер студентов через MockMvc")
public class WMT_StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private AvatarRepository avatarRepository;

    @SpyBean
    private StudentService service;

    @Autowired
    private ObjectMapper objectMapper;

    private final Random random = new Random();

    @Test
    @DisplayName("получаем студентов по id")
    public void testGetFacultyInfo() throws Exception {
        Student student = createStudent();

        final Long id = student.getId();
        final String name = student.getName();
        final int age = student.getAge();

        when(studentRepository.getReferenceById(any())).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/id/" + id)
                        .content(id.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    @DisplayName("создаём студента")
    public void testCreateStudent() throws Exception {
        Student student = createStudent();

        when(studentRepository.save(any())).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(result -> {
                    Student response = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Student.class);
                    assertThat(response).usingRecursiveComparison()
                            .isEqualTo(student);
                    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
                });
    }

    @Test
    @DisplayName("обновляем студента")
    public void testEditStudent() throws Exception {
        long id = 1;
        String newName = "Petr";
        int newAge = 15;

        Student oldStudent = new Student("Ivan", 14);
        oldStudent.setId(id);

        Student newStudent = new Student(newName, newAge);
        newStudent.setId(id);

        when(studentRepository.getReferenceById(any())).thenReturn(oldStudent);
        when(studentRepository.save(any())).thenReturn(newStudent);

        mockMvc.perform(MockMvcRequestBuilders.put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(result -> {
                    Student response = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Student.class);
                    assertThat(response).usingRecursiveComparison()
                            .isEqualTo(newStudent);
                    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
                });
    }

    @Test
    @DisplayName("удаляем студента")
    public void testDeleteStudent() throws Exception {
        Student student = createStudent();
        when(studentRepository.getReferenceById(any())).thenReturn(student);
        mockMvc.perform(MockMvcRequestBuilders.delete("/student/" + student.getId()))
                .andExpect(s -> assertThat(s.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("получаем коллекцию студентов по возрасту")
    public void testFindByAge() throws Exception {
        Student student = createStudent();
        Integer ageStudent = student.getAge();

        Collection<Student> actual = new ArrayList<>();
        actual.add(student);

        when(studentRepository.findStudentsByAge(student.getAge())).thenReturn(actual);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .param("age", ageStudent.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(actual)))
        ;
    }

    @Test
    @DisplayName("получаем коллекцию студентов по минимальному и максимальному возрасту")
    public void testFindByAgeBetween() throws Exception {
        Integer min = 12;
        Integer max = 18;
        Student student = createStudent();

        Collection<Student> actual = new ArrayList<>();
        actual.add(student);

        when(studentRepository.findByAgeBetween(min, max)).thenReturn(actual);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student")
                        .param("min", String.valueOf(min))
                        .param("max", String.valueOf(max))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(actual)));
    }

    @Test
    @DisplayName("получаем факультет по имени студента")
    public void testFindByFaculty() throws Exception {
        Student student = createStudent();
        Faculty faculty = student.getFaculty();

        when(studentRepository.findStudentByNameIgnoreCase(student.getName()))
                .thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + student.getName())
                        .content(student.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(faculty)));
    }

    private Student createStudent() {
        final Long id = random.nextLong();
        final String name = random.toString();
        final int age = random.nextInt();
        final Faculty faculty = new Faculty();

        Student student = new Student(name, age);
        student.setId(id);
        student.setFaculty(faculty);
        return student;
    }
}
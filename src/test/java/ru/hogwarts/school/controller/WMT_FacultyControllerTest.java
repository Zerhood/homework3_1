package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
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
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.predicate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
@DisplayName("тест контроллера факультета через MVC")
public class WMT_FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private FacultyService facultyService;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Random random = new Random();

    @Test
    @DisplayName("обновляем факультет")
    public void testEditFaculty() throws Exception {
        long id = 1;
        String newName = "Гриффиндор";
        String newColor = "красный";

        Faculty oldFaculty = new Faculty("Слизерин", "зеленый");
        oldFaculty.setId(id);

        Faculty newFaculty = new Faculty(newName, newColor);
        newFaculty.setId(id);

        when(facultyRepository.findById(any())).thenReturn(Optional.of(oldFaculty));
        when(facultyRepository.save(any())).thenReturn(newFaculty);

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(newFaculty)))
                .andExpect(result -> {
                    Faculty response = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Faculty.class);
                    assertThat(response).usingRecursiveComparison()
                            .isEqualTo(newFaculty);
                    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
                });
    }

    @Test
    @DisplayName("получаем факультет по id")
    public void testGetFacultyInfo() throws Exception {
        Faculty faculty = createFaculty();

        final Long id = faculty.getId();
        final String name = faculty.getName();
        final String color = faculty.getColor();
        final List<Student> studentList = faculty.getStudentList();

        when(facultyRepository.getReferenceById(any())).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + id)
                        .content(id.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color))
                .andExpect(jsonPath("$.studentList").value(studentList));
    }

    @Test
    @DisplayName("создаём факультет")
    public void testCreateFaculty() throws Exception {
        Faculty faculty = createFaculty();

        when(facultyRepository.save(any())).thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(result -> {
                    Faculty response = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), Faculty.class);
                    assertThat(response).usingRecursiveComparison()
                            .isEqualTo(faculty);
                    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
                });
    }

    @Test
    @DisplayName("удаляем факультет")
    public void testDeleteFaculty() throws Exception {
        Faculty faculty = createFaculty();
        when(facultyRepository.getReferenceById(any())).thenReturn(faculty);
        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/" + faculty.getId()))
                .andExpect(s -> assertThat(s.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value()));
    }

    @Test
    @DisplayName("получаем коллекцию факультетов по имени или цвету")
    public void testFindFaculties() throws Exception {
        Faculty faculty = createFaculty();

        Collection<Faculty> actual = new ArrayList<>();
        actual.add(faculty);

        when(facultyRepository.findFacultiesByColorIgnoreCaseOrNameIgnoreCase(faculty.getColor(), faculty.getName()))
                .thenReturn(actual);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .param("colorOrName", faculty.getColor())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(actual)));
    }

    @Test
    @DisplayName("получаем коллекцию студентов по названию факультета")
    public void testFindByStudent() throws Exception {
        Faculty faculty = createFaculty();

        Collection<Student> actual = new ArrayList<>(faculty.getStudentList());

        when(facultyRepository.findFacultyByNameIgnoreCase(faculty.getName()))
                .thenReturn(faculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + faculty.getName() + "/students")
                        .content(faculty.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(actual)))
        ;
    }

    private Faculty createFaculty() {
        final Long id = random.nextLong();
        final String name = random.toString();
        final String color = name + random;
        final List<Student> studentCollection = new ArrayList<>();

        Faculty faculty = new Faculty(name, color);
        faculty.setId(id);
        faculty.setStudentList(studentCollection);
        return faculty;
    }
}
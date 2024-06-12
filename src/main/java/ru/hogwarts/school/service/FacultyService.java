package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {

    private FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.getReferenceById(id);
    }

    public Faculty editFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(long id) {
        Faculty result = findFaculty(id);
        if (result != null) {
            facultyRepository.deleteById(id);
        }
        return result;
    }

    public Collection<Faculty> findByColorOrName(String colorOrName) {
        return facultyRepository.findFacultiesByColorIgnoreCaseOrNameIgnoreCase(colorOrName, colorOrName);
    }

    public Collection<Student> findByStudents(String name) {
        Faculty faculty = facultyRepository.findFacultyByNameIgnoreCase(name);
        return faculty.getStudentList();
    }
}
package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;

@Service
public class FacultyService {

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("A method was called to create a faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.info("A method was called to output information about the faculty");
        return facultyRepository.getReferenceById(id);
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("A method was called to change the information about the faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(long id) {
        logger.info("A method was called to delete the faculty");
        Faculty result = findFaculty(id);
        if (result != null) {
            facultyRepository.deleteById(id);
        }
        return result;
    }

    public Collection<Faculty> findByColorOrName(String colorOrName) {
        logger.info("A method was called that outputs faculty by name or color");
        return facultyRepository.findFacultiesByColorIgnoreCaseOrNameIgnoreCase(colorOrName, colorOrName);
    }

    public Collection<Student> findByStudents(String name) {
        logger.info("A method was called that outputs a list of students of the faculty");
        Faculty faculty = facultyRepository.findFacultyByNameIgnoreCase(name);
        return faculty.getStudentList();
    }
}
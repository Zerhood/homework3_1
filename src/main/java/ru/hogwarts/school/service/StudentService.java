package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class StudentService {

    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;

    private StudentRepository repository;
    private AvatarRepository avatarRepository;

    @Value("avatars.dir.path")
    private String avatarsDir;

    public StudentService(StudentRepository repository,
                          AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.repository = repository;
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        return repository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("A method was called to output information about the student");
        return repository.getReferenceById(id);
    }

    public Student editStudent(Student student) {
        logger.info("The method of changing the student's data was called");
        return repository.save(student);
    }

    public Student deleteStudent(long id) {
        logger.info("A method was called to delete the student");
        Student result = repository.getReferenceById(id);
        if (result != null) {
            repository.delete(result);
        }
        return result;
    }

    public Collection<Student> findByAge(int age) {
        logger.info("A method was called that outputs all students of a certain age");
        return repository.findStudentsByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("A method was called that outputs all students of a certain age range");
        return repository.findByAgeBetween(min, max);
    }

    public Faculty findByFaculty(String student) {
        logger.info("A method was called showing the student's faculty");
        Student student1 = repository.findStudentByNameIgnoreCase(student);
        return student1.getFaculty();
    }

    public Avatar findAvatar(Long studentId) {
        logger.info("A method was called showing the student's avatar");
        return avatarRepository.findByStudentId(studentId).orElseThrow();
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("The method that loads the avatar was called");
        Student student = findStudent(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    public int getCountAllByStudents() {
        logger.info("A method was called showing the number of students");
        return repository.getCountAllByStudents();
    }

    public double getAvgAgeByAllStudents() {
        logger.info("A method was called showing the average age of students");
        return repository.getAvgAgeByAllStudents();
    }

    public List<Student> get5StudentsAscId() {
        logger.info("A method was called showing the last 5 enrolled students");
        return repository.get5StudentsAscId();
    }

    public List<Avatar> getAllAvatar(Integer pageNumber, Integer pageSize) {
        logger.info("A method was called showing all avatars page by page");
        PageRequest request = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(request).getContent();
    }

    public List<String> getAllStudentsByNameFirstA() {
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(s -> String.valueOf(s.charAt(0)).equals("A"))
                .map(s -> s.toUpperCase(Locale.ROOT))
                .sorted(Comparator.naturalOrder())
                .toList();
    }

    public Double getAvgAgeStudents() {
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .getAsDouble();
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
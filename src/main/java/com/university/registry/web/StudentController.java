package com.university.registry.web;

import com.university.registry.model.Student;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.StudentService;
import com.university.registry.web.dto.StudentRequest;
import com.university.registry.web.dto.StudentResponse;
import com.university.registry.web.dto.StudentUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    public StudentController(StudentService studentService, EnrollmentService enrollmentService)
    {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public List<StudentResponse> list()
    {
        List<StudentResponse> result = new ArrayList<>();
        for (Student student : studentService.getAllStudents())
        {
            result.add(StudentResponse.from(student));
        }

        return result;
    }

    @GetMapping("/{am}")
    public StudentResponse get(@PathVariable String am)
    {
        return StudentResponse.from(studentService.getStudentByAm(am));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentRequest request)
    {
        Student student = new Student(request.am(), request.semester(), request.name(), request.email(), request.phone());
        studentService.addStudent(student);

        return StudentResponse.from(student);
    }

    @PutMapping("/{am}")
    public StudentResponse update(@PathVariable String am, @Valid @RequestBody StudentUpdateRequest request)
    {
        Student student = new Student(am, request.semester(), request.name(), request.email(), request.phone());
        studentService.updateStudent(student);

        return StudentResponse.from(student);
    }

    @DeleteMapping("/{am}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String am)
    {
        enrollmentService.deleteStudentSafely(am);
    }
}

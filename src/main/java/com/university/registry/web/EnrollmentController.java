package com.university.registry.web;

import com.university.registry.model.Course;
import com.university.registry.service.EnrollmentService;
import com.university.registry.web.dto.CourseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EnrollmentController
{
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService)
    {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/students/{am}/courses/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void enroll(@PathVariable String am, @PathVariable String courseId)
    {
        enrollmentService.enrollStudentToCourse(am, courseId);
    }

    @DeleteMapping("students/{am}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable String am, @PathVariable String courseId)
    {
        enrollmentService.unenrollStudent(am, courseId);
    }

    @GetMapping("/students/{am}/courses")
    public List<CourseResponse> coursesOfStudent(@PathVariable String am)
    {
        List<CourseResponse> result = new ArrayList<>();
        for (Course course : enrollmentService.getCoursesForStudent(am))
        {
            result.add(CourseResponse.from(course));
        }

        return result;
    }

    @PostMapping("professors/{profId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void assignProfessor(@PathVariable String profId, @PathVariable String courseId)
    {
        enrollmentService.assignCourseToProfessor(profId, courseId);
    }

    @DeleteMapping("professors/{profId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignProfessor(@PathVariable String profId, @PathVariable String courseId)
    {
        enrollmentService.unassignProfessor(profId, courseId);
    }

    @GetMapping("professors/{profId}/courses")
    public List<CourseResponse> coursesOfProfessor(@PathVariable String profId)
    {
        List<CourseResponse> result = new ArrayList<>();
        for (Course course : enrollmentService.getCoursesForProfessor(profId))
        {
            result.add(CourseResponse.from(course));
        }

        return result;
    }
}

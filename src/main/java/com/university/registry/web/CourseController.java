package com.university.registry.web;

import com.university.registry.model.Course;
import com.university.registry.service.CourseService;
import com.university.registry.service.EnrollmentService;
import com.university.registry.web.dto.CourseRequest;
import com.university.registry.web.dto.CourseResponse;
import com.university.registry.web.dto.CourseUpdateRequest;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController
{
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public CourseController(CourseService courseService, EnrollmentService enrollmentService)
    {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public List<CourseResponse> list()
    {
        List<CourseResponse> result = new ArrayList<>();
        for (Course course : courseService.getAllCourses())
        {
            result.add(CourseResponse.from(course));
        }

        return result;
    }

    @GetMapping("/{courseId}")
    public CourseResponse get(@PathVariable String courseId)
    {
        return CourseResponse.from(courseService.getCourseById(courseId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@RequestBody CourseRequest request)
    {
        Course course = new Course(request.courseId(), request.courseTitle(), request.courseSemester());
        courseService.addCourse(course);

        return CourseResponse.from(course);
    }

    @PutMapping("/{courseId}")
    public CourseResponse update(@PathVariable String courseId, @RequestBody CourseUpdateRequest request)
    {
        Course course = new Course(courseId, request.courseTitle(), request.courseSemester());
        courseService.updateCourse(course);

        return CourseResponse.from(course);
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String courseId)
    {
        enrollmentService.deleteCourseSafely(courseId);
    }
}

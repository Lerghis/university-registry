package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CourseService
{
    private final Map<String, Course> coursesById;

    public CourseService()
    {
        this.coursesById = new HashMap<>();
    }

    public void addCourse(Course course) throws DuplicateEntityException
    {
        if (coursesById.containsKey(course.getCourseId()))
        {
            throw new DuplicateEntityException("Course", course.getCourseId());
        }

        coursesById.put(course.getCourseId(), course);
    }

    public Course getCourseById(String id) throws EntityNotFoundException
    {
        Course course = coursesById.get(id);
        if (course == null)
        {
            throw new EntityNotFoundException("Course", id);
        }

        return course;
    }

    public void updateCourse(Course updatedCourse) throws EntityNotFoundException
    {
        getCourseById(updatedCourse.getCourseId());
        coursesById.put(updatedCourse.getCourseId(), updatedCourse);
    }

    public void deleteCourse(String id) throws EntityNotFoundException
    {
        getCourseById(id);
        coursesById.remove(id);
    }

    public Collection<Course> getAllCourses()
    {
        return coursesById.values();
    }
}

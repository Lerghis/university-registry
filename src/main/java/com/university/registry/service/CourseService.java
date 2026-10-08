package com.university.registry.service;

import com.university.registry.dao.CourseDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CourseService
{
    private final CourseDAO courseDAO;

    public CourseService(CourseDAO courseDAO)
    {

        this.courseDAO = courseDAO;
    }

    public void addCourse(Course course) throws DuplicateEntityException
    {
        if (courseDAO.findById(course.getCourseId()) != null)
        {
            throw new DuplicateEntityException("Course", course.getCourseId());
        }

        courseDAO.insert(course);
    }

    public Course getCourseById(String id) throws EntityNotFoundException
    {
        Course course = courseDAO.findById(id);
        if (course == null)
        {
            throw new EntityNotFoundException("Course", id);
        }

        return course;
    }

    public void updateCourse(Course updatedCourse) throws EntityNotFoundException
    {
        getCourseById(updatedCourse.getCourseId());
        courseDAO.update(updatedCourse);
    }

    public void deleteCourse(String id) throws EntityNotFoundException
    {
        Course course = getCourseById(id);
        courseDAO.delete(course);
    }

    public Collection<Course> getAllCourses()
    {
        return courseDAO.findAll();
    }
}

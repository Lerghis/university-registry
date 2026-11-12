package com.university.registry.service;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Business-logic layer for {@link Course} objects.
 * <p>
 * Translates repository results into domain rules (a missing record throws an
 * {@link EntityNotFoundException}, while inserting an existing ID throws a
 * {@link DuplicateEntityException}) and delegates persistence operations to
 * {@link CourseRepository}. Holds no state itself.
 * <p>
 * All domain exceptions thrown by this service are unchecked and handled globally
 * by the application's exception handler. Database failures surface separately as
 * Spring's unchecked {@code DataAccessException}.
 */
@Service
public class CourseService
{
    private final CourseRepository courseRepository;

    /**
     * Constructs a {@code CourseService} backed by the given repository.
     *
     * @param courseRepository the Spring Data JPA repository interface used for {@link Course} persistence
     */
    public CourseService(CourseRepository courseRepository)
    {
        this.courseRepository = courseRepository;
    }

    /**
     * Adds a new {@link Course} to the database.
     *
     * @param course the course to be added, assumed already validated before being passed here
     * @throws DuplicateEntityException if a course with the same ID already exists
     */
    public void addCourse(Course course)
    {
        if (courseRepository.existsById(course.getCourseId()))
        {
            throw new DuplicateEntityException("Μάθημα", course.getCourseId());
        }

        courseRepository.save(course);
    }

    /**
     * Looks up a specific {@link Course} by its ID.
     *
     * @param id the course's ID (primary key)
     * @return the matching course
     * @throws EntityNotFoundException if no course exists with the given ID
     */
    public Course getCourseById(String id)
    {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isEmpty())
        {
            throw new EntityNotFoundException("Μάθημα", id);
        }

        return course.get();
    }

    /**
     * Updates an existing {@link Course} after verifying its existence.
     *
     * @param updatedCourse the course with updated fields to save
     * @throws EntityNotFoundException if no course exists with the given course's ID
     */
    public void updateCourse(Course updatedCourse)
    {
        getCourseById(updatedCourse.getCourseId());
        courseRepository.save(updatedCourse);
    }

    /**
     * Deletes the {@link Course} matching the given ID after verifying its existence.
     *
     * @param id the identifier of the course to be deleted
     * @throws EntityNotFoundException if no course exists with the given ID
     */
    public void deleteCourse(String id)
    {
        getCourseById(id);
        courseRepository.deleteById(id);
    }

    /**
     * Retrieves all {@link Course} objects stored in the database.
     *
     * @return a collection holding all courses
     */
    public Collection<Course> getAllCourses()
    {
        return courseRepository.findAll();
    }
}

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
 * Decides what the stored data means (a missing row becomes an
 * {@link EntityNotFoundException}, an existing row on insert becomes a
 * {@link DuplicateEntityException}) and delegates all actual storage to
 * {@link CourseRepository}. Holds no data itself.
 * <p>
 * Both exceptions are checked, so methods declare them with {@code throws}.
 * Database failures surface separately as the unchecked
 * {@code DataAccessException}.
 */
@Service
public class CourseService
{
    private final CourseRepository courseRepository;

    /**
     *
     * @param courseRepository is the Interface Spring Data JPA uses to generate the implementation class.
     */
    public CourseService(CourseRepository courseRepository)
    {
        this.courseRepository = courseRepository;
    }

    /**
     * Adds a new Course to the database.
     *
     * @param course the course to be added assumed already validated before being passed here
     * @throws DuplicateEntityException if the adding fails because of a duplicate primary key (courseId)
     */
    public void addCourse(Course course) throws DuplicateEntityException
    {
        if (courseRepository.existsById(course.getCourseId()))
        {
            throw new DuplicateEntityException("Μάθημα", course.getCourseId());
        }

        courseRepository.save(course);
    }

    /**
     * Looks up for a specific Course by its id.
     *
     * @param id the Course's id (primary key)
     * @return the matching course
     * @throws EntityNotFoundException if the Course isn't found in the database
     */
    public Course getCourseById(String id) throws EntityNotFoundException
    {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isEmpty())
        {
            throw new EntityNotFoundException("Μάθημα", id);
        }

        return course.get();
    }

    /**
     * Updates the Course that's being passed as a parameter after first verifying its existence.
     *
     * @param updatedCourse the Course to be updated
     * @throws EntityNotFoundException if the Course isn't found in the database
     */
    public void updateCourse(Course updatedCourse) throws EntityNotFoundException
    {
        getCourseById(updatedCourse.getCourseId());
        courseRepository.save(updatedCourse);
    }

    /**
     * Deletes the Course matching the given id after first verifying its existence.
     *
     * @param id the identifier of the Course that's going to be deleted
     * @throws EntityNotFoundException if the Course isn't found in the database
     */
    public void deleteCourse(String id) throws EntityNotFoundException
    {
        getCourseById(id);
        courseRepository.deleteById(id);
    }

    /**
     * Gets the list of all the Course
     * objects stored in the database.
     * @return a collection holding all the courses
     */
    public Collection<Course> getAllCourses()
    {
        return courseRepository.findAll();
    }
}

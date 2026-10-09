package com.university.registry.service;

import com.university.registry.dao.CourseDAO;
import com.university.registry.dao.StudentDAO;
import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.model.Student;

import java.util.Collection;

/**
 * Business-logic layer for {@link Course} objects.
 * <p>
 * Decides what the stored data means (a missing row becomes an
 * {@link EntityNotFoundException}, an existing row on insert becomes a
 * {@link DuplicateEntityException}) and delegates all actual storage to
 * {@link CourseDAO}. Holds no data itself.
 * <p>
 * Both exceptions are checked, so methods declare them with {@code throws}.
 * Database failures surface separately as the unchecked
 * {@code DataAccessException}.
 */
public class CourseService
{
    private final CourseDAO courseDAO;

    /**
     *
     * @param courseDAO is the Data Access Object for the {@code courses} table
     *                   which is being injected via this constructor, because
     *                   {@code CourseService} needs access to its methods.
     */
    public CourseService(CourseDAO courseDAO)
    {

        this.courseDAO = courseDAO;
    }

    /**
     * Adds a new Course to the database by calling {@link CourseDAO}'s {@code insert()} method
     *
     * @param course the course to be added assumed already validated before being passed here
     *
     * @throws DuplicateEntityException if the adding fails because of a duplicate primary key (courseId)
     */
    public void addCourse(Course course) throws DuplicateEntityException
    {
        if (courseDAO.findById(course.getCourseId()) != null)
        {
            throw new DuplicateEntityException("Course", course.getCourseId());
        }

        courseDAO.insert(course);
    }

    /**
     * Looks up for a specific Course by its id by calling {@link CourseDAO}'s {@code findById()} method
     *
     * @param id the Course's id (primary key)
     *
     * @return the matching {@link Course}
     *
     * @throws EntityNotFoundException if the Course isn't found in the database
     */
    public Course getCourseById(String id) throws EntityNotFoundException
    {
        Course course = courseDAO.findById(id);
        if (course == null)
        {
            throw new EntityNotFoundException("Course", id);
        }

        return course;
    }

    /**
     * Updates the {@link Course} that's being passed as a parameter by calling {@link CourseDAO}'s
     * {@code update()} method, after first verifying its existence with {@code getCourseById()}
     *
     * @param updatedCourse the {@link Course} to be updated
     *
     * @throws EntityNotFoundException if the Course isn't found in the database
     */
    public void updateCourse(Course updatedCourse) throws EntityNotFoundException
    {
        getCourseById(updatedCourse.getCourseId());
        courseDAO.update(updatedCourse);
    }

    /**
     * Deletes the {@link Course} matching the given id by calling {@link CourseDAO}'s
     * {@code delete()} method, after first verifying its existence with {@code getCourseById()}
     *
     * @param id the identifier of the Course that's going to be deleted
     *
     * @throws EntityNotFoundException if the Course isn't found in the database
     */
    public void deleteCourse(String id) throws EntityNotFoundException
    {
        Course course = getCourseById(id);
        courseDAO.delete(course);
    }

    /**
     * Calls {@link CourseDAO}'s {@code findAll()} method to get the list of all the Course
     * objects stored in the database
     *
     * @return a collection holding all the courses
     */
    public Collection<Course> getAllCourses()
    {
        return courseDAO.findAll();
    }
}

package com.university.registry.model;

import java.io.Serializable;
import java.util.Objects;

public class StudentCourseId implements Serializable
{
    private String studentAm;
    private String courseId;

    public StudentCourseId() { }

    public StudentCourseId(String studentAm, String courseId)
    {
        this.studentAm = studentAm;
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        StudentCourseId that = (StudentCourseId) object;
        return Objects.equals(studentAm, that.studentAm) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(studentAm, courseId);
    }

    /*
      Hibernate keeps every loaded row in an in-memory cache keyed by its id.
      Without these two methods, two key objects holding the same two values
      would count as different keys, so lookups would miss and rows could be treated as duplicates.

      equals and hashcode: They make “same two values” mean “same key.”
     */
}

package com.university.registry.web.dto;

import com.university.registry.model.Course;

public record CourseResponse(String courseId, String courseTitle, int courseSemester)
{
    public static CourseResponse from(Course course)
    {
        return new CourseResponse(course.getCourseId(), course.getCourseTitle(), course.getCourseSemester());
    }
}

package com.university.registry.model;

import com.university.registry.exception.InvalidSemesterException;

public class Course
{
    private String courseId;
    private String courseTitle;
    private int courseSemester;

    public Course(String courseId, String courseTitle, int courseSemester) throws InvalidSemesterException
    {
        this.courseId = courseId.trim();
        this.courseTitle = courseTitle;
        setCourseSemester(courseSemester);
    }

    public String getCourseId()
    {
        return courseId;
    }

    public void setCourseId(String courseId)
    {
        this.courseId = courseId.trim();
    }

    public String getCourseTitle()
    {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle)
    {
        this.courseTitle = courseTitle;
    }

    public int getCourseSemester()
    {
        return courseSemester;
    }

    public void setCourseSemester(int courseSemester) throws InvalidSemesterException
    {
        if (courseSemester < 1)
        {
            throw new InvalidSemesterException(courseSemester);
        }
        this.courseSemester = courseSemester;
    }

    @Override
    public String toString()
    {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseTitle='" + courseTitle + '\'' +
                ", courseSemester=" + courseSemester +
                '}';
    }
}

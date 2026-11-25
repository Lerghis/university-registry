package com.university.registry.controller;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.model.Course;
import com.university.registry.service.CourseService;
import com.university.registry.service.EnrollmentService;
import com.university.registry.web.CourseController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
public class CourseControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private EnrollmentService enrollmentService;

    @Test
    void listCourses_whenHasItems_returns200() throws Exception
    {
        List<Course> courses = List.of(new Course("C1", "C Programming",  1),
                                       new Course("C2", "Databases",  1));

        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listCourses_whenEmpty_returnAnEmptyList() throws Exception
    {
        when(courseService.getAllCourses()).thenReturn(List.of());

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getCourse_whenExists_return200AndJson() throws Exception
    {
        when(courseService.getCourseById("C1")).thenReturn(new Course("C1", "C Programming",  1));

        mockMvc.perform(get("/courses/C1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId").value("C1"))
                .andExpect(jsonPath("$.courseTitle").value("C Programming"));
    }

    @Test
    void getCourse_whenMissing_returns404() throws Exception
    {
        when(courseService.getCourseById("C1")).thenThrow(new EntityNotFoundException("Course", "C1"));

        mockMvc.perform(get("/courses/C1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createCourse_whenCreated_returns201() throws Exception
    {
        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1","courseTitle":"C Programming","courseSemester":1}
                        """))
                .andExpect(status().isCreated());

        verify(courseService, times(1)).addCourse(any());
    }

    @Test
    void createCourse_whenDuplicate_returns409() throws Exception
    {
        doThrow(new DuplicateEntityException("Course", "C1")).when(courseService).addCourse(any());

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1","courseTitle":"C Programming","courseSemester":1}
                        """))
                .andExpect(status().isConflict());
    }

    @Test
    void createCourse_withEmptyBody_returns400() throws Exception
    {
        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(courseService, never()).addCourse(any());
    }

    @Test
    void updateCourse_whenUpdated_returns200() throws Exception
    {
        mockMvc.perform(put("/courses/C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1","courseTitle":"C Programming","courseSemester":1}
                        """))
                .andExpect((status().isOk()));

        verify(courseService, times(1)).updateCourse(any());
    }

    @Test
    void updateCourse_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Course", "C1")).when(courseService).updateCourse(any());

        mockMvc.perform(put("/courses/C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"courseId":"C1","courseTitle":"C Programming","courseSemester":1}
                        """))
                .andExpect((status().isNotFound()))
                .andExpect(jsonPath("$.status").value(404));

        verify(courseService, times(1)).updateCourse(any());
    }

    @Test
    void updateCourse_withEmptyBody_returns400AndDoesNotCallTheService() throws Exception
    {
        mockMvc.perform(put("/courses/C1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

        verify(courseService, never()).updateCourse(any());
    }

    @Test
    void deleteCourse_whenDeleted_returns204() throws Exception
    {
        mockMvc.perform(delete("/courses/C1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService, times(1)).deleteCourseSafely("C1");
    }

    @Test
    void deleteCourse_whenMissing_returns404() throws Exception
    {
        doThrow(new EntityNotFoundException("Course", "C1")).when(enrollmentService).deleteCourseSafely("C1");

        mockMvc.perform(delete("/courses/C1"))
                .andExpect(status().isNotFound());

        verify(enrollmentService, times(1)).deleteCourseSafely("C1");
    }
}

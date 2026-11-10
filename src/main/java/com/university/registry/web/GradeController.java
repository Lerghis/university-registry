package com.university.registry.web;

import com.university.registry.model.Grade;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.GradeService;
import com.university.registry.web.dto.GradeRequest;
import com.university.registry.web.dto.GradeResponse;
import com.university.registry.web.dto.GradeUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GradeController
{
    private final GradeService gradeService;
    private final EnrollmentService enrollmentService;

    public GradeController(GradeService gradeService, EnrollmentService enrollmentService)
    {
        this.gradeService = gradeService;
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/students/{am}/grades")
    @ResponseStatus(HttpStatus.CREATED)
    public GradeResponse addGrade(@PathVariable String am, @RequestBody GradeRequest request)
    {
        gradeService.recordGrade(am, request.courseId(), request.value());

        return new GradeResponse(am, request.courseId(), request.value());
    }

    @GetMapping("/students/{am}/grades")
    public List<GradeResponse> gradesOfStudent(@PathVariable String am)
    {
        List<GradeResponse> result = new ArrayList<>();
        for (Grade grade : gradeService.getGradesForStudent(am))
        {
            result.add(GradeResponse.from(grade));
        }

        return result;
    }

    @PutMapping("/students/{am}/grades/{courseId}")
    public GradeResponse updateGrade(@PathVariable String am, @PathVariable String courseId, @RequestBody GradeUpdateRequest request)
    {
        Grade grade = new Grade(am, courseId, request.value());
        gradeService.updateGrade(grade);

        return GradeResponse.from(grade);
    }

    @DeleteMapping("/students/{am}/grades/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGrade(@PathVariable String am, @PathVariable String courseId)
    {
        gradeService.deleteGrade(am, courseId);
    }

    @GetMapping("/students/{am}/average")
    public Float studentAverage(@PathVariable String am)
    {
        return gradeService.getStudentAverage(am);
    }

    @GetMapping("/courses/{courseId}/average")
    public Float courseAverage(@PathVariable String courseId)
    {
        return gradeService.getCourseAverage(courseId);
    }
}

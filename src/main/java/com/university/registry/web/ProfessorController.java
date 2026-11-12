package com.university.registry.web;

import com.university.registry.model.Professor;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.ProfessorService;
import com.university.registry.web.dto.ProfessorRequest;
import com.university.registry.web.dto.ProfessorResponse;
import com.university.registry.web.dto.ProfessorUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST endpoints for professors under {@code /professors}.
 * <p>
 * A thin translator: it converts the request into Java objects, calls the
 * services and converts the result into a response. It contains no business
 * rules. Deletion goes through {@link EnrollmentService#deleteProfessorSafely(String)},
 * so a professor who is still assigned cannot be removed.
 */
@RestController
@RequestMapping("/professors")
public class ProfessorController
{
    private final ProfessorService professorService;
    private final EnrollmentService enrollmentService;

    public ProfessorController(ProfessorService professorService, EnrollmentService enrollmentService)
    {
        this.professorService = professorService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public List<ProfessorResponse> list()
    {
        List<ProfessorResponse> result = new ArrayList<>();
        for (Professor professor : professorService.getAllProfessors())
        {
            result.add(ProfessorResponse.from(professor));
        }

        return result;
    }

    @GetMapping("/{profId}")
    public ProfessorResponse get(@PathVariable String profId)
    {
        return ProfessorResponse.from(professorService.getProfById(profId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessorResponse create(@Valid  @RequestBody ProfessorRequest request)
    {
        Professor professor = new Professor(request.profId(), request.specialty(), request.name(), request.email(), request.phone());
        professorService.addProfessor(professor);

        return ProfessorResponse.from(professor);
    }

    @PutMapping("/{profId}")
    public ProfessorResponse update(@PathVariable String profId, @Valid @RequestBody ProfessorUpdateRequest request)
    {
        Professor professor = new Professor(profId, request.specialty(), request.name(), request.email(), request.phone());
        professorService.updateProfessor(professor);

        return ProfessorResponse.from(professor);
    }

    @DeleteMapping("/{profId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String profId)
    {
        enrollmentService.deleteProfessorSafely(profId);
    }
}

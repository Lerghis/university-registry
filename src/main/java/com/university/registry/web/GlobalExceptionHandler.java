package com.university.registry.web;

import com.university.registry.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Turns exceptions into HTTP responses for every controller, in one place.
 * <p>
 * Domain errors map to 404, 409 or 400 and carry their own user-facing
 * message. Database failures answer 500 with a generic message; the details
 * go to the log.
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EntityNotFoundException.class, NoGradesRecordedException.class})
    public ProblemDetail handleNotFound(RegistryException ex)
    {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({DuplicateEntityException.class, EntityInUseException.class})
    public ProblemDetail handleConflict(RegistryException ex)
    {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({InvalidSemesterException.class, InvalidGradeException.class})
    public ProblemDetail handleInvalid(RegistryException ex)
    {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleConstraint(DataIntegrityViolationException ex)
    {
        log.warn("A database constraint was violated", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Η ενέργεια δεν μπορεί να ολοκληρωθεί λόγω υπαρχόντων δεδομένων.");
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDatabase(DataAccessException ex)
    {
        log.error("Database failure", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Σφάλμα βάσης δεδομένων. Δοκιμάστε ξανά.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex)
    {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors())
        {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Μη έγκυρα δεδομένα αίτησης.");
        problem.setProperty("errors", errors);
        return problem;
    }
}

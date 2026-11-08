package com.university.registry.web;

import com.university.registry.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}

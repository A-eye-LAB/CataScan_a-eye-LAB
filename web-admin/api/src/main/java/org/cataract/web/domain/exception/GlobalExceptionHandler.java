package org.cataract.web.domain.exception;

import jakarta.validation.ConstraintViolationException;
import org.cataract.web.presentation.dto.responses.ErrorResponseDto;
import org.paseto4j.commons.PasetoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");
            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not authorized to access this resource");
        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The Token signature is invalid");
        }

        if (exception instanceof PasetoException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The Paseto token has expired");
        }

        if (errorDetail == null) {
            System.out.println(exception.getMessage());
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");
        }

        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ) {
        ErrorResponseDto errorMessageDto = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageDto);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNoHandlerFoundException(
            NoHandlerFoundException ex
    ) {
        ErrorResponseDto errorMessageDto = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessageDto);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePatientNotFoundException(
            PatientNotFoundException ex
    ) {
        ErrorResponseDto errorMessageDto = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessageDto);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(
            UserNotFoundException ex
    ) {
        ErrorResponseDto errorMessageDto = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessageDto);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorResponseDto response = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append("'").append(error.getField()).append("'").append(":").append("'").append(error.getDefaultMessage())
                    .append("',");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        ErrorResponseDto response = new ErrorResponseDto("Validation failed ," + sb);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadUploadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadUploadRequestExceptions(BadUploadRequestException ex) {
        ErrorResponseDto errorMessageDto = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageDto);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTokenExceptions(InvalidTokenException ex) {
        ErrorResponseDto errorMessageDto = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageDto);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsExceptions(BadCredentialsException ex) {
        ErrorResponseDto errorMessageDto = new ErrorResponseDto(ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessageDto);
    }

}
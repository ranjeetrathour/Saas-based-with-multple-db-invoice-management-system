package com.moptra.invoice.comman.exception.exceptionHandling;

import com.moptra.invoice.comman.exception.GenericException;
import com.moptra.invoice.comman.exception.res.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandling {

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponse> handleGenericException(GenericException genericException) {
        return ResponseEntity.status(genericException.getStatusCode())
                .body(new ErrorResponse(genericException.getErrorMessage(), genericException.getStatusCode()));
    }
}

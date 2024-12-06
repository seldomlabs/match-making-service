package com.matchmaker.exceptionhandler;

import com.matchmaker.common.dto.MPResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.common.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MatchMakingExceptionHandler {

    @ExceptionHandler({BadRequestException.class})
    public final ResponseEntity<MPResponse> handleException(BadRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        MPResponse mpResponse = new MPResponse();
        mpResponse.setMessage(ex.getMessage());
        mpResponse.setStatus(MPResponseStatus.FAILURE.name());
        return new ResponseEntity<>(mpResponse, status);
    }

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<MPResponse> handleException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        MPResponse mpResponse = new MPResponse();
        mpResponse.setMessage(ex.getMessage());
        mpResponse.setStatus(MPResponseStatus.FAILURE.name());
        return new ResponseEntity<>(mpResponse, status);
    }
}

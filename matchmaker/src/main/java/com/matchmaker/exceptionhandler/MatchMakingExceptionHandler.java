package com.matchmaker.exceptionhandler;

import com.matchmaker.common.dto.MPResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.common.exception.BadRequestException;
import com.matchmaker.service.impl.MatchServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class MatchMakingExceptionHandler {

    Logger logger = LogManager.getLogger(MatchServiceImpl.class);

    @ExceptionHandler({BadRequestException.class})
    public final ResponseEntity<MPResponse> handleException(BadRequestException ex) {
        logger.error("Exception occurred : ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        MPResponse mpResponse = new MPResponse();
        mpResponse.setMessage(ex.getMessage());
        mpResponse.setStatus(MPResponseStatus.FAILURE.name());
        return new ResponseEntity<>(mpResponse, status);
    }

    @ExceptionHandler({Exception.class})
    public final ResponseEntity<MPResponse> handleException(Exception ex) {
        logger.error("Exception occurred : ", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        MPResponse mpResponse = new MPResponse();
        mpResponse.setMessage(ex.getMessage());
        mpResponse.setStatus(MPResponseStatus.FAILURE.name());
        return new ResponseEntity<>(mpResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MPResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Exception occurred : ", ex);
        StringBuilder message = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            message.append(String.format("%s for %s",
                    fieldError.getDefaultMessage(), fieldError.getField())).append(",");
        });
        MPResponse mpResponse = new MPResponse();
        mpResponse.setStatus(MPResponseStatus.FAILURE.name());
        mpResponse.setMessage(message.toString());
        return new ResponseEntity<>(mpResponse, HttpStatus.BAD_REQUEST);
    }
}

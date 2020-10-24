package pl.com.karwowsm.musiqueue.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletResponse response) throws IOException {
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        log.debug("Responding with DataIntegrityViolationException: {}", message);
        response.sendError(HttpServletResponse.SC_CONFLICT, message);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletResponse response) throws IOException {
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        log.debug("Responding with MaxUploadSizeExceededException: {}", message);
        response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(RuntimeException e) {
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        log.debug("Responding with {}: {}", e.getClass().getSimpleName(), message);
        throw e;
    }
}

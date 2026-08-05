package com.rapid7.nexpose.nsc.exception;

import com.rapid7.nexpose.console.exception.NexposeException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Central error handling: renders {@code error.jsp} with a stable error code, the
 * message, and (for the POC) the stack trace so testers can correlate the UI error
 * with the log lines the defect analysis system ingests.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NexposeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleNexpose(NexposeException ex, HttpServletRequest request, Model model) {
        log.error("Nexpose error [{}] handling {} : {}",
                ex.getErrorCode(), request.getRequestURI(), ex.getMessage(), ex);
        model.addAttribute("errorCode", ex.getErrorCode());
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("stackTrace", stackTraceOf(ex));
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneric(Exception ex, HttpServletRequest request, Model model) {
        log.error("Unhandled {} handling {} : {}",
                ex.getClass().getName(), request.getRequestURI(), ex.getMessage(), ex);
        model.addAttribute("errorCode", ex.getClass().getSimpleName());
        model.addAttribute("message", ex.getMessage() == null ? ex.toString() : ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("stackTrace", stackTraceOf(ex));
        return "error";
    }

    private String stackTraceOf(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

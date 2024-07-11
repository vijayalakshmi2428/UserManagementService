package com.user.management.exception;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.user.management.payload.ErrorInfo;




@RestControllerAdvice
public class UserAppGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	
	@ExceptionHandler(value = {UserNotFoundException.class, RoleNotFoundException.class})
	public ResponseEntity<ErrorInfo> handleResourceNotFoundException(UserNotFoundException exception,
			WebRequest webRequest) {

		ErrorInfo errordetails = new ErrorInfo(new Date(), exception.getMessage(), webRequest.getDescription(false));
		
		return new ResponseEntity<>(errordetails, HttpStatus.NOT_FOUND);

	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> handleApplicationException(Exception exception,
			WebRequest webRequest) {

		ErrorInfo errordetails = new ErrorInfo(new Date(), "Application Error : "+exception.getMessage(), webRequest.getDescription(false));
		
		return new ResponseEntity<>(errordetails, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	// global exception
	@ExceptionHandler(UserAuthApplicationException.class)
	public ResponseEntity<ErrorInfo> handleBlogExceptionn(UserAuthApplicationException exception, WebRequest webRequest) {

		ErrorInfo errordetails = new ErrorInfo(new Date(), exception.getMessage(),
				webRequest.getDescription(false));

		return new ResponseEntity<>(errordetails, HttpStatus.UNAUTHORIZED);

	}
	
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();
 
        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorInfo> handleAccessDeniedException(AccessDeniedException exception,
			WebRequest webRequest) {

		ErrorInfo errordetails = new ErrorInfo(new Date(), exception.getMessage(), webRequest.getDescription(false));
		
		return new ResponseEntity<>(errordetails, HttpStatus.UNAUTHORIZED);

	}
}

package scot.ianmacdonald.cakemgr.rest.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CakeServiceErrorAdvice {

	@ResponseBody
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<CakeServiceError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		String error = "It is forbidden to create a Cake with a duplicate title";
		return new ResponseEntity<CakeServiceError>(new CakeServiceError(HttpStatus.FORBIDDEN, error, ex),
				HttpStatus.FORBIDDEN);
	}
	
	@ResponseBody
	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<CakeServiceError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		String error = "The JSON message in the HTTP request was malformed";
		return new ResponseEntity<CakeServiceError>(new CakeServiceError(HttpStatus.BAD_REQUEST, error, ex),
				HttpStatus.BAD_REQUEST);
	}

}

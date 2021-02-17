package scot.ianmacdonald.cakemgr.rest.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import scot.ianmacdonald.cakemgr.rest.model.CakeManagerError;

@ControllerAdvice
public class CakeManagerErrorAdvice {

	@ResponseBody
	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<CakeManagerError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		String error = "It is forbidden to create a Cake with a duplicate title";
		return new ResponseEntity<CakeManagerError>(new CakeManagerError(HttpStatus.FORBIDDEN, error, ex),
				HttpStatus.FORBIDDEN);
	}
	
	@ResponseBody
	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<CakeManagerError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		String error = "The JSON message in the HTTP request was malformed";
		return new ResponseEntity<CakeManagerError>(new CakeManagerError(HttpStatus.BAD_REQUEST, error, ex),
				HttpStatus.BAD_REQUEST);
	}

}

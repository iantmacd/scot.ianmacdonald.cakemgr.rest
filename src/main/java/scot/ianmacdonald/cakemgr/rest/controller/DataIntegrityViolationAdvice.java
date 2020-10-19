package scot.ianmacdonald.cakemgr.rest.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DataIntegrityViolationAdvice {

	@ResponseBody
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	String dataIntegrityViolationHandler(DataIntegrityViolationException ex) {
		return "It is forbidden to create a Cake with a duplicate TITLE: " + ex.getMessage();
	}

}

package scot.ianmacdonald.cakemgr.rest.controller;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

class CakeServiceError {

	private HttpStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private String message;
	private String debugMessage;

	CakeServiceError(HttpStatus status) {
		this.status = status;
	}

	CakeServiceError(HttpStatus status, Throwable ex) {
		this.status = status;
		this.message = "Unexpected error";
		this.debugMessage = ex.getLocalizedMessage();
	}

	CakeServiceError(HttpStatus status, String message, Throwable ex) {
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}
}
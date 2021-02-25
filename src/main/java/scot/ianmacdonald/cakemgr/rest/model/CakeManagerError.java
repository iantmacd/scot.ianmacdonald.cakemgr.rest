package scot.ianmacdonald.cakemgr.rest.model;

import org.springframework.http.HttpStatus;

public class CakeManagerError {

	private HttpStatus status;
	
	private String message;
	
	private String debugMessage;

	public CakeManagerError(HttpStatus status, String message, Throwable ex) {
		
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
package se.opendataexchange.common;

import java.io.Serializable;

public class ErrorInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7836746354670168331L;
	private String message;
	
	public ErrorInfo(String msg) {
		super();
		this.message = msg;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}

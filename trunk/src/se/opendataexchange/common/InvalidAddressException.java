package se.opendataexchange.common;

public class InvalidAddressException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2448479808806365219L;

	public InvalidAddressException() {
	}

	public InvalidAddressException(String arg0) {
		super(arg0);
	}

	public InvalidAddressException(Throwable arg0) {
		super(arg0);
	}

	public InvalidAddressException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}

package eu.superhub.wp4.monitor.eventbus.exception;

public class EventBusConnectionException extends Exception {

	public EventBusConnectionException(Exception cause) {
		this.initCause(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4983637008651236725L;

}

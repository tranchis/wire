package net.sf.ictalive.eventbus.exception;

public class EventBusConnectionException extends Exception
{

	public EventBusConnectionException(Exception cause)
	{
		this.initCause(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4983637008651236725L;

}

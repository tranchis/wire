package apapl;

/**
 * Signals that an action has failed during execution. This exception
 * is used in the implementation of different plans to determine the
 * failure of an action.
 */
public class ActionFailedException extends Exception
{
	/**
	 * Constructs an action failed exception with a specific message that 
	 * provides more information about the exception.
	 * 
	 * @param message the detail message
	 */
	public ActionFailedException(String message)
	{
		super(message);
	}
}
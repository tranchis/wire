package apapl;

/**
 * Signals that an error occurred during the instantiation of the module.
 */
public class CreateModuleException extends Exception
{

	/**
	 * Constructs a create module exception.
	 * 
	 * @param message the message describing what has caused the exception
	 */
	public CreateModuleException(String message)
	{
		super(message);
	}
}

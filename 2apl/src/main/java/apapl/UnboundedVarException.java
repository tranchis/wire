package apapl;

/**
 * Signals that a term contains an unbounded variable while it should have been grounded.
 */
public class UnboundedVarException extends Exception
{
	/**
	 * Constructs an UnboundedVarException.
	 * 
	 * @param message message providing extra information.
	 */
	public UnboundedVarException(String message)
	{
		super(message);
	}
}
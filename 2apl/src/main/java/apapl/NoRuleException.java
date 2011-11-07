package apapl;

/**
 * Signals that there was no rule corresponding to an abstract action or belief update.
 * Note that this is different from no rule being applicable. In the latter case
 * a rule is specified, while in the first case there is no rule at all.
 */
public class NoRuleException extends Exception
{
	/**
	 * Constructs a NoRuleException.
	 * 
	 * @param message messages containing extra information
	 */
	public NoRuleException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructs a NoRuleException.
	 */
	public NoRuleException()
	{
	}
}
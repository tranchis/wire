package msagent;

/***
 * The MSAgentException is thrown when an error occurred while controlling the MSAgents.
 * For example when a character is created twice.
 * 
 * @see MSAgent
 * @author Ramon Hagenaars
 */
public class MSAgentException extends Exception
{
	/***
	 * Default constructor; simply creates an empty MSAgentException.
	 */
	public MSAgentException()
	{
		super();
	}
	
	/***
	 * Constructor with a specified message.
	 * @param message the message which is shown when this MSAgentException occurs
	 */
	public MSAgentException(String message)
	{
		super(message);
	}
}

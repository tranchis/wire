package msagent;

/***
 * Every object that implements this interface will receive a notification when an Microsoft Agent action was performed.
 * Remember to add the listener to a MSAgent object in order to receive notifications for the regarding MSAgent. 
 * 
 * @see MSAgentActionEvent
 * @see MSAgent
 * @author Ramon Hagenaars
 */
public interface MSAgentListener 
{
	/***
	 * This method will be called after execution of a Microsoft Agent action.
	 * @param msAgentActionEvent an MSAgentActionEvent object containing information about the execution performed
	 */
	public void actionPerformed(MSAgentActionEvent msAgentActionEvent);
}

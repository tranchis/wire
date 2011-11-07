package msagent;

/***
 * The MSAgentActionEvent objects are passed as arguments to an MSAgentListener.
 * When a Microsoft Agent finished performing an action, an animation for example, 
 * all listeners that are registered to the corresponding MSAgent object are notified
 * and receive an object of this class.
 * 
 * @see MSAgentListener
 * @see MSAgent
 * @author Ramon Hagenaars
 */
public class MSAgentActionEvent
{
	public static final int INITIAL = 0;
	public static final int CREATEAVATAR = 1;
	public static final int SHOW = 2;
	public static final int HIDE = 3;
	public static final int SPEAKAUDIO = 4;
	public static final int SPEAKTEXT = 5;
	public static final int THINK = 6;
	public static final int MOVETO = 7;
	public static final int GESTUREAT = 8;
	public static final int PLAYANIMATION = 9;
	public static final int SETLANGUAGEID = 10;
	public static final int DETACH = 11;
	
	private int type;
	private MSAgent performer;
	private Object[] arguments;
	
	/***
	 * Constructor; initializes a new MSAgentActionEvent.
	 * @param type the type of this event as an int
	 * @param performer the MSAgent object which performed the action
	 * @param arguments the eventual arguments passed
	 */
	public MSAgentActionEvent(int type, MSAgent performer, Object... arguments)
	{
		this.type = type;
		this.performer = performer;
		this.arguments = arguments;
	}
	
	/***
	 * Returns the type of this MSAgentActionEvent.
	 * @return an int which represents the type of this event
	 */
	public int getType()
	{
		return type;
	}
	
	/***
	 * Returns the type of this MSAgentActionEvent as a String.
	 * Possible outputs could be: "HIDE" or "SPEAKAUDIO".
	 * @return a String representing the type of this MSAgentActionEvent
	 */
	public String getTypeAsString()
	{
		switch (type)
		{
			case INITIAL:
				return "INITIAL";
			case CREATEAVATAR:
				return "CREATEAVATAR";
			case SHOW:
				return "SHOW";
			case HIDE:
				return "HIDE";
			case SPEAKAUDIO:
				return "SPEAKAUDIO";
			case SPEAKTEXT:
				return "SPEAKTEXT";
			case THINK:
				return "THINK";
			case MOVETO:
				return "MOVETO";
			case GESTUREAT:
				return "GESTUREAT";
			case PLAYANIMATION:
				return "PLAYANIMATION";
			case SETLANGUAGEID:
				return "SETLANGUAGEID";
			case DETACH:
				return "DETACH";
		}
		
		return "undefined type";
	}
	
	/***
	 * Returns the MSAgent which performed the action.
	 * @return an MSAgent object
	 */
	public MSAgent getPerformer()
	{
		return performer;
	}
	
	/***
	 * Returns the given arguments.
	 * @return an array of objects
	 */
	public Object[] getArguments()
	{
		return arguments;
	}
	
	/***
	 * Returns a String containing the information of this MSAgentActionEvent.
	 */
	public String toString()
	{
		String result = 
			"MSAgentActionEvent\n" +
			"\tType: " + getTypeAsString() + "\n" +
			"\tPerformer: " + performer + "\n";
		for (Object object : arguments)
			result += "\targument: " + object + "\n";
		
		return result;
	}
}

package data;

import java.util.Collection;
import java.util.HashMap;

/***
 * The Action class represent a virtual action that the user can perform.
 * Examples of actions could be: "stir", "shake", "turn fire on".
 * @author Ramon Hagenaars
 *
 */
public class Action
{
	private static HashMap<String, Action> actions = new HashMap<String, Action>();
	
	private String name;
	private String title;
	
	/***
	 * Returns all instantiated Actions.
	 * @return A Collection containing Action objects
	 */
	public static Collection<Action> getActions()
	{
		return actions.values();
	}
	
	public static boolean doesActionExist(String actionName)
	{
		return actions.containsKey(actionName);
	}
	
	public static Action getAction(String actionName)
	{
		return actions.get(actionName);
	}
	
	/***
	 * Constructor; initializes a new Action object.
	 * @param name the name of the Action
	 * @param title the title of this action as shown in the program
	 */	
	public Action(String name, String title)
	{
		this.name = name;
		this.title = title;

		actions.put(name, this);
	}
	
	/***
	 * Returns the name of this Action.
	 * @return the name as a String
	 */
	public String getName()
	{
		return name;
	}
	
	public String getTitle()
	{
		return title;
	}
}

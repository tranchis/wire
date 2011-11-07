package data;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class Tool extends Item
{
	private static HashMap<String, Tool> tools = new HashMap<String, Tool>();
	
	private int capacity;
	private Action[] actions;
	
	/***
	 * Returns all tools.
	 * @return A Collection containing Collection objects
	 */
	public static Collection<Tool> getTools()
	{
		return tools.values();
	}
	
	public static Tool getToolByActionName(String actionName)
	{
		for (Tool tool : tools.values())
			if (tool.hasAction(actionName))
				return tool;
		
		return null;
	}
	
	public static boolean doesToolExist(String toolName)
	{
		return tools.containsKey(toolName);
	}
	
	/*public static boolean hasAction(Tool toolName, String actionName)
	{
		if (!tools.containsKey(toolName))
			return false; //TODO misschien nog even naar kijken
		
		return tools.get(toolName).hasAction(actionName);
	}
	
	public static boolean hasCapacity(Tool toolName, int capacity)
	{
		if (!tools.containsKey(toolName))
			return false; //TODO misschien nog even naar kijken
		
		return tools.get(toolName).hasCapacity(capacity);
	}	*/
	
	/***
	 * Constructor; initializes a new Tool object.
	 * @param name the name of the Tool
	 * @param capacity the capacity of this Tool. If it is not equals to -1, this Tool is a container
	 * @param imageIcon the icon of this Tool, which may be null
	 * @param actions the names of the actions which can be performed with this Tool
	 */
	public Tool(String name, String title, int capacity, ImageIcon imageIcon, Action... actions) 
	{
		super(name, title, imageIcon);
		
		this.capacity = capacity;
		this.actions = actions;
		
		tools.put(name, this);
	}
	
	/***
	 * Returns the capacity this Tool has.
	 * @return the capacity as an integer
	 */
	public int getCapacity()
	{
		return capacity;
	}
	
	/***
	 * Returns a Prolog notation of this Tool object.
	 * It will have the following form: <code>name ( capacity )</code>
	 * @return a String containing the Prolog notation
	 */
	public String toProlog()
	{
		return getName() + " ( " + capacity + " )";	
	}
	
	public Action[] getActions()
	{
		return actions;
	}
	
	public boolean hasAction(String actionName)
	{
		for (Action action : actions)
			if (action.getName().equals(actionName))
				return true;
		return false;
	}
	
	public Action getActionByTitle(String actionTitle)
	{
		for (Action action : actions)
			if (action.getTitle().equals(actionTitle))
				return action;
		return null;
	}
	
	public boolean hasCapacity(int capacity)
	{
		return this.capacity >= capacity;
	}
}

package data;

public class ToolInRecipe
{
	private String name;
	//private Action action;
	private int capacity;
	
	public ToolInRecipe(String name, /*, Action action, */int capacity)
	{
		this.name = name;
		//this.action = action;
		this.capacity = capacity;
	}
	
	public String getName()
	{
		return name;
	}
	
	//public Action getAction()
	//{
	//	return action;
	//}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public String toString()
	{
		String result = "";
		
		if (name != null)
			result = name;
		else
		//	result = "Tool( " + action.getName() + " )";
			result = "Tool";
		
		if (capacity != -1)
			result = "Tool( capacity( " + capacity + " ) )";
		
		return result;
	}
}

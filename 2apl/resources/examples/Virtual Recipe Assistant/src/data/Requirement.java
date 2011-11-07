package data;

/***
 * A Requirement determines the capacity we need for a tool.
 * It says something like: "we need any tool that has capacity X".
 * 
 * @author Ramon Hagenaars
 *
 */
public class Requirement 
{
	private int id;
	private int capacity;
	
	public Requirement(int id, int capacity)
	{
		this.id = id;
		this.capacity = capacity;
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
}

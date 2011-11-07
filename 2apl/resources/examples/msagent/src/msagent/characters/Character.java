package msagent.characters;

/***
 * This is a superclass for MS Agent characters.
 * 
 * @see msagent.MSAgent
 * @see Genie
 * @see Merlin
 * @see Peedy
 * @see Robby
 * @author Ramon Hagenaars
 */
public abstract class Character 
{
	/***
	 * The name of the Character.
	 */
	protected String name;
	
	/***
	 * Constructor; instantiates a Character object.
	 * @param name the name of the MS Agent
	 */
	protected Character(String name)
	{
		this.name = name;
	}

	/***
	 * Returns the name of this Character.
	 * @return the name of this Character as String
	 */
	public String getName()
	{
		return name;
	}
}

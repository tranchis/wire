package msagent.characters;

import java.util.HashMap;

/***
 * This class represents an animation which the Microsoft agents can perform, like reading, searching or waving.
 *
 * @see msagent.MSAgent
 * @see Genie
 * @see Merlin
 * @see Peedy
 * @see Robby
 * @author Ramon Hagenaars
 */
public class Animation 
{
	private String name;
	private Character character;
	
	private static HashMap<String, Animation> animations;
	
	/***
	 * Constructor; visible within the package only.
	 * @param name the name of the Animation
	 * @param character the Character that may perform this Animation
	 */
	protected Animation(String name, Character character)
	{
		if (animations == null)
			animations = new HashMap<String, Animation>();
		
		this.name = name;
		this.character = character;

		animations.put(character.getName() + "." + name.toUpperCase(), this);
	}
	
	/***
	 * Returns an Animation object which corresponds to the given Character's name and Animation name.
	 * <xmp>
	 * agentPeedy.perform(Animation.getAnimation(Peedy.getInstance(), "blink")); // Will successfully perform the blink-animation
	 * agentPeedy.perform(Animation.getAnimation(Peedy.getInstance(), "not_existing_animation")); // Will throw a NullPointerException in the perform method
	 * </xmp>
	 * @param character the Character of which the Animation is required
	 * @param name the name of the Animation, which is case insensitive
	 * @return an Animation object or null if there is no Animation which corresponds to the given arguments
	 */
	public static Animation getAnimation(Character character, String name)
	{
		return animations.get(character.getName() + "." + name.toUpperCase());
	}
	
	/***
	 * Returns the name of this Animation.
	 * @return the name of this Animation as a String
	 */
	public String getName()
	{
		return name;
	}
	
	/***
	 * Returns the Character object that may perform this Animation.
	 * @return the Character object that may perform this Anitmation
	 */
	public Character getCharacter()
	{
		return character;
	}
}

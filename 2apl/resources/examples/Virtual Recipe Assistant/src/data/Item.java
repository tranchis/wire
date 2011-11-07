package data;

import javax.swing.ImageIcon;

/***
 * An Item can be used when cooking a virtual recipe.
 * @see {@link Ingredient}, {@link Tool}
 * @author Ramon Hagenaars
 *
 */
public abstract class Item 
{
	private String name;
	private String title;
	private ImageIcon imageIcon;
	
	/***
	 * Constructor; initializes a new Item object.
	 * @param name the name of the Item
	 * @param actions the names of the Actions which are related to this Item
	 */
	public Item(String name, String title, ImageIcon imageIcon)
	{
		this.name = name;
		this.title = title;
		this.imageIcon = imageIcon;
	}
	
	/***
	 * Returns the name of this Item.
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
	
	public ImageIcon getImageIcon()
	{
		return imageIcon;
	}
}

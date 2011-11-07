package data;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class Ingredient extends Item
{
	private static HashMap<String, Ingredient> ingredients = new HashMap<String, Ingredient>();
	
	private String unit;
	private int amounts;
	private double capacity;
	
	/***
	 * Returns all instantiated Ingredients.
	 * @return A Collection containing Ingredient objects
	 */
	public static Collection<Ingredient> getIngredients()
	{
		return ingredients.values();
	}
	
	public static boolean doesIngredientExist(String ingredientName)
	{
		return ingredients.containsKey(ingredientName);
	}
	
	public static Ingredient getIngredient(String ingredientName)
	{
		return ingredients.get(ingredientName);
	}
	
	/***
	 * Constructor; initializes a new Ingredient object.
	 * @param name the name of the Ingredient which is used to refer to this Ingredient
	 * @param title the title which is shown in the game
	 * @param actions the names of the actions which can be performed on this Ingredient
	 */
	public Ingredient(String name, String title, String unit, int amounts, double capacity, ImageIcon imageIcon)
	{
		super(name, title, imageIcon);
		
		this.unit = unit;
		this.amounts = amounts;
		this.capacity = capacity;

		ingredients.put(name, this);
	}
	
	public String getUnit()
	{
		return unit;
	}
	
	public int getAmounts()
	{
		return amounts;
	}
	
	public double getCapacity()
	{
		return capacity;
	}
	
	public String toString()
	{
		return "Ingredient( " + getName() + " )";
	}
}

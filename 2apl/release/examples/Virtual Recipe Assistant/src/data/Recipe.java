package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Recipe 
{
	private static HashMap<String, Recipe> recipies = new HashMap<String, Recipe>();
	
	private boolean parsed;
	private String name;
	private String title;
	private ArrayList<ActionInRecipe> actions;
	private Requirement[] requirements;
	
	public static Collection<Recipe> getRecipies()
	{
		return recipies.values();
	}
	
	public static Recipe getRecipe(String name)
	{
		return recipies.get(name);
	}
	
	public static Recipe getRecipeByTitle(String title)
	{
		for (Recipe recipe : recipies.values())
			if (recipe.getTitle().equals(title))
				return recipe;
			
		return null;
	}
	
	public Recipe(String name, String title, Requirement[] requirements, ArrayList<ActionInRecipe> actions)
	{
		this.parsed = false;
		this.name = name;
		this.title = title;
		this.requirements = requirements;
		this.actions = actions;
		
		recipies.put(name, this);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public ArrayList<ActionInRecipe> getActions()
	{
		return actions;
	}
	
	public Requirement[] getRequirements()
	{
		return requirements;
	}
	
	public void parse()
	{
		if (parsed)
			return;
		
		ArrayList<ActionInRecipe> buffer = new ArrayList<ActionInRecipe>();
		
		for (int i = 0; i < actions.size(); i ++)
		{
			if (actions.get(i).getName().equals("include"))
			{
				// TODO throw exception when recipe does not exist

				Recipe r = Recipe.getRecipe(actions.get(i).getTool().getName());
				
				r.parse();
				
				buffer.addAll(r.getActions());
			}
			else
				buffer.add(actions.get(i));
		}
		
		actions = buffer;
		parsed = true;
	}
	
	public String toString()
	{
		String result = "Recipe(" + name + ") = { ";
		boolean b = false;
		for (ActionInRecipe action : actions)
		{
			if (b)
				result += ", ";
			result += action;
			
			b = true;
		}
		
		result += " }";
		
		return result;
	}
}

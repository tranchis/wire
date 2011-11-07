package data;

/***
 * The difference between an ActionInRecipe and a normal Action is that
 * the ActionInRecipe was read from a recipe, while the Action was read from a tool.
 * @author Ramon Hagenaars
 *
 */
public class ActionInRecipe
{
	private int amount;
	private String recipe;
	private String name;
	private String comment;
	private ToolInRecipe tool;
	private Ingredient ingredient;
	
	public ActionInRecipe(String recipe, String name, String comment, ToolInRecipe tool, Ingredient ingredient, int amount)
	{
		this.recipe = recipe;
		this.name = name;
		this.comment = comment;
		this.tool = tool;
		this.ingredient = ingredient;
		this.amount = amount;
	}
	
//	public int getStep()
//	{
//		return step;
//	}
	
	public String getRecipe()
	{
		return recipe;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public ToolInRecipe getTool()
	{
		return tool;
	}
	
	public Ingredient getIngredient()
	{
		return ingredient;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	public String toString()
	{
		String result = name;
		if (tool != null && ingredient != null)
			result += "( " + tool + ", " + ingredient + " )";
		else if (tool != null)
			result += "( " + tool + " )";
		
		return result;
	}
}

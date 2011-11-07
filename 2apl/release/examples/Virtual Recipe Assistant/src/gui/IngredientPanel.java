package gui;

import java.awt.event.MouseEvent;

import virtualRecipeAssistantEnvironment.VirtualRecipeAssistantEnvironment;

import data.Ingredient;
import data.Item;
import data.Percepts;
import eis.iilang.Identifier;
import eis.iilang.Percept;

@SuppressWarnings("serial")
public class IngredientPanel extends ItemPanel
{
	public IngredientPanel(Item item)
	{
		super(item);
	}
	
	public void mousePressed(MouseEvent me) 
	{
		VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
			Percepts.INGREDIENTSELECTED, 
			new Identifier(this.item.getName())
		));
		
		DraggedIngredient draggedIngredient = DraggedIngredient.getInstance();
		draggedIngredient.setIngredient((Ingredient)item);
		draggedIngredient.setVisible(true);
	}
}

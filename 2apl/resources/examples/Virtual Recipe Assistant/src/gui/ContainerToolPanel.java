package gui;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import data.Ingredient;
import data.Item;
import data.Tool;

@SuppressWarnings("serial")
public class ContainerToolPanel extends ItemPanel
{
	private boolean hovered;
	private ToolContentDialog toolContentDialog;
	private static ArrayList<ContainerToolPanel> containerToolPanels = new ArrayList<ContainerToolPanel>();
	
	public static ContainerToolPanel checkHover(int x, int y)
	{
		for (ContainerToolPanel containerToolPanel : containerToolPanels)
		{
			if (x >= containerToolPanel.getX() && 
			y >= containerToolPanel.getY() && 
			x <= containerToolPanel.getX() + containerToolPanel.getWidth() && 
			y <= containerToolPanel.getY() + containerToolPanel.getHeight())
			{
				containerToolPanel.mouseOver();
				
				return containerToolPanel;
			}
			else if (containerToolPanel.hovered)
				containerToolPanel.mouseOut();
		}
		
		return null;
	}
	
	public ContainerToolPanel(Item item) 
	{
		super(item);
		
		hovered = false;
		containerToolPanels.add(this);
		
		toolContentDialog = new ToolContentDialog((Tool)item);
	}
	
	public boolean addIngredient(Ingredient ingredient, int amount)
	{
		if (toolContentDialog.addIngredient(ingredient, amount))
		{
			toolContentDialog.setVisible(true);
			return true;
		}
		
		return false;
	}
	
	public void destroy()
	{
		toolContentDialog.removeAll();
		toolContentDialog.setVisible(false);
		containerToolPanels.remove(this);
	}
	
	public void mouseOver()
	{
		hovered = true;
		
		ToolTipPanel.getInstance().print("insert " + DraggedIngredient.getInstance().getAmount() + " " + DraggedIngredient.getInstance().getIngredient().getUnit() + " " + DraggedIngredient.getInstance().getIngredient().getTitle() + " into this " + item.getTitle());
		if (!selected)
			select();
	}
	
	public void mouseOut()
	{
		hovered = false;
		
		ToolTipPanel.getInstance().clear();
		deSelect();
	}
	
	@Override
	public void mousePressed(MouseEvent me)
	{
		if (!toolContentDialog.isVisible())
			toolContentDialog.setVisible(true);
		else
			toolContentDialog.setVisible(false);
	}
}

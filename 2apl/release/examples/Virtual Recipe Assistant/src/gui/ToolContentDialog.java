package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;

import virtualRecipeAssistantEnvironment.VirtualRecipeAssistantEnvironment;

import data.Ingredient;
import data.Percepts;
import data.Tool;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.Percept;

@SuppressWarnings("serial")
public class ToolContentDialog extends JDialog 
{
	private double capacity;
	private Tool tool;
	private HashMap<String, ContentItem> contentItems;
	
	void removeIngredient(ContentItem contentItem)
	{
		VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
			Percepts.INGREDIENTREMOVED, 
			new Identifier(contentItem.getIngredient().getTitle()),
			new Identifier(tool.getName())
		));		
		
		contentItems.remove(contentItem.getIngredient().getName());
		
		double requiredCapacity = (contentItem.getAmount() / contentItem.getIngredient().getAmounts()) * contentItem.getIngredient().getCapacity();
		capacity += requiredCapacity;
		
		remove(contentItem);

		validate();
		repaint();
	}
	
	public ToolContentDialog(Tool tool)
	{
		this.tool = tool;
		
		capacity = tool.getCapacity();
		contentItems = new HashMap<String, ContentItem>();
		setSize(250, 200);
		setLayout(new FlowLayout());
		setTitle("Content of " + this.tool.getTitle());
	}
	
	public boolean addIngredient(Ingredient ingredient, int amount)
	{
		double requiredCapacity = (amount / ingredient.getAmounts()) * ingredient.getCapacity();

		if (capacity != -1 && requiredCapacity > capacity)
		{
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
				Percepts.NOCAPACITY, 
				new Identifier(tool.getName()),
				new Identifier(ingredient.getName())
			));
			
			return false;
		}
		else
		{
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
				Percepts.INGREDIENTADDED, 
				new Identifier(ingredient.getName()),
				new Numeral(amount),
				new Identifier(tool.getName())
			));
			
			if (contentItems.containsKey(ingredient.getName()))
				contentItems.get(ingredient.getName()).add(amount);
			else
			{
				ContentItem contentItem = new ContentItem(ingredient, amount, this);
				add(contentItem);
				contentItems.put(ingredient.getName(), contentItem);
			}
			
			if (capacity != -1)
				capacity -= requiredCapacity;
			
			validate();
		}
		
		return true;
	}
}

@SuppressWarnings("serial")
class ContentItem extends JLabel
implements MouseListener, ActionListener
{
	private ToolContentDialog toolContentDialog;
	private Ingredient ingredient;
	private int amount;
	private JPopupMenu jPopupMenu;
	
	public ContentItem(Ingredient ingredient, int amount, ToolContentDialog toolContentDialog)
	{
		this.toolContentDialog = toolContentDialog;
		this.ingredient = ingredient;
		this.amount = amount;
		
		jPopupMenu = new JPopupMenu();
		JMenuItem jMenuItemRemove = new JMenuItem("Remove");
		jMenuItemRemove.addActionListener(this);
		jPopupMenu.add(jMenuItemRemove);
		
		setBorder(new LineBorder(Color.BLACK, 1));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		if (ingredient.getImageIcon() != null)
			setIcon(new ImageIcon(ingredient.getImageIcon().getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)));
		setPreferredSize(new Dimension(50, 50));
		
		addMouseListener(this);
	}
	
	public Ingredient getIngredient()
	{
		return ingredient;
	}
	
	public void add(int amount)
	{
		this.amount += amount;
	}
	
	public int getAmount()
	{
		return amount;
	}

	@Override
	public void mouseEntered(MouseEvent me) 
	{
		ToolTipPanel.getInstance().print(amount + " " + ingredient.getUnit() + " " + ingredient.getName());
	}
	
	@Override
	public void mousePressed(MouseEvent me) 
	{ 
		Point position = MainFrame.getInstance().getMousePosition();
		jPopupMenu.show(MainFrame.getInstance().jFrame, (int)position.getX(), (int)position.getY());
	}
	
	@Override
	public void mouseExited(MouseEvent me) 
	{  
		ToolTipPanel.getInstance().clear();
	}
	
	@Override
	public void mouseClicked(MouseEvent me) {  }

	@Override
	public void mouseReleased(MouseEvent me) {  }

	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		toolContentDialog.removeIngredient(this);
	}
}
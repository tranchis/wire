package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import data.Ingredient;

@SuppressWarnings("serial")
public class DraggedIngredient extends JPanel
implements ActionListener, MouseListener, MouseWheelListener
{
	private int amount;
	private Timer timer;
	private Ingredient ingredient;
	private ContainerToolPanel containerToolPanel;
	
	protected static DraggedIngredient instance;
	
	protected DraggedIngredient()
	{
		timer = new Timer(1, this);
		
		addMouseWheelListener(this);
		
		setSize(30, 30);
		setBorder(new LineBorder(Color.BLACK, 1));
		setVisible(false);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setLayout(null);

		addMouseListener(this);
	}
	
	public static DraggedIngredient getInstance()
	{
		if (instance == null)
			instance = new DraggedIngredient();
		
		return instance;
	}
	
	public void setIngredient(Ingredient ingredient)
	{
		this.ingredient = ingredient;
		amount = ingredient.getAmounts();
	}
	
	public Ingredient getIngredient()
	{
		return ingredient;
	}
	
	public int getAmount()
	{
		return amount;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			timer.start();
			Point p = MainFrame.getInstance().getMousePosition();
			setLocation(p.x - 15, p.y - 15);
			
			if (ingredient.getImageIcon() != null)
			{
				JLabel jLabelIcon = new JLabel();
				jLabelIcon.setIcon(new ImageIcon(ingredient.getImageIcon().getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH)));
				jLabelIcon.setBounds(2, 2, 26, 26);
				add(jLabelIcon);
			}			
		}
		else
		{
			timer.stop();
		
			ingredient = null;
			removeAll();
		}

		super.setVisible(visible);		
	}

	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		ToolTipPanel.getInstance().print(amount + " " + ingredient.getUnit() + " " + ingredient.getTitle() + " selected");
		
		Point p = MainFrame.getInstance().getMousePosition();
		setLocation(p.x - 15, p.y - 15);
		
		containerToolPanel = ContainerToolPanel.checkHover((int)p.getX(), (int)p.getY());
	}

	@Override
	public void mouseClicked(MouseEvent me) {  }

	@Override
	public void mouseEntered(MouseEvent me) {  }

	@Override
	public void mouseExited(MouseEvent me) {  }

	@Override
	public void mouseReleased(MouseEvent me) {  }
	
	@Override
	public void mousePressed(MouseEvent me) 
	{ 
		if (containerToolPanel != null)
		{
			if (containerToolPanel.addIngredient(ingredient, amount))
			{
				containerToolPanel.deSelect();
				containerToolPanel = null;
				setVisible(false);
			}
		}
		else
		{
			ToolTipPanel.getInstance().clear();
			setVisible(false);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) 
	{
		if (mwe.getWheelRotation() == -1)
			amount += ingredient.getAmounts();
		else if (amount > ingredient.getAmounts())
			amount -= ingredient.getAmounts();
	}
}

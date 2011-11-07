package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import data.Item;

@SuppressWarnings("serial")
public abstract class ItemPanel extends JPanel
implements MouseListener
{
	protected Item item;
	protected boolean selected;
	
	public ItemPanel(Item item)
	{
		this.item = item;
		selected = false;
		
		setSize(55, 55);
		setLayout(null);
		setBackground(Color.WHITE);
		setBorder(new LineBorder(Color.BLACK, 1));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		if (item.getImageIcon() != null)
		{
			JLabel labelIcon = new JLabel();
			labelIcon.setBounds(1, 1, 55 - 2, 55 - 2);
			labelIcon.setIcon(item.getImageIcon());
			add(labelIcon);
		}
		
		addMouseListener(this);
	}
	
	public Item getItem()
	{
		return item;
	}
	
	public void select()
	{
		selected = true;
		this.setBorder(new LineBorder(Color.GREEN, 3));
		
		repaint();
	}
	
	public void deSelect()
	{
		selected = false;
		this.setBorder(new LineBorder(Color.BLACK, 1));
		
		repaint();
	}
	
	public boolean isSelected()
	{
		return selected;
	}	
	
	@Override
	public void mouseEntered(MouseEvent me) 
	{
		ToolTipPanel.getInstance().print(item.getTitle());
	}

	@Override
	public void mouseExited(MouseEvent me) 
	{
		ToolTipPanel.getInstance().clear();
	}

	@Override
	public void mousePressed(MouseEvent me) {  }

	@Override
	public void mouseReleased(MouseEvent me) {  }
	
	@Override
	public void mouseClicked(MouseEvent me) {  }
}

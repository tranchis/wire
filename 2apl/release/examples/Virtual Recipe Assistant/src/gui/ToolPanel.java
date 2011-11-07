package gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import virtualRecipeAssistantEnvironment.VirtualRecipeAssistantEnvironment;

import data.Action;
import data.Item;
import data.Percepts;
import data.Tool;
import eis.iilang.Identifier;
import eis.iilang.Percept;

@SuppressWarnings("serial")
public class ToolPanel extends ItemPanel
implements ActionListener
{
	private JPopupMenu jPopupMenu;
	
	public ToolPanel(Tool tool)
	{
		super(tool);
		
		jPopupMenu = new JPopupMenu();
		
		for (Action action : tool.getActions())
		{
			JMenuItem jMenuItem = new JMenuItem(action.getTitle());
			jMenuItem.addActionListener(this);
			jPopupMenu.add(jMenuItem);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent me) 
	{ 	
		Point position = MainFrame.getInstance().getMousePosition();
		jPopupMenu.show(MainFrame.getInstance().jFrame, (int)position.getX(), (int)position.getY());
	}

	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		if (ae.getActionCommand().equals("use"))
		{
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
				Percepts.TOOLSELECTED, 
				new Identifier(this.item.getName())
			));
			
			//ContainerToolPanel containerToolPanel = new ContainerToolPanel(item);
			VisualPanel.getInstance().addContainerToolPanel(item);
			
			for (Component component : jPopupMenu.getComponents())
			{
				JMenuItem jMenuItem = (JMenuItem)component;
				if (jMenuItem.getActionCommand().equals("use"))
				{
					jPopupMenu.remove(jMenuItem);
					break;
				}
			}
			
			JMenuItem jMenuItem = new JMenuItem("set back in the cabinet");
			jMenuItem.addActionListener(this);
			jPopupMenu.add(jMenuItem);
			
			select();
		}
		else if (ae.getActionCommand().equals("set back in the cabinet"))
		{
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
				Percepts.TOOLDESELECTED, 
				new Identifier(this.item.getName())
			));		
			
			for (Component component : jPopupMenu.getComponents())
			{
				JMenuItem jMenuItem = (JMenuItem)component;
				if (jMenuItem.getActionCommand().equals("set back in the cabinet"))
				{
					jPopupMenu.remove(jMenuItem);
					VisualPanel.getInstance().removeContainerToolPanel(item);
					break;
				}
			}
			
			JMenuItem jMenuItem = new JMenuItem("use");
			jMenuItem.addActionListener(this);
			jPopupMenu.add(jMenuItem);
			
			deSelect();
		}
		else
		{
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
				Percepts.ACTIONPERFORMED, 
				new Identifier( (((Tool)item).getActionByTitle(ae.getActionCommand())).getName()),
				new Identifier(item.getName())
			));
		}
	}	
}

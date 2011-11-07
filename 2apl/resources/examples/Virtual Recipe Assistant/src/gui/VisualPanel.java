package gui;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JPanel;

import data.Item;

@SuppressWarnings("serial")
public class VisualPanel extends JPanel
{
	private HashMap<String, ContainerToolPanel> containerToolPanels;
	
	protected static VisualPanel instance;
	
	private void init()
	{
		containerToolPanels = new HashMap<String, ContainerToolPanel>();
	}
	
	protected VisualPanel()
	{
		setBounds(0, 0, 300, 275);
		setBackground(Color.BLACK);
		setLayout(null);
		
		init();
	}
	
	public static VisualPanel getInstance()
	{
		if (instance == null)
			instance = new VisualPanel();
		
		return instance;
	}
	
	public void reset()
	{
		removeAll();
		
		for (ContainerToolPanel containerToolPanel : containerToolPanels.values())
			containerToolPanel.destroy();
		
		init();
		
		repaint();
	}
	
	public void addContainerToolPanel(Item item)
	{
		if (!containerToolPanels.containsKey(item.getName()))
		{
			ContainerToolPanel containerToolPanel = new ContainerToolPanel(item);
			
			int nr = containerToolPanels.size();
			int x = 16 + ((55 + 16) * (nr % 4));
			int y = 13 + (70 * (nr / 4));
			containerToolPanel.setLocation(x, y);
			
			containerToolPanels.put((containerToolPanel.getItem().getName()), containerToolPanel);

			add(containerToolPanel);

			repaint();
		}
	}
	
	public void removeContainerToolPanel(Item item)
	{
		containerToolPanels.get(item.getName()).destroy();
		remove(containerToolPanels.get(item.getName()));
		containerToolPanels.remove(item.getName());
		
		repaint();
	}
}

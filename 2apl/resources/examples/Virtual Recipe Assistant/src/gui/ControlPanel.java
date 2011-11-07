package gui;


import io.FileReader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Ingredient;
import data.Tool;


@SuppressWarnings("serial")
public class ControlPanel extends JPanel
implements ActionListener
{
	private int nrOfToolPanels;
	private int nrOfIngredientPanels;
	
	private JComboBox jComboBox;
	private JScrollPane jScrollPaneContainer;
	private JPanel jPanelTools;
	private JPanel jPanelIngredients;
	
	protected static ControlPanel instance;

	private void init()
	{
		nrOfToolPanels = 0;
		nrOfIngredientPanels = 0;
		
		setBounds(300, 0, 200, 300);
		setBackground(Color.WHITE);
		setLayout(null);
		
		jScrollPaneContainer = new JScrollPane();
		jScrollPaneContainer.setBounds(5, 35, 190, 265);
		add(jScrollPaneContainer);
		
		jPanelTools = new JPanel();
		jPanelTools.setLayout(null);
		jPanelTools.setBackground(Color.WHITE);
		jPanelTools.setPreferredSize(new Dimension(172, 4 * 75));
		buildCabinet(0, jPanelTools);
		
		jPanelIngredients = new JPanel();
		jPanelIngredients.setLayout(null);
		jPanelIngredients.setBackground(Color.WHITE);
		jPanelIngredients.setPreferredSize(new Dimension(172, 4 * 75));
		buildCabinet(0, jPanelIngredients);
		
		String[] selections = { "Tools", "Ingredients" };
		jComboBox = new JComboBox(selections);
		jComboBox.setBounds(5, 5, 190, 25);
		jComboBox.addActionListener(this);
		add(jComboBox);
		
		for (Ingredient ingredient : Ingredient.getIngredients())
			addIngredient(ingredient);
		
		for (Tool tool : Tool.getTools())
			addTool(tool);
		
		jComboBox.setEnabled(false);
		jScrollPaneContainer.getVerticalScrollBar().setEnabled(false);
	}
	
	private void buildCabinet(int oldHeight, JPanel jPanel)
	{
		JLabel labelShelf;
		ImageIcon imageIcon = null;
		try 
		{
			imageIcon = new ImageIcon(FileReader.getInstance().getImages() + "/" + "cabinet_shelf.jpg");
		} 
		catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
		
		if (imageIcon != null)
		{
			for (int i = oldHeight; i < jPanel.getPreferredSize().height + 75; i += 75)
			{
				labelShelf = new JLabel();
				labelShelf.setIcon(imageIcon);
				labelShelf.setBounds(0, i, 172, 75);
				jPanel.add(labelShelf);
				jPanel.setComponentZOrder(labelShelf, jPanel.getComponentCount() - 1);
			}
		}
	}
	
	protected ControlPanel()
	{
		init();
	}
	
	public static ControlPanel getInstance()
	{
		if (instance == null)
			instance = new ControlPanel();
		
		return instance;
	}
	
	public void reset()
	{
		removeAll();
		
		init();
		
		repaint();
	}
	
	public void enable()
	{
		jComboBox.setEnabled(true);
		jScrollPaneContainer.getVerticalScrollBar().setEnabled(true);
		jComboBox.setSelectedIndex(0);
	}
	
	public void addIngredient(Ingredient ingredient)
	{
		ItemPanel ingredientPanel = new IngredientPanel(ingredient);
		int oldHeight = jPanelIngredients.getPreferredSize().height;
		jPanelIngredients.setPreferredSize(new Dimension(172, ((nrOfIngredientPanels + 2) / 2) * 75));
		
		int x = (nrOfIngredientPanels % 2 == 0) ? 20 : 97;
		int y = ((nrOfIngredientPanels / 2) * 75) + 10;
		ingredientPanel.setLocation(x, y);

		jPanelIngredients.add(ingredientPanel);
		jPanelIngredients.setComponentZOrder(ingredientPanel, 0);

		nrOfIngredientPanels ++;
		
		buildCabinet(oldHeight, jPanelIngredients);
	}
	
	public void addTool(Tool tool)
	{
		ItemPanel toolPanel = new ToolPanel(tool);
		int oldHeight = jPanelTools.getPreferredSize().height;
		jPanelTools.setPreferredSize(new Dimension(172, ((nrOfToolPanels + 2) / 2) * 75));
		
		int x = (nrOfToolPanels % 2 == 0) ? 20 : 97;
		int y = ((nrOfToolPanels / 2) * 75) + 10;
		toolPanel.setLocation(x, y);
		jPanelTools.add(toolPanel);
		jPanelTools.setComponentZOrder(toolPanel, 0);

		nrOfToolPanels ++;
		
		buildCabinet(oldHeight, jPanelTools);
	}

	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		if (ae.getSource() == jComboBox)
		{
			if (jComboBox.getSelectedItem().equals("Tools"))
			{
				jScrollPaneContainer.getViewport().removeAll();
				jScrollPaneContainer.getViewport().add(jPanelTools);
			}
			else if (jComboBox.getSelectedItem().equals("Ingredients"))
			{
				jScrollPaneContainer.getViewport().removeAll();
				jScrollPaneContainer.getViewport().add(jPanelIngredients);
			}
		}
	}
}

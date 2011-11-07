package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.io.FileNotFoundException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import virtualRecipeAssistantEnvironment.VirtualRecipeAssistantEnvironment;

import data.Constants;
import data.Percepts;
import data.Recipe;
import eis.iilang.Identifier;
import eis.iilang.Percept;

public class MainFrame
{
	protected JFrame jFrame;
	
	private VisualPanel visualPanel;
	private ToolTipPanel toolTipPanel;
	private ControlPanel controlPanel;
	private CommunicationPanel communicationPanel;
	private DraggedIngredient draggedIngredient;
	
	protected static MainFrame instance;
	
	private void init()
	{
		draggedIngredient = DraggedIngredient.getInstance();
		jFrame.getContentPane().add(draggedIngredient);
		
		visualPanel = VisualPanel.getInstance();
		jFrame.getContentPane().add(visualPanel);
		
		toolTipPanel = ToolTipPanel.getInstance();
		jFrame.getContentPane().add(toolTipPanel);
		
		controlPanel = ControlPanel.getInstance();
		jFrame.getContentPane().add(controlPanel);
		
		try 
		{//TODO EIS EXCEPTIONS STRAKS
			communicationPanel = CommunicationPanel.getInstance();
			jFrame.getContentPane().add(communicationPanel);
		} 
		catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
	}
	
	protected MainFrame()
	{
		jFrame = new JFrame();
		jFrame.setLayout(null);
		jFrame.setTitle(Constants.TITLE + " v" + Constants.VERSION);
		jFrame.setSize(500 + 8, 400 + 34); // The 8 and 34 pixels are the correction for the borders and the titlebar of the JFrame
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setResizable(false);
		jFrame.setLocationRelativeTo(null);
		jFrame.setGlassPane(new PausePane());
	}
	
	public static MainFrame getInstance()
	{
		if (instance == null)
			instance = new MainFrame();
		
		return instance;
	}
	
	public void reset() 
	throws FileNotFoundException
	{
		CommunicationPanel.getInstance().reset();
		ControlPanel.getInstance().reset();
		VisualPanel.getInstance().reset();
		
		jFrame.getContentPane().removeAll();
		
		init();
		
		jFrame.repaint();
	}
	
	public void pause()
	{
		jFrame.getGlassPane().setVisible(true);
	}
	
	public void start() 
	throws FileNotFoundException
	{
		if (jFrame.getGlassPane().isVisible() && jFrame.isVisible())
			jFrame.getGlassPane().setVisible(false);
		else
		{
			reset();
		
			jFrame.getGlassPane().setVisible(false);
			jFrame.setVisible(true);
		}
	}
	
	public void stop()
	{
		jFrame.setVisible(false);
	}
	
	public Point getMousePosition()
	{
		return new Point(
			MouseInfo.getPointerInfo().getLocation().x - jFrame.getLocation().x - 3,
			MouseInfo.getPointerInfo().getLocation().y - jFrame.getLocation().y - 30);	
	}
	
	public void repaint()
	{
		jFrame.repaint();
	}
}

@SuppressWarnings("serial")
class PausePane extends JComponent
{
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString("PAUSED", 10, 30);
	}
}

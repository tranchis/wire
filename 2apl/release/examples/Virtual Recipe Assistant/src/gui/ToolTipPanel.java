package gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ToolTipPanel extends JPanel
{
	private static JLabel jLabel;

	protected static ToolTipPanel instance;
	
	public ToolTipPanel()
	{
		setBounds(0, 275, 300, 25);
		setBackground(Color.BLACK);
		setLayout(null);
		
		jLabel = new JLabel();
		jLabel.setBounds(0, 0, 300, 25);
		jLabel.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel.setForeground(Color.WHITE);
		add(jLabel);
	}
	
	public static ToolTipPanel getInstance()
	{
		if (instance == null)
			instance = new ToolTipPanel();
		
		return instance;
	}
	
	public void print(String text)
	{
		jLabel.setText(text);
	}
	
	public void clear()
	{
		jLabel.setText("");
	}
}

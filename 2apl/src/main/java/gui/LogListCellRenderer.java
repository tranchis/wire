package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

/**
 * Custom cell renderer that formats the color of the log entries according to
 * their severity.
 * 
 * @see gui.LogWindow
 */
public class LogListCellRenderer extends JTextArea implements ListCellRenderer
{
	final static Color stepNameColor = new Color(220, 220, 220);
	final static Color defaultColor = new Color(255, 255, 255);
	final static Color significantColor = new Color(230, 255, 230);
	final static Color errorColor = new Color(255, 230, 230);

	private boolean rtf = false;

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		setOpaque(true);
		String cellText = (String) value;

		if (cellText.startsWith("[STEP"))
		{
			setBackground(stepNameColor);
		} else if (cellText.startsWith("[Significant]"))
		{
			setBackground(significantColor);
			cellText = cellText.substring(13);
			cellText += "\n";
		} else if (cellText.startsWith("[Error]"))
		{
			setBackground(errorColor);
			cellText = cellText.substring(7);
			cellText += "\n";
		} else
		{
			setBackground(defaultColor);
			cellText += "\n";
		}

		setMargin(new Insets(2, 5, 2, 5));
		// setBorder(new LineBorder(Color.GRAY, 1));

		setText(cellText);
		return this;
	}
}

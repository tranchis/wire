package gui;

import apapl.program.Rule;
import apapl.program.BeliefUpdate;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JEditorPane;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Font;

public class BaseListCellRenderer extends JEditorPane implements ListCellRenderer
{
	Color odd, even;
	private boolean head = false;
	private boolean rtf = true;
	
	public BaseListCellRenderer(Color odd, Color even, boolean head, boolean rtf)
	{
		this.rtf = rtf;
		this.head = head;
		this.odd = odd;
		this.even = even;
	}
	
	public Component getListCellRendererComponent(
	JList list,
	Object value,
	int index,
	boolean isSelected,
	boolean cellHasFocus)
	{
		if (rtf) setContentType("text/rtf");
		
		if (rtf) setText(RTFFrame.basicRTF((String)value));
		else setText((String)value);
		
		if (head&&index==0) setFont(getFont().deriveFont(Font.BOLD));
		else setFont(getFont().deriveFont(Font.PLAIN));
		
		setBackground(index%2==1?odd:even);
		return this;
	}
}
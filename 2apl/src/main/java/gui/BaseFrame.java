package gui;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.TextArea;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JScrollPane;

public class BaseFrame extends JComponent
{
	private String name;
	private boolean selected = true;
	private JTextArea ta = new JTextArea();
	private JLabel caption;
	
	public BaseFrame(String name)
	{
		this.name = name;
		init();
	}
	
	private void init()
	{
		setLayout(new BorderLayout());
		ta.setTabSize(4); ta.setEditable(true);
				
		if (name!=null) {
			caption = new JLabel(name);
			add(caption,BorderLayout.NORTH);
		}
				
		add(new JScrollPane(ta),BorderLayout.CENTER);
	}
	
	public void update(String s)
	{
		try {
		ta.setText(s);
		}
		catch (Error e) {
		}
		
	}
	
	public void setColor(Color c)
	{
		caption.setForeground(c);
		ta.setForeground(c);
	}
	
	public void append(String s)
	{
		ta.append(s);
	}
	
	public void clear()
	{
		ta.setText("");
	}
}

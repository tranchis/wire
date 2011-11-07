package gui;

import apapl.program.Base;
import apapl.data.Term;
import apapl.plans.PlanSeq;
import apapl.plans.Plan;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.TextArea;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.text.PlainDocument;

public class RTFFrame extends JComponent
{
	private String name = null;
	private JEditorPane editor = new JEditorPane();
	private JScrollPane editorScrollPane;
	private JLabel caption;
	private boolean rtf;
	
	public RTFFrame(String name, boolean rtf)
	{
		this.rtf = rtf;
		this.name = name;
		init();
	}
	
	public RTFFrame(boolean rtf)
	{
		this.rtf = rtf;
		init();
	}
	
	public void setRTF(boolean rtf)
	{
		this.rtf = rtf;
		if (rtf) editor.setContentType("text/rtf");
		else editor.setContentType("text/plain");
	}
	
	private void init()
	{
		setRTF(rtf);
		setLayout(new BorderLayout());
		if (rtf) editor.setContentType("text/rtf");
		
		// Set tab size to 4
		editor.getDocument().putProperty( PlainDocument.tabSizeAttribute, new Integer(2) );

				
		if (name!=null) {
			caption = new JLabel(name);
			add(caption,BorderLayout.NORTH);
		}
		
		editorScrollPane = new JScrollPane(editor);
		add(editorScrollPane, BorderLayout.CENTER);
	}
	
	public void update(String s)
	{
		try {
			if (rtf) editor.setText(basicRTF(s));
			else editor.setText(s);
		}
		catch (Error e) {System.out.println("Error during RTF update:"); e.printStackTrace();}
	}
	
	public void setColor(Color c)
	{
		caption.setForeground(c);
		editor.setForeground(c);
	}
	
	public void setBackground(Color c)
	{
		if (caption!=null) caption.setBackground(c);
		editor.setBackground(c);
	}
	
	/*public void append(String s)
	{
		ta.append(s);
	}*/
	
	public void clear()
	{
		editor.setText("");
	}
	
	public static String basicRTF(String c)
	{
		/*
		1 = Operator (255,0,0)
		2 = Literal2  (101,0,153)
		3 = Method (bold)
		4 = Atom (255,153,0)
		5 = Literal1 (153,153,255)
		6 = Quote (153,153,255)
		*/

		return "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033\\deftab120{\\fonttbl{\\f0\\fswiss\\fcharset0 Arial;}}\n"
		+ "{\\colortbl ;"
		+ "\\red255\\green0\\blue0;"		// Operator
		+ "\\red101\\green0\\blue153;"		// Literal2
		+ "\\red255\\green255\\blue255;"	// Method
		+ "\\red101\\green0\\blue153;"		// Atom
		+ "\\red255\\green153\\blue0;"		// Literal1
		+ "\\red153\\green153\\blue255;"	// Quote
		+ "\\red0\\green0\\blue255;"		// Headers
		+ "\\red0\\green255\\blue0;"		// Log bullet
		+ "}\n"
		+ c+"\n}";
	}
}

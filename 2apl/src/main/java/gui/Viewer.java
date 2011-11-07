package gui;

import javax.swing.JTabbedPane;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import java.io.StringReader;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;

public class Viewer extends JTabbedPane
{
	//protected ArrayList<FileFrame> files = new ArrayList<FileFrame>();
	protected ArrayList<JComponent> tabs = new ArrayList<JComponent>();

	protected boolean showBases = true;
	protected boolean showFileTabs = false;
		
	public Viewer()
	{
	}
	
	public void select(JComponent c)
	{
		setSelectedComponent(c);
	}
}

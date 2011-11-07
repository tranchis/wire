package gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileListCellRenderer implements ListCellRenderer, ActionListener
{
	public FileListCellRenderer()
	{
	}
	

	public Component getListCellRendererComponent(
	JList list,
	Object value,
	int index,
	boolean isSelected,
	boolean cellHasFocus)
	{
		return (JCheckBox)value;
	}
	
	
	public void actionPerformed (ActionEvent e)
	{
	}
	
}
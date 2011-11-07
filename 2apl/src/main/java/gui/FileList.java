package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileList extends JPanel
{
	private Edit edit;
	private JPanel pane;
	private Viewer viewer;
	
	public FileList(Edit edit,File[] s, Viewer viewer)
	{
		this.edit = edit;
		this.viewer = viewer;
		setLayout(new BorderLayout());
		
		int n = s.length;
		pane = new JPanel();
		pane.setLayout(new GridLayout(n,1));

		for (int i=0; i<n; i++)	addFile(s[i]);
		add(pane,BorderLayout.NORTH);
	}
	
	private void addFile(File f)
	{
		JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
		JButton b = new JButton("Edit");
		b.addActionListener(new FileEditListener(f));
		p1.add(b,BorderLayout.WEST);
		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());
		p2.add(new JLabel("  "+f.getName()),BorderLayout.WEST);
		JLabel l = new JLabel("  "+f);
		l.setForeground(Color.GRAY);
		p2.add(l,BorderLayout.CENTER);
		p1.add(p2,BorderLayout.CENTER);
		pane.add(p1);
	}
	
	class FileEditListener implements ActionListener
	{
		File f;
		public FileEditListener(File g)
		{
			f = g;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if (edit!=null) edit.editFile(f,viewer);
		}
	}
		
		
	
	
}
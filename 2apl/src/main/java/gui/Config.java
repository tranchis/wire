package gui;

import apapl.messaging.*;

import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.InetAddress;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;


public class Config extends JFrame implements ActionListener, WindowListener
{
	private JRadioButton[] modeButton;
	private JTextField portField, hostField;
	private JButton okButton, cancelButton;
	private File masfile = null;
	
	public Config(File masfile)
	{
		this.masfile = masfile;
		
	    modeButton = new JRadioButton[3];
		ButtonGroup modes = new ButtonGroup();
		
		modeButton[0] = new JRadioButton("No Jade");
		modeButton[0].setActionCommand("nojade");
		modeButton[1] = new JRadioButton("Jade Main-Container");
		modeButton[1].setActionCommand("server");
		modeButton[2] = new JRadioButton("Jade Agent-Container");
		modeButton[2].setActionCommand("client");
		
		portField = new JTextField("1099");
		hostField = new JTextField(12);

		hostField.setEditable(false);
		portField.setEditable(false);
		
		okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("cancel");
		cancelButton.addActionListener(this);
		
		JPanel top = new JPanel();
		top.setLayout(new GridLayout(3,1,0,1));
		for (int i=0; i < modeButton.length; i++) {
			modeButton[i].addActionListener(this);
			top.add(modeButton[i]);
			modes.add(modeButton[i]);
		}
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new GridLayout(3,2,0,1));
		bottom.add(new JLabel("Host"));
		bottom.add(hostField);
		bottom.add(new JLabel("Port"));
		bottom.add(portField);
		
		bottom.add(okButton);
		bottom.add(cancelButton);
		
		setLayout(new BorderLayout());
		add(top,BorderLayout.NORTH);
		add(bottom,BorderLayout.CENTER);
		
		hostField.setEditable(false);
		modeButton[0].setSelected(true);
		setTitle("2APL configuration");
		setResizable(false);
		setSize(300,120);
		addWindowListener(this);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		pack();
		okButton.grabFocus();
  	
		doLayout();
		setVisible(true);
	
    try
    {	hostField.setText(InetAddress.getLocalHost().getHostAddress());
  	}
		catch(java.net.UnknownHostException e) {}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ("cancel".equals(e.getActionCommand()))
		{	System.exit(0);
		}
		else if ("ok".equals(e.getActionCommand()))
		{	int port = 1099;
			try {port = Integer.parseInt(portField.getText());} catch (Exception ex) {}
			String host = hostField.getText();
			if (modeButton[1].isSelected()) new GUI(new JadeMessenger(null,port), masfile);
			else if (modeButton[2].isSelected()) new GUI(new JadeMessenger(host,port), masfile);
			else if (modeButton[0].isSelected()) new GUI(new LocalMessenger(), masfile);
			setVisible(false);
		}
		else if ("server".equals(e.getActionCommand()))
		{	hostField.setEditable(false);
			portField.setEditable(true);
		}
		else if ("client".equals(e.getActionCommand()))
		{	hostField.setEditable(true);
			portField.setEditable(true);
		}
		else if ("nojade".equals(e.getActionCommand()))
		{	hostField.setEditable(false);
			portField.setEditable(false);
		}
	}
	
	public void windowClosing(WindowEvent e) {System.exit(0);}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}

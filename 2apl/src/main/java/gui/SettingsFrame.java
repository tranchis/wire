package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Toolkit;


public class SettingsFrame extends JFrame implements ActionListener, WindowListener
{
	//public JTextField logLevelField	= new JTextField(""+Logger.getLogLevel());
	//public JTextField logSizeField	= new JTextField(""+Logger.getLogSize());
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private JCheckBox useSHBox;
	private JCheckBox m_chkDebugMode;
	private GUI gui;
		
	public SettingsFrame(GUI gui)
	{
		this.gui=gui;
		
		useSHBox = new JCheckBox("Use syntax highlighting in overview.",gui.useSH());
		//m_chkDebugMode = new JCheckBox("Enable debug mode.", gui.getDebugModeSetting());
		
		okButton.setActionCommand("ok");
		cancelButton.setActionCommand("cancel");
		
		setLayout(new GridLayout(5,1,0,1));
		JPanel row1 = new JPanel();
		row1.setLayout(new GridLayout(1,2,0,1));
		row1.add(new JLabel("Log Level"));
		//row1.add(logLevelField);
		//JPanel row2 = new JPanel();
		//row2.setLayout(new GridLayout(1,2,0,1));
		//row2.add(new JLabel("Log Size"));
		//row2.add(logSizeField);
		JPanel row3 = new JPanel();
		row3.setLayout(new GridLayout(1,2,0,1));
		row3.add(okButton);
		row3.add(cancelButton);
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		add(row1);
		//add(row2);
		add(useSHBox);
		add(m_chkDebugMode);
		add(row3);
		
		setTitle("Settings");
		
		setResizable(false);
		setSize(300,160);
		addWindowListener(this);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		
		pack();
	    
		doLayout();
		setVisible(true);
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if ("cancel".equals(e.getActionCommand()))
    		setVisible(false);
    	else if ("ok".equals(e.getActionCommand())) {
			//try {Logger.setLogLevel(Integer.parseInt(logLevelField.getText()));} catch (Exception ex) {}
			//try {Logger.setLogSize (Integer.parseInt(logSizeField.getText()));} catch (Exception ex) {}
			gui.useSH(useSHBox.isSelected());

			setVisible(false);
		}
	}
	
	public void windowClosing(WindowEvent e) {setVisible(false);}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
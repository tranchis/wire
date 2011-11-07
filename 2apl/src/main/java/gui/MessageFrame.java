package gui;

import apapl.messaging.*;
import apapl.data.APLFunction;
import apapl.Parser;
import apapl.parser.ParseException;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MessageFrame extends JFrame implements ActionListener, WindowListener
{
	private MessageAgent ma = null;
	private Messenger msgr;
	
	private JTextField receiver, content, performative, language, ontology;
	
	public MessageFrame(Messenger msgr)
	{
		this.msgr = msgr;
		msgr.addModule("MessageAgent");
		setBounds(400,400,400,200);
		setResizable(false);
		
		receiver = new JTextField("");
		content = new JTextField("");
		performative = new JTextField("inform");
		language = new JTextField("prolog");
		ontology = new JTextField("aplFunction");
		
		JPanel right = new JPanel();
		JPanel left = new JPanel();
		right.setLayout(new GridLayout(5,1,2,2));
		left.setLayout(new GridLayout(5,1,2,2));
				
		left.add(new JLabel("Receiver"));
		left.add(new JLabel("Content"));
		left.add(new JLabel("Performative   "));
		left.add(new JLabel("Language"));
		left.add(new JLabel("Ontology"));

		right.add(receiver);
		right.add(content);
		right.add(performative);
		right.add(language);
		right.add(ontology);
		
		setLayout(new BorderLayout());
		add(right,BorderLayout.CENTER);
		add(left,BorderLayout.WEST);
		
		addWindowListener(this);
		JButton button = new JButton("Send Message");
		button.addActionListener(this);
		
		add(button,BorderLayout.SOUTH);
				
		setTitle("messageAgent");
	}
	
	public void actionPerformed(ActionEvent e)
	{
		try {
			APLMessage message = new APLMessage();
			message.setSender("messageAgent");
			message.setReceiver(receiver.getText());
			message.setContent(parseContent(content.getText()));
			message.setPerformative(performative.getText());
			message.setLanguage(language.getText());
			message.setOntology(ontology.getText());
			msgr.sendMessage(message);
		}
		catch (ParseException ex)
		{
			JOptionPane.showMessageDialog(this,
		    "\""+content.getText()+"\" is not a valid APLFunction.",
		    "Parse Exception",
		    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private APLFunction parseContent(String c) throws ParseException
	{
		return (new Parser()).parseAPLFunction(c);
	}
	
	public void windowClosing(WindowEvent e){
		this.hide();
	}
	
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	
}
		
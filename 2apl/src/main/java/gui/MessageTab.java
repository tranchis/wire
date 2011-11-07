package gui;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import apapl.messaging.*;
import java.util.ArrayList;

public class MessageTab extends JTable implements MessageListener
{
	private MessagesModel model;
	private boolean doUpdate = false;
	
	public MessageTab()
	{
		model = new MessagesModel();
		//setFillsViewportHeight(true);
		ListSelectionModel sm = getSelectionModel();
		update();
	}
		
	public void messageSent(APLMessage message)
	{
		model.addMessage(message);
		if (isShowing()) update();
	}
	
	private void update()
	{
		setModel(model);
		//TODO: hoe netjes updaten
		//When invokeLater is called contiunously without a sleep, it causes the ui to crash.
		try {Thread.sleep(1);}
		catch (Exception e) {System.out.println("oops");}
		
		//Directly updating the ui causes exceptions (docuemted by swing).
		//Swing suggests invokeLater for updating the ui.
		Runnable update = new Runnable() {public void run() {updateUI();}};
		SwingUtilities.invokeLater(update);
	}	
}
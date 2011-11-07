package gui;

import apapl.messaging.*;
import javax.swing.table.*;
import java.util.ArrayList;

public class MessagesModel extends AbstractTableModel
{
	
	private final int size = 80;
	private int t = 0;
	private APLMessage[] messages;
	private String[] columnNames = {"Nr.","Sender","Receiver","Performative","Language","Ontology","Content"};
	
	public MessagesModel()
	{
		messages = new APLMessage[size];
	}
	
	public void addMessage(APLMessage message)
	{
		messages[t%size] = message;
		t++;
	}
	
	public int getRowCount()
	{
		return Math.min(t,messages.length);
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		int deletedRows = Math.max(0,t-size);
		APLMessage message = messages[(rowIndex+deletedRows)%size];
		if (columnIndex==1) return message.getSender();
		if (columnIndex==2) return message.getReceiver();
		if (columnIndex==3) return message.getPerformative();
		if (columnIndex==4) return message.getLanguage();
		if (columnIndex==5) return message.getOntology();
		if (columnIndex==6) return message.getContent();
		if (columnIndex==0) return ""+(rowIndex+deletedRows);
		return null;
	}
	
	public String getColumnName(int columnIndex)
	{
		if (columnIndex<columnNames.length) return columnNames[columnIndex];
		else return "";
	}
	
	public int getColumnCount()
	{
		return columnNames.length;
	}
	
	
}
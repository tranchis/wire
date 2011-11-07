package gui;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import apapl.messaging.*;
import java.util.ArrayList;

public class MasTab extends Viewer 
{
	private MessageTab messageTable;

	public MasTab( MessageTab messageTable )
	{
		this.messageTable = messageTable;
		addTab("Messages",new JScrollPane(messageTable));
	}
}

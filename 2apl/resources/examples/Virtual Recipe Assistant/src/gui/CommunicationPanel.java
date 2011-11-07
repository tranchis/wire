package gui;

import io.FileReader;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import virtualRecipeAssistantEnvironment.VirtualRecipeAssistantEnvironment;
import data.Percepts;
import data.Recipe;
import eis.iilang.Identifier;
import eis.iilang.Percept;

@SuppressWarnings("serial")
public class CommunicationPanel extends JPanel
implements ActionListener
{
	private JButton buttonSpeak;
	private JLabel labelSelectRecipe;
	private JLabel labelSelectAssistant;
	private JComboBox comboRecipies;
	private JComboBox comboAssistants;
	private JComboBox comboSpeak;
	private JButton buttonOK;
	
	protected static CommunicationPanel instance;	
	
	private void init() 
	throws FileNotFoundException
	{
		labelSelectRecipe = new JLabel("Select a recipe:");
		labelSelectRecipe.setBounds(20, 10, 150, 25);
		add(labelSelectRecipe);
		
		labelSelectAssistant = new JLabel("Select an assistant:");
		labelSelectAssistant.setBounds(20, 40, 150, 25);
		add(labelSelectAssistant);
		
		Object[] arr = Recipe.getRecipies().toArray();
		String[] recipies = new String[arr.length];
		for (int i = 0; i < arr.length; i ++)
			recipies[i] = ((Recipe)arr[i]).getTitle();

		comboRecipies = new JComboBox(recipies);
		comboRecipies.setBounds(180, 10, 150, 25);
		comboRecipies.setSelectedIndex(0);
		add(comboRecipies);
		
		String[] assistants = { "merlin", "genie", "peedy", "robby" };
		comboAssistants = new JComboBox(assistants);
		comboAssistants.setBounds(180, 40, 150, 25);
		add(comboAssistants);
		
		buttonOK = new JButton("OK");
		buttonOK.setBounds(340, 40, 100, 25);
		buttonOK.addActionListener(this);
		add(buttonOK);

//		buttonAgree = new JButton("Yes");
//		buttonAgree.setBounds(10, 10, 75, 30);
//		buttonAgree.addActionListener(this);
//		add(buttonAgree);
//
//		buttonDisagree = new JButton("No");
//		buttonDisagree.setBounds(95, 10, 75, 30);
//		buttonDisagree.addActionListener(this);
//		add(buttonDisagree);
//		
//		buttonHelp = new JButton("Help");
//		buttonHelp.setBounds(180, 10, 75, 30);
//		buttonHelp.addActionListener(this);
//		add(buttonHelp);
		
		JLabel labelIcon = new JLabel();
		labelIcon.setBounds(0, 0, 500, 100);
		labelIcon.setIcon(new ImageIcon(FileReader.getInstance().getImages() + "/" + "communication_panel.jpg"));
		add(labelIcon);
		
		validate();
	}
	
	public CommunicationPanel()
	throws FileNotFoundException
	{
		setBounds(0, 300, 500, 100);
		setBackground(Color.BLUE);
		setLayout(null);
		
		init();
	}
	
	public static CommunicationPanel getInstance() 
	throws FileNotFoundException
	{
		if (instance == null)
			instance = new CommunicationPanel();
		
		return instance;
	}

	public void reset() 
	throws FileNotFoundException
	{
		removeAll();
		
		init();

		repaint();
	}
	
	public void resetSpeakCombo()
	{
		comboSpeak.removeAllItems();
		comboSpeak.insertItemAt("Hello", 0);
		comboSpeak.setSelectedIndex(0);
	}
	
	public void addSpeak(String text)
	{
		removeSpeak(text);

		comboSpeak.insertItemAt(text, 0);
		comboSpeak.setSelectedIndex(0);
	}
	
	public void removeSpeak(String text)
	{
		for (int i = 0; i < comboSpeak.getItemCount(); i ++)
			if (comboSpeak.getItemAt(i).equals(text))
				comboSpeak.removeItemAt(i);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		if (ae.getSource() == buttonOK)
		{
			remove(labelSelectRecipe);
			remove(labelSelectAssistant);
			remove(comboRecipies);
			remove(comboAssistants);
			remove(buttonOK);
			
			ControlPanel.getInstance().enable();
			
			repaint();
			
			Recipe selectedRecipe = Recipe.getRecipeByTitle((String)comboRecipies.getSelectedItem());
			
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
					Percepts.RECIPYSELECTED, 
					new Identifier(selectedRecipe.getName()),
					new Identifier(selectedRecipe.getTitle())
				));
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
					Percepts.ASSISTANTSELECTED, 
					new Identifier((String)comboAssistants.getSelectedItem())
				));
			
			comboSpeak = new JComboBox();
			comboSpeak.setBounds(20, 10, 300, 25);
			resetSpeakCombo();
			add(comboSpeak, 0);
			
			buttonSpeak = new JButton("Say");
			buttonSpeak.setBounds(330, 10, 75, 25);
			buttonSpeak.addActionListener(this);
			add(buttonSpeak, 0);
			
			validate();
		}
		else if (ae.getSource() == buttonSpeak)
		{
			VirtualRecipeAssistantEnvironment.sendPercept(new Percept(
				Percepts.SPEAK, 
				new Identifier(((String)comboSpeak.getSelectedItem()).toLowerCase())
			));
		}
	}
}

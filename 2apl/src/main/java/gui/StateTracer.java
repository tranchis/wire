package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class StateTracer extends JPanel implements ActionListener, ItemListener
{
	private ArrayList<StateFrame> frames;
	private StateHistory history;
	
	private int current = 0;
	private JPanel statePanel;
	private JButton b1,b2,b3,b4;
	private ButtonGroup g;
	private JRadioButton one,two,three;
	private ArrayList<JCheckBox> checks = new ArrayList<JCheckBox>();
		
	private static int displayedStatesNo = 3;
	private static boolean showBeliefs = true;
	private static boolean showGoals = true;
	private static boolean showPlans = true;
	private static boolean showStep = true;
		
	public StateTracer(StateHistory history)
	{
		setHistory(history);
		
		frames = new ArrayList<StateFrame>();
		for(int i=0; i < displayedStatesNo; i++) 
			frames.add(new StateFrame(Color.BLACK));
		
		setLayout(new BorderLayout());
		
		JPanel topButtons = new JPanel();
		topButtons.setLayout(new GridLayout(1,4,0,0));
		b1 = new JButton("<<");
		b2 = new JButton("<");
		b3 = new JButton(">");
		b4 = new JButton(">>");
		b1.setActionCommand("<<");
		b2.setActionCommand("<");
		b3.setActionCommand(">");
		b4.setActionCommand(">>");
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		topButtons.add(b1);
		topButtons.add(b2);
		topButtons.add(b3);
		topButtons.add(b4);
		
		statePanel = new JPanel();
		statePanel.setLayout(new GridLayout(1,displayedStatesNo,0,0));

		for (StateFrame f : frames) 
			statePanel.add(f);
		
		JPanel bottomRightButtons = new JPanel();
		bottomRightButtons.setLayout(new FlowLayout());
		
		addCheckButton("Show Beliefs",bottomRightButtons);
		addCheckButton("Show Goals",bottomRightButtons);
		addCheckButton("Show Plans",bottomRightButtons);
		addCheckButton("Show Deliberation Step",bottomRightButtons);
		
		JPanel bottomLeftButtons = new JPanel();
		bottomLeftButtons.setLayout(new FlowLayout());
		g = new ButtonGroup();
		one = new JRadioButton("1");
		two = new JRadioButton("2");
		three = new JRadioButton("3");
		three.setSelected(true);
		one.addItemListener(this);
		two.addItemListener(this);
		three.addItemListener(this);
		g.add(one);
		g.add(two);
		g.add(three);
		bottomLeftButtons.add(new JLabel("Show states: "));
		bottomLeftButtons.add(one);
		bottomLeftButtons.add(two);
		bottomLeftButtons.add(three);
		
		JPanel bottomButtons = new JPanel();
		bottomButtons.setLayout(new BorderLayout());
		bottomButtons.add(bottomLeftButtons,BorderLayout.WEST);
		bottomButtons.add(bottomRightButtons,BorderLayout.EAST);
		
		add(topButtons,BorderLayout.NORTH);
		add(bottomButtons,BorderLayout.SOUTH);
		add(statePanel,BorderLayout.CENTER);
		setVisible(true);
	}
	
	private void makeFrames()
	{
		statePanel.removeAll();
		statePanel.setLayout(new GridLayout(1,displayedStatesNo,0,0));
		frames = new ArrayList<StateFrame>();
		
		for(int i=0; i<displayedStatesNo; i++) 
			frames.add(new StateFrame(Color.BLACK));
		
		for (StateFrame f : frames) 
			statePanel.add(f);
		
		showOnly();
		validate();
	}
		
	private void addCheckButton(String s, JPanel p)
	{
		JCheckBox c = new JCheckBox(s);
		c.addItemListener(this);
		c.setSelected(true);
		checks.add(c);
		p.add(c);
	}
	
	/**
	 * Updates the information in the state tracer.
	 * 
	 * @param resetPos determines whether to reset the position to display the end of the history
	 */
	public void update(boolean resetPos)
	{
		if (history == null) return;
		
		int firstState = history.getFirstState();
		int lastState = history.getLastState();
		
		// Show the end of the history
		if (resetPos) 
			current = lastState - (displayedStatesNo - 1);
		
		current = Math.max(firstState, current);
		
		int c = current;		
		for (StateFrame f : frames) {
			if (c >= firstState && c <= lastState) 
				f.update(history, c);
			else 
				f.clear();			
			c++;
		}
		
		// Enable/disable "<", "<<" buttons
		if (current <= firstState) {
			b1.setEnabled(false);
			b2.setEnabled(false);
			
		}			 
		else {
			b1.setEnabled(true);
			b2.setEnabled(true);
		}
		
		// Enable/disable ">", ">>" buttons				
		if (current + (displayedStatesNo-1) >= lastState) {
			b3.setEnabled(false); 
			b4.setEnabled(false); 
		}
		else {
			b3.setEnabled(true);
			b4.setEnabled(true);
		}
			
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (history==null) return;
		
		if ("<".equals(e.getActionCommand())) 
			current--;
		
		if (">".equals(e.getActionCommand())) 
			current++;
		
		if ("<<".equals(e.getActionCommand())) 
			current = history.getFirstState();
		
		if (">>".equals(e.getActionCommand())) 
			current = Math.max(0, history.getLastState() - (displayedStatesNo - 1));
			
		update(false);
	}
	
	public void setDisplayedStatesNo(int nof)
	{
		displayedStatesNo = nof;
		makeFrames();
		update(false);
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getItem() instanceof JRadioButton) {
			JRadioButton b = (JRadioButton)(e.getItem());
			if (b.isSelected()) {
				int a = 0;
				if (b==one) a = 1;
				else if (b==two) a = 2;
				else if (b==three) a = 3;
				if (a>0) setDisplayedStatesNo(a);
			}
		}
		else {
			if (checks.size()>=4) {
				if (e.getItem()==checks.get(0)) showBeliefs = checks.get(0).isSelected();
				if (e.getItem()==checks.get(1)) showGoals = checks.get(1).isSelected();
				if (e.getItem()==checks.get(2)) showPlans = checks.get(2).isSelected();
				if (e.getItem()==checks.get(3)) showStep = checks.get(3).isSelected();
			}
			showOnly();
			
			//If only one box is selected, this box should become disabled.
			int checked = 0;
			for (JCheckBox c : checks) if (c.isSelected()) checked++;
			if (checked==1) {for (JCheckBox c : checks) if (c.isSelected()) c.setEnabled(false);}
			else for (JCheckBox c : checks) c.setEnabled(true);
			
			validate();
		}
	}
	
	/**
	 * Hides or displays certain parts of a the state frame.
	 */
	private void showOnly()
	{
		if (checks.size()>=4) {
			for (StateFrame f : frames)
				f.showOnly(checks.get(0).isSelected(),checks.get(1).isSelected(),checks.get(2).isSelected(),checks.get(3).isSelected());
		}
	}
	
		
	public void isSelected()
	{
		checks.get(0).setSelected(showBeliefs);
		checks.get(1).setSelected(showGoals);
		checks.get(2).setSelected(showPlans);
		checks.get(3).setSelected(showStep);
		
		if (displayedStatesNo==1) one.setSelected(true);
		else if (displayedStatesNo==2) two.setSelected(true);
		else if (displayedStatesNo==3) three.setSelected(true);
		
		showOnly();
		validate();
	}
	
	public void setHistory(StateHistory history)
	{
		this.history = history;
	}
	

	

}

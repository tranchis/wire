package gui;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;

public class StateFrame extends JPanel
{
	private RTFFrame beliefs, goals, plans, step;
	private JLabel stateLabel;
	private JPanel p = new JPanel();
	
	public StateFrame(Color c)
	{
		setLayout(new BorderLayout());
		stateLabel = new JLabel(" ",JLabel.CENTER);
		add(stateLabel,BorderLayout.NORTH);
		
		stateLabel.setForeground(c);
		
		p.setLayout(new GridLayout(4,1,0,0));
		
		step = new RTFFrame("Step",false);
		beliefs = new RTFFrame("Beliefs",false);
		goals = new RTFFrame("Goals",false);
		plans = new RTFFrame("Plans",false);
		
		beliefs.setColor(c);
		goals.setColor(c);
		plans.setColor(c);
		
		p.add(step);
		p.add(beliefs);
		p.add(goals);
		p.add(plans);		 
		
		add(p,BorderLayout.CENTER);
	}
	
	public void update(StateHistory history, int statenr)
	{
		stateLabel.setText("State: " + statenr);
		
		step.update(history.getItem(StateHistory.LOGS, statenr));
		beliefs.update(history.getItem(StateHistory.BELIEFS, statenr));
		goals.update(history.getItem(StateHistory.GOALS, statenr));
		plans.update(history.getItem(StateHistory.PLANS, statenr));
		
	}	
	
	public void clear()
	{
		stateLabel.setText(" ");
		beliefs.clear();
		goals.clear();
		plans.clear();
	}
	
	public void showOnly(boolean showBeliefs, boolean showGoals, boolean showPlans, boolean showStep)
	{
		p.removeAll();
		
		int bases=0;
		if (showBeliefs) bases++;
		if (showGoals) bases++;
		if (showPlans) bases++;
		if (showStep) bases++;
		
		p.setLayout(new GridLayout(bases,1,0,0));
		
		if (showStep) p.add(step);
		if (showBeliefs) p.add(beliefs);
		if (showGoals) p.add(goals);
		if (showPlans) p.add(plans);
		
	}
}

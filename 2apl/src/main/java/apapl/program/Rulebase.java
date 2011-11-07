package apapl.program;

import apapl.APLModule;
import apapl.Parser;
import apapl.plans.PlanSeq;
import apapl.plans.Plan;
import apapl.program.Rule;
import apapl.program.PRrule;
import apapl.data.Goal;
import apapl.data.Term;
import apapl.data.Query;
import java.util.ArrayList;
import apapl.SubstList;
import java.util.ArrayList;
import apapl.SubstList;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * The root of all bases that have {@link apapl.program.Rule}s stored in it. Implements
 * the functionality to store and iterate over rules stored in the base.
 */
public abstract class Rulebase<E extends Rule> extends Base implements Iterable<E>, Cloneable
{
	
	protected ArrayList<E> rules = new ArrayList<E>();
	


	/**
	 * Checks whether the rule base contains no rules.
	 * 
	 * @return true if the base is empty, false otherwise
	 */
	public boolean isEmpty()
	{
		return rules.isEmpty();
	}
	
	/**
	 * Adds a rule to the rule base. 
	 * 
	 * @param rule the rule to add
	 */
	public void addRule(E rule)
	{
		rules.add(rule);
	}
	
	/**
	 * Converts the rule base to a string.
	 * 
	 * @return the string representing the rule base
	 */
	public String toString()
	{
		if (rules.size()>0)
			return concatWith(rules,",\n") + "\n\n";
		else return "";
	}
	
	/**
	 * Pretty print, to display this rule base in a readable format.
	 * 
	 * @return the string format of this rule base 
	 */
	public String pp()
	{
		String s = "";
		for (Rule r : rules)
			s = s + r.pp()+"\n\n";
		
		if (s.length()>=3) s = s.substring(0,s.length()-3);	
		
		return s+"\n\n";
	}
	
	/**
	 * Returns all the rules that are stored in this rule base.
	 * 
	 * @return the list of rules
	 */
	public ArrayList<E> getRules()
	{
		return rules;
	}
	
	/**
	 * Sets the content of the rulebase
	 * 
	 * @param rules new content of the rulebase
	 */
	public void setRules(ArrayList<E> rules) {
		this.rules = new ArrayList<E>(rules);
	}
	
	/**
	 * Converts this rule base to a RTF string.
	 * 
	 * @return the RTF string
	 */
	public String toRTF()
	{
		String s = "";
		for (E r : rules)
			s = s + r.toRTF()+"\\par\n\\par\n";
		
		if (s.length()>=11) s = s.substring(0,s.length()-11);	
		
		return s;
	}
	
	/**
	 * Returns the iterator to iterate over the rule stored in this rule base.
	 * 
	 * @return the iterator
	 */
	public Iterator<E> iterator()
	{
		return rules.iterator();
	}
	

}

package apapl.program;

import apapl.data.*;
import apapl.plans.*;
import java.util.ArrayList;
import apapl.SubstList;

/**
 * A procedure call rule that is used to generate plans for received messages, events and
 * for abstract actions. A PC-rule consists of a head (an {@link apapl.data.APLFunction}
 * corresponding to a message, event or an abstract action), guard ({@link apapl.data.Query})
 * and body ({@link apapl.plans.PlanSeq}).
 */
public class PCrule extends Rule
{
	private APLFunction head;
	private Query guard;
	private PlanSeq body;
	
	/**
	 * Constructs a PC-rule.
	 * 
	 * @param head the head
	 * @param guard the guard
	 * @param body the body
	 */
	public PCrule(APLFunction head, Query guard, PlanSeq body)
	{
		this.head = head;
		this.guard = guard;
		this.body = body;
	}
	
	/**
	 * Returns the head of this rule.
	 * 
	 * @return the head
	 */
	public APLFunction getHead()
	{
		return head;
	}
	
	/**
	 * Returns the guard of this rule.
	 * 
	 * @return the guard
	 */
	public Query getGuard()
	{
		return guard;
	}
	
	/**
	 * Returns the body of this rule.
	 * 
	 * @return the body
	 */
	public PlanSeq getBody()
	{
		return body;
	}
	
	/**
	 * copnverts this object to a string.
	 * 
	 * @return the string representation of this object
	 */
	public String toString()
	{
		return head + " <- " + guard + " | " + body;
	}
	
	/**
	 * Pretty print, for displaying this rule in a readable format.
	 *  
	 * @return the string representation of this rule
	 */
	public String pp()
	{
		return head+" <- "+guard+(body.oneliner()?" | "+body:" |\n\t"+body.pp(1));
	}
	
	/**
	 * Applies a subsitution to this rule.
	 * 
	 * @param theta the substitution
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
		head.applySubstitution(theta);
		guard.applySubstitution(theta);
		body.applySubstitution(theta);
	}
	
	/**
	 * Clones this rule.
	 * 
	 * @return the clone
	 */
	public PCrule clone()
	{
		return new PCrule(head.clone(),guard.clone(),body.clone());
	}
	
	public String toRTF()
	{
		return head.toRTF()+" \\cf1<- \\cf0 "+guard.toRTF()+" \\cf1| \\cf0"+(body.oneliner()?" "+body.toRTF():" \\par\n\\tab"+body.toRTF(1));
	}
	
	/**
	 * Returns a list of variables that can become bounded in this rule.
	 * 
	 * @return the list of variables
	 */
	public ArrayList<String> canBeBounded()
	{
		ArrayList<String> canBeBounded = head.getVariables();
		canBeBounded.addAll(guard.getVariables());
		
		for (Plan p : body) canBeBounded.addAll(p.canBeBounded());
		return canBeBounded;
	}
	
	/**
	 * Returns a list of variables that must become bounded in this rule.
	 * 
	 * @return a list of variables
	 */
	public ArrayList<String> mustBeBounded()
	{
		ArrayList<String> mustBeBounded = new ArrayList<String>();
		for (Plan p : body) mustBeBounded.addAll(p.mustBeBounded()); 
		return mustBeBounded;
	}
	
	/**
	 * Returns the type of this rule as a string.
	 * 
	 * @return the type string
	 */
	public String getRuleType()
	{
		return "PC rule";
	}
	
	/**
	 * Turn all variables of this pc-rule into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this PC-rule
	 * @param changes list [[old,new],...] of substitutions
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		head.freshVars(unfresh,own,changes);
		guard.freshVars(unfresh,own,changes);
		body.freshVars(unfresh,own,changes);
	}
	
	/**
	 * Returns all variables that occur in this PC-rule.
	 * 
	 * @return the list of variables
	 */
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = head.getVariables();
		vars.addAll(guard.getVariables());
		vars.addAll(body.getVariables());
		return vars;
	}
	
	/**
	 * Returns a clone of this rule with fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @return a fresh clone
	 */
	public PCrule getVariant(ArrayList<String> unfresh)
	{
		PCrule variant = clone();
		variant.freshVars(unfresh,getVariables());
		return variant;
	}
}
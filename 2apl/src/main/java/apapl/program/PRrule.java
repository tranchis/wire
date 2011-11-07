package apapl.program;

import apapl.data.*;
import apapl.plans.*;
import java.util.ArrayList;
import apapl.SubstList;

/**
 * A plan repair rule. A plan repair rule consists of a head (a {@link apapl.plans.PlanSeq},
 * the plan pattern that corresponds to the plan that can be repaired by this rule),
 * guard (a {@link apapl.data.Query}) and body (a {@link apapl.plans.PlanSeq}).
 */
public class PRrule extends Rule
{
	private PlanSeq head;
	private Query guard;
	private PlanSeq body;
	
	/**
	 * Constructs a plan repair rule.
	 * 
	 * @param head the head
	 * @param guard the guard
	 * @param body the body
	 */
	public PRrule(PlanSeq head, Query guard, PlanSeq body)
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
	public PlanSeq getHead()
	{
		return head;
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
	 * Returns the guard of this rule.
	 * 
	 * @return the guard
	 */
	public Query getGuard()
	{
		return guard;
	}
	
	/**
	 * Pretty print, for displaying the rule in a readable format.
	 * 
	 * @return the string representation of this rule
	 */
	public String pp()
	{
		return head.pp(0)+" <- "+guard+(body.oneliner()?" | "+body:" |\n\t"+body.pp(1));
	}
	
	/**
	 * Returns a clone of this rule.
	 * 
	 * @return the clone
	 */
	public PRrule clone()
	{
		return new PRrule(head.clone(),guard.clone(),body.clone());
	}

	/**
	 * Converts this object to a string.
	 * 
	 * @return the string representation of this rule
	 */
	public String toString()
	{
		return head + " <- " + guard + " | " + body;
	}

	/**
	 * Converts this rule to a RTF representation.
	 * 
	 * @return the RTF string
	 */
	public String toRTF()
	{
		return	head.toRTF(0)
		+		" \\cf1 <- \\cf0 "
		+		guard.toRTF()
		+		" \\cf1 | \\cf0 "
		+		(	body.oneliner()
				?	body.toRTF(0)
				:	"\\par\n\\tab"+body.toRTF(1)
				)
		;
	}
	
	/**
	 * Turn all variables of this rule into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this rule (the variables
	 *   that occur inside the plan that is being repaired)
	 * @param changes list [[old,new],...] of substitutions
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		head.freshVars(unfresh,own,changes);
		guard.freshVars(unfresh,own,changes);
		body.freshVars(unfresh,own,changes);
	}
	
	/**
	 * Returns the variables that occur inside this rule.
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
	 * Returns a list of variables that can become bounded in this rule.
	 * 
	 * @return the list of variables
	 */
	public ArrayList<String> canBeBounded()
	{
		ArrayList<String> canBeBounded = guard.getVariables();
		for (Plan p : head)
			canBeBounded.addAll(p.getVariables());
		for (Plan p : body)
			canBeBounded.addAll(p.canBeBounded());
		return canBeBounded;
	}
	
	/**
	 * Returns a list of variables that must become bounded in this rule.
	 * 
	 * @return the list of variables
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
		return "PR rule";
	}	
}
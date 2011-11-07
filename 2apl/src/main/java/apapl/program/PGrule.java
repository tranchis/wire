package apapl.program;

import apapl.data.*;
import apapl.plans.*;
import java.util.ArrayList;
import apapl.SubstList;

/**
 * A planning goal rule for generating plans for goals. A PG-rule consists of a head
 * (a {@link apapl.data.Query} corresponding to the goal for which the rule can be applied),
 * a guard ({@link apapl.data.Query}) and body ({@link apapl.plans.PlanSeq}). 
 */
public class PGrule extends Rule
{
	private Query head;
	private Query guard;
	private PlanSeq body;
	
	/**
	 * Constructs a PG-rule.
	 * 
	 * @param head the head
	 * @param guard the guard
	 * @param body the body
	 */
	public PGrule(Query head, Query guard, PlanSeq body)
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
	public Query getHead()
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
	 * Converts this object to a string.
	 * 
	 * @return the string representation of this rule
	 */
	public String toString()
	{
		return ((head instanceof True)?"":head+" ") + "<- " + guard + " | " + body;
	}
	
	/**
	 * Pretty print, for displaying the rule in a readable format.
	 * 
	 * @return the string representation of this rule
	 */
	public String pp()
	{
		return ((head instanceof True)?"":head+" ")+" <- "+guard+(body.oneliner()?" | "+body:" |\n\t"+body.pp(1));
	}
	
	/**
	 * Applies a substitution to this rule.
	 * 
	 * @param theta the substitution to be applied.
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
		head.applySubstitution(theta);
		guard.applySubstitution(theta);
		body.applySubstitution(theta);
	}
	
	/**
	 * Returns a clone of this rule.
	 * 
	 * @return the clone
	 */
	public PGrule clone()
	{
		return new PGrule(head.clone(),guard.clone(),body.clone());
	}
	
	/**
	 * Converts this rule to a RTF representation.
	 * 
	 * @return the RTF string
	 */
	public String toRTF()
	{
		return	(	(head instanceof True)
				?	""
				:	head.toRTF()+" "
				)
		+		" \\cf1 <- \\cf0 "
		+		guard.toRTF()
		+		" \\cf1 | \\cf0 "
		+		(	body.oneliner()
				?	body.toRTF()
				:	"\\par\n\\tab"+body.toRTF(1)
				)
		;
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
		return "PG rule";
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
	public PGrule getVariant(ArrayList<String> unfresh)
	{
		PGrule variant = clone();
		variant.freshVars(unfresh,getVariables());
		return variant;
	}
}

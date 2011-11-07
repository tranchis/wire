package apapl.plans;

import apapl.NoRuleException;
import apapl.APLModule;
import apapl.data.APLFunction;
import apapl.data.APLIdent;
import apapl.data.Term;
import apapl.program.PCrule;
import apapl.program.Beliefbase;
import apapl.program.PCrulebase;
import java.util.ArrayList;
import java.util.LinkedList;
import apapl.SubstList;

/**
 * A variable that ranges over plans. A plan variable is a special type
 * of plan that cannot be executed. Plan variables occur inside the head
 * and body of {@apapl.program.PRrule}s.
 */
public class PlanVariable extends Plan
{
	private String name;
	
	/**
	 * Constructs a plan variable.
	 * 
	 * @param name the name of this variable
	 */
	public PlanVariable(String name)
	{
		this.name = name;
	}
	
	/**
	 * Returns the name of this variable.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * A plan variable cannot be executed. This method should never be performed on
	 * a plan variable.
	 * 
	 * @param module the module this plan belongs to
	 * @return
	 */
	public PlanResult execute(APLModule module)
	{
		return new PlanResult(this, PlanResult.FAILED);
	}
	
	/**
	 * Not implemented for a plan variable.
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
	}
	
	public String toRTF(int t)
	{
		return name;
	}
	
	/**
	 * A plan variable cannot contain variables. Consequently, this
	 * method returns an empty list.
	 * 
	 * @return an empty list.
	 */	
	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}
	
	/**
	 * Clones this object.
	 * 
	 * @return the clone
	 */
	public PlanVariable clone()
	{
		return new PlanVariable(name+"");
	}
	
	/**
	 * Gives a string representation of this object
	 */
	public String toString()
	{
		return name;
	}
	
	/**
	 * Not implemented for a plan variable.
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
	}
	
	/**
	 * Not implemented for a plan variable.
	 */
	public void checkPlan(LinkedList<String> warnings, APLModule module)
	{
	}

	public APLIdent getPlanDescriptor() {
		return new APLIdent("planvar");
	}
	
}

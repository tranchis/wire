package apapl.plans;

import apapl.APLModule;
import apapl.data.APLIdent;
import apapl.data.Term;
import apapl.data.Query;
import apapl.program.Beliefbase;
import apapl.program.Base;
import apapl.SubstList;
import java.util.ArrayList;
import java.util.LinkedList;
import apapl.data.Test;
import apapl.data.PlanTest;

import apapl.program.Beliefbase;
import apapl.program.Goalbase;
import apapl.ModuleAccessException;
import apapl.SubstList;
import apapl.SolutionIterator;

/**
 * A test action.
 */
public class TestAction extends Plan
{
	private Test test;
	
	public TestAction(Test test)
	{
		this.test = test;
	}
	
	public PlanResult execute(APLModule module)
	{	
		// Ensure that all plan tests will be performed outside the calling module. 
		// Testing plans of the calling module is not allowed.
		Test t = test;
		do {
			if (t instanceof PlanTest) {
				if (t.getModuleId() == null || t.getModuleId().equals(module.getName())) {
					return new PlanResult(this, PlanResult.FAILED);
				}					
			}
		}
		while ((t = t.getNext()) != null );
		
		SubstList<Term> theta = test.test(module);
		
		if (theta!=null) {
			parent.removeFirst();
			parent.applySubstitution(theta);
			return new PlanResult(this, PlanResult.SUCCEEDED);
		}
		else
		{
			return new PlanResult(this, PlanResult.FAILED);
		}
	}
	
	public String toString()
	{
		return test.toString();
	}
	
	public TestAction clone()
	{
		return new TestAction(test.clone());
	}
	
	public void applySubstitution(SubstList<Term> theta)
	{
		test.applySubstitution(theta);
	}
	
	public String toRTF(int t)
	{
		return test.toRTF(true);
	}
	
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		test.freshVars(unfresh,own,changes);
	}
	
	public ArrayList<String> getVariables()
	{
		return test.getVariables();
	}
	
	public ArrayList<String> canBeBounded()
	{
		return test.getVariables();
	}
	
	public ArrayList<String> mustBeBounded()
	{
		return new ArrayList<String>();
	}
	
	public Test getTest()
	{
		return test;
	}

	public APLIdent getPlanDescriptor() {
		return new APLIdent("testaction");
	}
}

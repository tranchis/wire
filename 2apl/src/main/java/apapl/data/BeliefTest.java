package apapl.data;

import apapl.program.Beliefbase;
import apapl.program.Goalbase;
import apapl.APLModule;
import apapl.ModuleAccessException;
import apapl.SubstList;
import apapl.SolutionIterator;

/**
 * Corresponds to a test on the belief base. A belief test is part of a 
 * {@link apapl.data.Test} consisting of a composed sequence of goal and
 * belief tests.
 */
public class BeliefTest extends Test
{
	/**
	 * Constructs a belief test.
	 * 
	 * @param query the query to be performed on the belief base
	 * @param next the rest of the test (belief or goal test)
	 */
	public BeliefTest(Query query, Test next)
	{
		this.query = query;
		this.next = next;
	}
	
	/**
	 * Constructs a belief test.
	 * 
	 * @param moduleId the local identifier of the module on which is the test performed
	 * @param query the query to be performed on the belief base
	 */
	public BeliefTest(APLIdent moduleId, Query query)
	{
		this.moduleId = moduleId;
		this.query = query;
		this.next = null;
	}

	/**
	 * Performs this test on the module beliefbase
	 * 
	 * @param module the module that is performing the test. Relative module IDs will be resolved against this module.
	 * @return the substitution if the test succeeds, null otherwise
	 */
	public SubstList<Term> test(APLModule module)	
	{		
		APLModule testedModule = null;		
		try {	
			if (moduleId == null)
				testedModule = module;
			else
				testedModule = module.getMas().getModule(module, moduleId.getName());
		}
		catch (ModuleAccessException e)
		{   // test fails if the module does not exist or is not accessible
			return null;
		}
		
		Beliefbase bb = testedModule.getBeliefbase();
		
		SolutionIterator solutions = bb.doTest(query);
		if (next == null) {
			return solutions.next();
		}
		else {
			for (SubstList<Term> solution : solutions) {
				if (solution == null) 
					return null;
				Test next2 = next.clone();
				next2.applySubstitution(solution);
				SubstList<Term> solution2 = next2.test(module);
				if (solution2 != null) {
					solution2.putAll(solution);
					return solution2;
				}
			}
		}
		return null;
	}
	
	/**
	 * Clones this belief test.
	 * 
	 * @return the clone.
	 */	
	public BeliefTest clone()
	{
		if (next==null) return new BeliefTest(moduleId, query.clone());
		else return new BeliefTest(query.clone(),next.clone());
	}
	
	public String toString()
	{	
		String ms = "";
		if (moduleId != null)
			ms = moduleId + ".";
		
		return ms + "B("+super.toString()+")";
	}
	
	public String toRTF(boolean inplan)
	{
		return toString();
	}
}
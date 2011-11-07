package apapl.data;

import apapl.program.Beliefbase;
import apapl.program.Goalbase;
import apapl.APLModule;
import apapl.ModuleAccessException;
import apapl.SubstList;
import apapl.SolutionIterator;
import apapl.Unifier;

/**
 * An exact goal test. Corresponds to a test on the goal base. A goal test is part of a 
 * {@link apapl.data.Test} consisting of a composed sequence of goal and
 * belief tests. An exact goal test succeeds if some substitution can be found s.t. the
 * test is exactly equal to some goal in the goal base.
 */
public class GoalTestExact extends Test
{
	/**
	 * Constructs an exact goal test.
	 * 
	 * @param query the query to be performed on the goal base
	 * @param next the rest of the test (belief or goal test)
	 */
	public GoalTestExact(Query query, Test next)
	{
		this.query = query;
		this.next = next;
	}

	/**
	 * Constructs an exact goal test.
	 * 
	 * @param moduleId the local identifier of the module on which is the test performed
	 * @param query the query to be performed on the goal base
	 */
	public GoalTestExact(APLIdent moduleId, Query query)
	{
		this.moduleId = moduleId;
		this.query = query;
		this.next = null;
	}

	/**
	 * Performs this test on the goal base. An exact goal test succeeds if 
	 * some substitution can be found s.t. the test is exactly equal to some goal 
	 * in the goal base.
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
		Goalbase gb = testedModule.getGoalbase();
		
		Goal testgoal = new Goal(query.toLiterals());
		if (query.containsDisjunct()) 
			return null;		 
			
		for (Goal goal : gb) {
			SubstList<Term> solution = new SubstList<Term>();
			if (Unifier.unify(goal,testgoal,solution)){
				if (next==null) return solution;
				if (solution==null) return null;
				Test next2 = next.clone();
				next2.applySubstitution(solution);
				SubstList<Term> solution2 = next2.test(module);
				if (solution2!=null) {
					solution2.putAll(solution);
					return solution2;
				}
			}
		}
		return null;
	}
	
	public GoalTestExact clone()
	{
		if (next==null) return new GoalTestExact(moduleId, query.clone());
		else return new GoalTestExact(query.clone(),next.clone());
	}
		
	public String toString()
	{
		String ms = "";
		if (moduleId != null)
			ms = moduleId + ".";
		
		return ms + "!G("+super.toString()+")";
	}
}
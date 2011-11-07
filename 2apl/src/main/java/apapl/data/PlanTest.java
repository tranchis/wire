package apapl.data;

import apapl.program.Beliefbase;
import apapl.program.Goalbase;
import apapl.program.Planbase;
import apapl.APLModule;
import apapl.ModuleAccessException;
import apapl.SubstList;
import apapl.SolutionIterator;

/**
 * Corresponds to a test on the plan base. A plan test is part of a
 * {@link apapl.data.Test} consisting of a composed sequence of goal, belief and
 * plan tests. A plan test succeeds if at least one of the actions to be
 * executed in the next deliberation cycle is of the type described by the plan
 * querying language.
 */
public class PlanTest extends Test
{

	/**
	 * Constructs a plan test.
	 * 
	 * @param query the query to be performed on the plan base
	 * @param next the rest of the test (belief, goal or plan test)
	 */
	public PlanTest(Query query, Test next)
	{
		this.query = query;
		this.next = next;
	}

	/**
	 * Constructs a plan test.
	 * 
	 * @param moduleId the local identifier of the module on which is the test
	 *        performed
	 * @param query the query to be performed on the plan base
	 */
	public PlanTest(APLIdent moduleId, Query query)
	{
		this.moduleId = moduleId;
		this.query = query;
		this.next = null;
	}

	/**
	 * Performs this test on the module planbase
	 * 
	 * @param module the module that is performing the test. Relative module IDs
	 *        will be resolved against this module.
	 * @return the substitution if the test succeeds, null otherwise
	 */
	public SubstList<Term> test(APLModule module)
	{
		APLModule testedModule = null;

		try
		{
			if (moduleId == null)
				testedModule = module;
			else
				testedModule = module.getMas().getModule(module,
						moduleId.getName());
		} catch (ModuleAccessException e)
		{ // test fails if the module does not exist or is not accessible
			return null;
		}

		Planbase pb = testedModule.getPlanbase();
        
        SolutionIterator solutions = pb.doTest(query);
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
        
		
		/*
		boolean succeeded = false;
		try
		{
			succeeded = pb.doTest(query);
		} catch (Exception e)
		{
			e.printStackTrace();
			// fail the test action
			return null;
		}

		if (next == null)
		{
			if (succeeded)
				return new SubstList<Term>();
			else
				return null;
		} else if (succeeded)
		{
			Test next2 = next.clone();
			SubstList<Term> solution2 = next2.test(module);
			if (solution2 != null)
			{
				return solution2;
			}
		}
		return null;
		*/
	}

	/**
	 * Clones this plan test.
	 * 
	 * @return the clone.
	 */
	public PlanTest clone()
	{
		if (next == null)
			return new PlanTest(moduleId, query.clone());
		else
			return new PlanTest(query.clone(), next.clone());
	}

	public String toString()
	{
		String ms = "";
		if (moduleId != null)
			ms = moduleId + ".";

		return ms + "P(" + super.toString() + ")";
	}

	public String toRTF(boolean inplan)
	{
		return toString();
	}
}
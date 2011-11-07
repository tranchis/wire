package apapl.deliberation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import apapl.APLModule;
import apapl.ActivationGoalAchievedException;
import apapl.ModuleDeactivatedException;
import apapl.NoRuleException;
import apapl.SubstList;
import apapl.data.APLFunction;
import apapl.data.Term;
import apapl.data.Test;
import apapl.plans.Plan;
import apapl.plans.PlanResult;
import apapl.plans.PlanSeq;

/**
 * The deliberation step that checks module's stopping condition. If it is
 * satisfied, the module returns execution control to its parent along with the
 * substitution that satisfied the stopping condition.
 */
public class TestStoppingCond implements DeliberationStep
{
	/**
	 * Tests whether stopping condition can be satisfied.
	 * 
	 * @return the result of this deliberation step
	 */
	public DeliberationResult execute(APLModule module)
	{
		Test stoppingCond = module.getStoppingCond();
		SubstList<Term> theta = null;
		// Contains the list of plans executed as a part of stopping plan.
		ArrayList<PlanResult> executedStoppingPlan = null;

		if (stoppingCond != null)
		{
			// Test the stopping condition against the beliefbase, goalbase and
			// planbase of the module
			theta = stoppingCond.test(module);

			if (theta != null)
			{
				// Stopping condition has been satisfied
				executedStoppingPlan = processStoppingPlan(module);
				module.getMas().returnFromModule(module, theta);
			}
		}
		return (new TestStoppingCondResult(stoppingCond, theta,
				executedStoppingPlan));
	}

	/**
	 * Processes stopping plan in an non-interleaving mode.
	 * 
	 * @param module the module that is being stopped
	 * @return the list of results of actions that have been successfully
	 *         executed, null if no stopping plan rule was specified.
	 */

	public ArrayList<PlanResult> processStoppingPlan(APLModule module)
	{
		ArrayList<PlanResult> executed = new ArrayList<PlanResult>();
		SubstList<Term> theta = new SubstList<Term>();
		try
		{
			PlanSeq planseq = module.getPCrulebase().selectRule(
					module.getBeliefbase(), new APLFunction("stop"),
					new ArrayList<String>(), theta).getBody().clone();

			planseq.applySubstitution(theta);
			LinkedList<Plan> plans = planseq.getPlans();

			while (true)
			{
				try
				{
					Plan plan = plans.getFirst();
					PlanResult result;
					if ((result = plan.execute(module)).failed())
					{
						break;
					} else
					{
						executed.add(result);
					}
				} catch (NoSuchElementException e)
				{
					break;
				} catch (ActivationGoalAchievedException e)
				{
					e.printStackTrace();
				} catch (ModuleDeactivatedException e)
				{
					e.printStackTrace();
				}
			}

		} catch (NoRuleException e)
		{
			return null;
		}

		return executed;
	}

	public String toString()
	{
		return "Test Stopping Condition";
	}
}

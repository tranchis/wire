package apapl.deliberation;

import java.util.*;

import apapl.data.*;
import apapl.deliberation.DeliberationResult.InfoMessage;
import apapl.plans.Plan;
import apapl.plans.PlanResult;
import apapl.program.*;
import apapl.*;

/**
 * The result of {@link TestStoppingCond} deliberation step.
 * 
 * @see apapl.deliberation.TestStoppingCond
 */
public class TestStoppingCondResult extends DeliberationResult
{
	Test condition;
	SubstList<Term> theta;
	ArrayList<PlanResult> executedStoppingPlan;

	public TestStoppingCondResult(Test condition, SubstList<Term> theta,
			ArrayList<PlanResult> executedStoppingPlan)
	{
		this.condition = condition;
		this.theta = theta;
		this.executedStoppingPlan = executedStoppingPlan;
	}

	public LinkedList<InfoMessage> listInfo()
	{
		LinkedList<InfoMessage> info = new LinkedList<InfoMessage>();
		if (condition == null)
		{
			info.add(new InfoMessage("No stopping condition"));
		} else
		{
			if (theta == null)
			{
				info.add(new InfoMessage("Stopping condition " + condition
						+ " not satisfied"));
			} else
			{
				info.add(new InfoMessage("Stopping condition " + condition
						+ " satisfied with substitution " + theta + ""));
			}

			if (executedStoppingPlan == null)
			{
				info.add(new InfoMessage("No stopping plan specified"));
			} else
			{
				if (executedStoppingPlan.size() == 0)
				{
					info.add(new InfoMessage("No plans were executed"));
				} else
				{
					for (PlanResult r : executedStoppingPlan)
					{
						info.add(new InfoMessage(r));
					}
				}
			}
		}
		return info;
	}

	public String stepName()
	{
		return ("Test Stopping Condition");
	}

	public boolean moduleChanged()
	{
		if (condition == null)
			return false;

		// Returns true if stopping condition was satisfied, false otherwise
		return (theta != null);
	}
}

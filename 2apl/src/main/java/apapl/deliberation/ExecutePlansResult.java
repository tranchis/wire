package apapl.deliberation;

import java.util.*;

import apapl.deliberation.DeliberationResult.InfoMessage;
import apapl.program.*;
import apapl.plans.*;
import apapl.*;

/**
 * The result of the deliberation step in which plans are executed. Contains
 * information about which plans have been executed and which plans failed during
 * their execution.
 */
public abstract class ExecutePlansResult extends DeliberationResult
{
	// Results of plan execution
	protected LinkedList<PlanResult> results;

	/**
	 * Constructs a new result.
	 */
	public ExecutePlansResult()
	{
		results = new LinkedList<PlanResult>();
	}

	/**
	 * Adds the result of plan execution.
	 * 
	 * @param r the result
	 */
	public void addPlanResult(PlanResult r)
	{
		results.add(r);
	}

	/**
	 * Lists the information about this step in text format. In particular, it
	 * lists which plans have been executed and which plans failed.
	 * 
	 * @return a list of strings pertaining to the information
	 */
	public LinkedList<InfoMessage> listInfo()
	{
		LinkedList<InfoMessage> info = new LinkedList<InfoMessage>();

		if (results.isEmpty())
		{
			info.add(new InfoMessage("No plans were executed"));
		} else
		{
			for (PlanResult r : results)
			{
				info.add(new InfoMessage(r));
			}
		}
		return (info);
	}

	public boolean moduleChanged()
	{
		return (!results.isEmpty());
	}
}

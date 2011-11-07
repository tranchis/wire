package apapl.plans;

/**
 * The result of a plan execution. This object stores information about the
 * outcome of the action and optional additional message.
 * 
 * @author Michal Cap
 */
public class PlanResult
{
	public static int SUCCEEDED = 0;
	public static int FAILED = 1;
	public static int NO_EFFECT = 2;

	private int outcome = NO_EFFECT;
	private String message = null;
	private Plan plan;

	/**
	 * Creates new plan result object with message.
	 * 
	 * @param plan the plan that generated the result
	 * @param outcome the outcome of the execution. One of:
	 *        PlanResult.SUCCEEDED, PlanResult.FAILED or PlanResult.NO_EFFECT
	 * @param message additional message describing the circumstances leading to
	 *        the outcome
	 */
	public PlanResult(Plan plan, int outcome, String message)
	{
		this.plan = plan;
		this.outcome = outcome;
		this.message = message;
	}

	/**
	 * Creates new plan result object with no additional message.
	 * 
	 * @param plan plan that generated the result
	 * @param outcome outcome of the execution. One of: PlanResult.SUCCEEDED or
	 *        PlanResult.FAILED, PlanResult.NO_EFFECT
	 */
	public PlanResult(Plan plan, int outcome)
	{
		this.plan = plan;
		this.outcome = outcome;
	}

	/**
	 * Returns the outcome of the plan execution.
	 * 
	 * @return the outcome
	 */
	public int getOutcome()
	{
		return outcome;
	}

	/**
	 * Returns plan that generated the result.
	 * 
	 * @return the plan that produced this result
	 */
	public Plan getPlan()
	{
		return plan;
	}

	/**
	 * Returns message associated with the outcome.
	 * 
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Determines whether plan has succeeded.
	 * 
	 * @return <code>true</code> if plan has succeeded, <code>false</code>
	 *         otherwise
	 */
	public boolean succeeded()
	{
		return outcome == PlanResult.SUCCEEDED;
	}

	/**
	 * Determines whether plan has failed.
	 * 
	 * @return <code>true</code> if plan has failed, <code>false</code>
	 *         otherwise
	 */
	public boolean failed()
	{
		return outcome == PlanResult.FAILED;
	}

	/**
	 * Determines whether plan has had no effect.
	 * 
	 * @return <code>true</code> if plan has had no effect, <code>false</code>
	 *         otherwise
	 */
	public boolean noEffect()
	{
		return outcome == PlanResult.NO_EFFECT;
	}

}

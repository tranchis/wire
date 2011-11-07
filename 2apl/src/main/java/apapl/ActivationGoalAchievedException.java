package apapl;

/**
 * Signals that while executing a plan the goal for which the plan has been generated 
 * is achieved. This information can then be used to decide to stop executing and drop 
 * the plan (see for example {@link apapl.deliberation.ExecuteAllPlans}).
 * 
 * @see apapl.plans
 */
public class ActivationGoalAchievedException extends Exception
{	
}
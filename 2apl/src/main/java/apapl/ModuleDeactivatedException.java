package apapl;

import apapl.plans.ExecuteModuleAction;

/**
 * Signals that the execute module action handed over the execution control to
 * the child module. This exception does not signify any kind of execution
 * problem. It is merely used to inform deliberation cycle that it has lost
 * execution control and therefore should immediately stop.
 * 
 * @see apapl.plans.ExecuteModuleAction
 */
public class ModuleDeactivatedException extends Exception
{
	// The action that caused module deactivation
	ExecuteModuleAction plan;

	/**
	 * Constructs a module deactivated exception.
	 * 
	 * @param plan the plan that has thrown this exception
	 */
	public ModuleDeactivatedException(ExecuteModuleAction plan)
	{
		super();
		this.plan = plan;
	}

	/**
	 * Returns the action that caused module deactivation.
	 * 
	 * @return the action that caused this exception
	 */
	public ExecuteModuleAction getPlan()
	{
		return plan;
	}

	/**
	 * Sets the action that caused module deactivation.
	 * 
	 * @param plan the plan that has thrown this exception
	 */
	public void setPlan(ExecuteModuleAction plan)
	{
		this.plan = plan;
	}


}
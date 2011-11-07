package apapl.plans;

import java.util.ArrayList;

import apapl.APLModule;
import apapl.ModuleAccessException;
import apapl.ModuleDeactivatedException;
import apapl.SubstList;
import apapl.data.APLIdent;
import apapl.data.Term;
import apapl.data.Test;

/**
 * Execute module action will hand execution control over to the specified
 * module. The executed module will start deliberation process and keeps the
 * execution control as long as given stopping condition is not satisfied. 
 * <p>
 * It has following syntax: <code>&lt;MODULEID&gt;".execute("&lt;STOPCOND&gt;")"</code>.
 * Where <code>&lt;MODULEID&gt;</code> is module to be executed and
 * <code>&lt;STOPCOND&gt;</code> is a test expression representing the stopping
 * condition.
 * 
 * @author Michal Cap
 */
public class ExecuteModuleAction extends ModulePlan
{

	/**
	 * Once this stopping condition is satisfied in the child module, the
	 *  execution control is returned back to this module.
	 */
	private Test stoppingCond;

	/**
	 * Contains the module that has been executed as an outcome of this action.
	 * If this variable is not <code>null</code>, it indicates that this module
	 * is inactive and waits for the child module to return.
	 */
	private APLModule executedModule;

	/**
	 * Creates an execute module action.
	 * 
	 * @param moduleId the name of the module to execute
	 * @param stoppingCond the stopping condition 
	 */
	public ExecuteModuleAction(APLIdent moduleId, Test stoppingCond)
	{
		this.moduleId = moduleId;
		this.stoppingCond = stoppingCond.clone();
		this.executedModule = null;
	}

	public PlanResult execute(APLModule module)
			throws ModuleDeactivatedException
	{
		try
		{
			executedModule = module.getMas().getModule(module, moduleId.getName());
			executedModule.setStoppingCond(stoppingCond);
			module.getMas().executeChildModule(module, executedModule);

			// Inform the deliberation process, that the module has lost the
			// execution control
			throw new ModuleDeactivatedException(this);
		} catch (ModuleAccessException e)
		{
			executedModule = null;
			return new PlanResult(this, PlanResult.FAILED,
					"Module is not accessible: " + e.getMessage());
		}
	}

	public ExecuteModuleAction clone()
	{
		return new ExecuteModuleAction(moduleId, stoppingCond);
	}

	public String toString()
	{
		return moduleId + ".execute(" + stoppingCond + ")";
	}

	public String toRTF(int t)
	{
		return moduleId + ".\\cf4 execute\\cf0 (" + stoppingCond.toRTF(true)
				+ ")";
	}

	public void applySubstitution(SubstList<Term> theta)
	{
		stoppingCond.applySubstitution(theta);
	}

	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own,
			ArrayList<ArrayList<String>> changes)
	{
		stoppingCond.freshVars(unfresh, own, changes);
	}

	public ArrayList<String> getVariables()
	{
		return stoppingCond.getVariables();
	}

	public ArrayList<String> canBeBounded()
	{
		return stoppingCond.getVariables();
	}

	public ArrayList<String> mustBeBounded()
	{
		return new ArrayList<String>();
	}

	/**
	 * Returns the module for which execution is this action responsible.
	 * 
	 * @return the executed module
	 */
	public APLModule getExecutedModule()
	{
		return executedModule;
	}

	public APLIdent getPlanDescriptor()
	{
		return new APLIdent("executemoduleaction");
	}

	/**
	 * Returns the stopping condition.
	 * 
	 * @return the stopping condition
	 */
	public Test getStoppingCond()
	{
		return stoppingCond;
	}
}

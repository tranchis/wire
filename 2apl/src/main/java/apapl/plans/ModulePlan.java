package apapl.plans;

import apapl.data.APLIdent;

/**
 * The superclass of all plans that can occur inside a
 * {@link apapl.plans.PlanSeq} and can be executed on a specific module.
 * That is plans that have following syntactical form:
 * <code>&lt;MODULEID&gt;"."&lt;PLAN&gt;</code> 
 */
public abstract class ModulePlan extends Plan
{

	/** Identifier of the module on which the action operates. */
	protected APLIdent moduleId;

	/**
	 * Returns the relative identifier of the module on which the action
	 * operates.
	 * 
	 * @return the module identifier
	 */
	public APLIdent getModuleId()
	{
		return moduleId;
	}

	/**
	 * Sets the relative identifier of the module on which the action operates.
	 * 
	 * @param moduleId the module identifier to set
	 */
	public void setModuleId(APLIdent moduleId)
	{
		this.moduleId = moduleId;
	}

}

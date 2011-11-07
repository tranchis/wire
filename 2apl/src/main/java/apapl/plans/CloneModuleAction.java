package apapl.plans;

import java.util.ArrayList;

import apapl.APLModule;
import apapl.CreateModuleException;
import apapl.data.APLIdent;

/**
 * Clone module action will crate an instance of module based on an existing
 * module instance. It has following syntax: 
 * <p>
 * <code>"clone("&lt;IDENT&gt;","&lt;IDENT&gt;")"</code>. First parameter is the 
 * existing module, the second parameter is the identifier of the module created 
 * as the result of cloning.
 * 
 * @author Michal Cap
 */
public class CloneModuleAction extends Plan
{

	/** The module to be cloned */
	private APLIdent model;
	/** The name of newly created module */
	private APLIdent identifier;

	/**
	 * Creates the clone module action.
	 * 
	 * @param model the name of the module to be cloned
	 * @param identifier the name given to the new module created by cloning
	 */
	public CloneModuleAction(APLIdent model, APLIdent identifier)
	{
		this.model = model;
		this.identifier = identifier;
	}

	public PlanResult execute(APLModule module)
	{
		try
		{
			module.getMas().cloneModule(module, model.toString(), identifier.toString());
		} catch (CreateModuleException e)
		{
			return new PlanResult(this, PlanResult.FAILED,
					"Cloning failed: \n" + e.getMessage());
		}

		parent.removeFirst();
		return new PlanResult(this, PlanResult.SUCCEEDED);
	}

	public CloneModuleAction clone()
	{
		return new CloneModuleAction(model, identifier);
	}

	public String toString()
	{
		return "clone(" + model + ", " + identifier + ")";
	}

	public String toRTF(int t)
	{
		return "\\cf4 clone\\cf0 (" + model + ", " + identifier + ")";
	}

	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}

	public APLIdent getPlanDescriptor()
	{
		return new APLIdent("clonemoduleaction");
	}

	/**
	 * Returns model for the cloning.
	 * 
	 * @return the identifier of the model module
	 */
	public APLIdent getModel()
	{
		return model;
	}

	/**
	 * Returns the identifier of the clone module.
	 * 
	 * @return the identifier of the clone
	 */
	public APLIdent getIdentifier()
	{
		return identifier;
	}
}

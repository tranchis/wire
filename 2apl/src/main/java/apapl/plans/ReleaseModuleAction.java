package apapl.plans;

import java.util.ArrayList;

import apapl.APLModule;
import apapl.ModuleAccessException;
import apapl.data.APLIdent;

/**
 * Release module action will release the instance of the module specified by
 * the given identifier. 
 * <p>
 * It has following syntax:
 * <code>"release("&lt;IDENT&gt;")"</code>. First parameter is the identifier of
 * the module to be released.
 * 
 * @author Michal Cap
 */
public class ReleaseModuleAction extends Plan
{

	// local module identifier
	private APLIdent identifier;

	/**
	 * Creates the release module action.
	 * 
	 * @param identifier the name of the module to be released
	 */
	public ReleaseModuleAction(APLIdent identifier)
	{
		this.identifier = identifier;
	}

	public PlanResult execute(APLModule module)
	{
		try
		{
			module.getMas().releaseModule(module, identifier.toString());
		} catch (ModuleAccessException e)
		{
			return new PlanResult(this, PlanResult.FAILED,
					"Module is not accessible: " + e.getMessage());
		}

		parent.removeFirst();
		return new PlanResult(this, PlanResult.SUCCEEDED);
	}

	public ReleaseModuleAction clone()
	{
		return new ReleaseModuleAction(identifier);
	}

	public String toString()
	{
		return "release(" + identifier + ")";
	}

	public String toRTF(int t)
	{
		return "\\cf4 release\\cf0 (" + identifier + ")";
	}

	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}

	public APLIdent getPlanDescriptor()
	{
		return new APLIdent("releaseaction");
	}

	/**
	 * Returns the name of the module to be released. 
	 * 
	 * @return the identifier of the released module
	 */
	public APLIdent getIdentifier()
	{
		return identifier;
	}
}

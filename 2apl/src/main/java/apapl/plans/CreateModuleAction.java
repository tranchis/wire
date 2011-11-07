package apapl.plans;

import java.util.ArrayList;

import apapl.APLModule;
import apapl.CreateModuleException;
import apapl.data.APLIdent;

/**
 * Create module action will create an instance of module from the module
 * specification. 
 * <p>
 * It has following syntax:
 * <code>"create("&lt;IDENT&gt;","&lt;IDENT&gt;")"</code>. First parameter is
 * the module specification, the second is the identifier of the module.
 * 
 * @author Michal Cap
 */
public class CreateModuleAction extends Plan
{

	/** Module specification name. */
	private APLIdent specification;
	/** Local module identifier. */
	private APLIdent identifier;

	/**
	 * Creates a create module action.
	 * 
	 * @param specification the name of the module specification
	 * @param identifier the name given to the newly created module
	 */
	public CreateModuleAction(APLIdent specification, APLIdent identifier)
	{
		this.specification = specification;
		this.identifier = identifier;
	}

	public PlanResult execute(APLModule module)
	{
		try
		{
			module.getMas()
				.createModule(module, specification.toString(), identifier.toString());
		} catch (CreateModuleException e)
		{
			return new PlanResult(this, PlanResult.FAILED,
					"Failed to parse module specification: \n" + e.getMessage());
		}

		parent.removeFirst();
		return new PlanResult(this, PlanResult.SUCCEEDED);
	}

	public CreateModuleAction clone()
	{
		return new CreateModuleAction(specification, identifier);
	}

	public String toString()
	{
		return "create(" + specification + ", " + identifier + ")";
	}

	public String toRTF(int t)
	{
		return "\\cf4 create\\cf0 (" + specification + ", " + identifier + ")";
	}

	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}

	public APLIdent getPlanDescriptor()
	{
		return new APLIdent("createaction");
	}

	/**
	 * Returns the module specification name.
	 * 
	 * @return the specification
	 */
	public APLIdent getSpecification()
	{
		return specification;
	}

	/**
	 * Returns the name of newly created module.
	 * 
	 * @return the identifier
	 */
	public APLIdent getIdentifier()
	{
		return identifier;
	}
}

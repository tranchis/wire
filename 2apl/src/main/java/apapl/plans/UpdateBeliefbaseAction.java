package apapl.plans;

import java.util.ArrayList;

import apapl.APLModule;
import apapl.ModuleAccessException;
import apapl.SubstList;
import apapl.UnboundedVarException;
import apapl.data.APLIdent;
import apapl.data.Literal;
import apapl.data.Term;

/**
 * Update beliefbase action adds and/or removes specified beliefs from the
 * beliefbase of the given module. Positive literals will be added to the
 * beliefbase, negative literals will be removed. 
 * <p>
 * The beliefbase update action has the following syntax:
 * 
 * <code>&lt;MODULEID&gt;".updateBB("&lt;LITERAL&gt;(","&lt;LITERAL&gt;)*")"</code>
 * The &lt;MODULEID&gt; is the name of the module whose beliefbase will be
 * updated, literals describe the update to perform: positive literals will be
 * added to the belifbase, negative ones will be removed.
 * 
 * @author Michal Cap
 */
public class UpdateBeliefbaseAction extends ModulePlan
{

	private ArrayList<Literal> literals;

	/**
	 * Creates a belief update action.
	 * 
	 * @param moduleId name of the module on which will be the
	 *        update performed
	 * @param literals the list of literals specifying the belief updates
	 */
	public UpdateBeliefbaseAction(APLIdent moduleId, ArrayList<Literal> literals)
	{
		this.moduleId = moduleId;
		this.literals = literals;
	}

	public PlanResult execute(APLModule module)
	{
		try
		{
			APLModule affectedModule = null;
			if (moduleId == null)
				affectedModule = module;
			else
				affectedModule = module.getMas().getModule(module, moduleId.getName());

			// Check access rights
			if (affectedModule.isAccessibleFrom(module)
					&& !affectedModule.isActive())
			{
				for (Literal literal : literals)
				{
					literal.unvar();
					affectedModule.getBeliefbase().assertBelief(literal);
				}
			} else
			{
				throw (new ModuleAccessException(moduleId,
						"Access to the module denied"));
			}

			parent.removeFirst();
			return new PlanResult(this, PlanResult.SUCCEEDED);
		} catch (ModuleAccessException e)
		{
			return new PlanResult(this, PlanResult.FAILED,
					"Module is not accessible: " + e.getMessage());
		} catch (UnboundedVarException e)
		{
			return new PlanResult(this, PlanResult.FAILED, 
					"Belief update contains unbound variables.");
		}
	}

	public UpdateBeliefbaseAction clone()
	{
		ArrayList<Literal> literalsCopy = new ArrayList<Literal>();

		for (Literal literal : literals)
		{
			literalsCopy.add(literal.clone());
		}

		return new UpdateBeliefbaseAction(moduleId, literalsCopy);
	}

	public String toString()
	{
		// Create the comma separated representation of the literals
		String s = "";
		for (Literal i : literals)
			s = s + i + ", ";

		if (s.length() >= 2)
			s = s.substring(0, s.length() - 2);

		return moduleId + ".updateBB(" + s + ")";
	}

	public String toRTF(int t)
	{
		// Create the comma separated representation of the literals
		String s = "";
		for (Literal l : literals)
			s = s + l.toRTF() + ", ";

		if (s.length() >= 2)
			s = s.substring(0, s.length() - 2);

		return (moduleId == null ? "" : (moduleId + "."))
				+ "\\cf4 updateBB\\cf0 (" + s + ")";
	}

	public ArrayList<String> getVariables()
	{
		ArrayList<String> variables = new ArrayList<String>();
		for (Literal literal : literals)
		{
			variables.addAll(literal.getVariables());
		}
		return variables;
	}

	public ArrayList<String> canBeBounded()
	{
		return new ArrayList<String>();
	}

	public ArrayList<String> mustBeBounded()
	{
		return getVariables();
	}

	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own,
			ArrayList<ArrayList<String>> changes)
	{
		for (Literal literal : literals)
		{
			literal.freshVars(unfresh, own, changes);
		}
	}

	public void applySubstitution(SubstList<Term> theta)
	{
		for (Literal literal : literals)
		{
			literal.applySubstitution(theta);
		}
	}

	public APLIdent getPlanDescriptor()
	{
		return new APLIdent("updatebeliefbaseaction");
	}
	/**
	 * Returns the literals specifying the belief update. 
	 * 
	 * @return the list of literals
	 */
	public ArrayList<Literal> getLiterals()
	{
		return literals;
	}

}

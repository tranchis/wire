package apapl.plans;

import apapl.APLModule;
import apapl.data.APLIdent;
import apapl.data.Term;
import apapl.program.Base;
import apapl.SubstList;
import java.util.ArrayList;
import apapl.ActivationGoalAchievedException;

/**
 * A skip action.
 */
public class Skip extends Plan
{
	
	public PlanResult execute(APLModule module)
	{
		parent.removeFirst();
		return new PlanResult(this, PlanResult.SUCCEEDED);
	}
	
	public String toString()
	{
		return "skip";
	}
			
	public Skip clone()
	{
		return new Skip();
	}
	
	public void applySubstitution(SubstList<Term> theta)
	{
	}
	
	
	public String toRTF(int t)
	{
		return "\\cf5 skip\\cf0 ";
	}
	
	public ArrayList<String> getVariables()
	{
		return new ArrayList<String>();
	}

	public APLIdent getPlanDescriptor() {
		return new APLIdent("skipaction");
	}
}

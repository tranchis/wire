package apapl.plans;

import apapl.APLModule;
import apapl.data.APLIdent;
import apapl.data.Test;
import apapl.data.Query;
import apapl.data.Term;
import apapl.program.Beliefbase;
import apapl.program.Goalbase;
import apapl.program.Base;
import java.util.ArrayList;
import java.util.LinkedList;
import apapl.SubstList;

/**
 * An iteration plan (while-loop).
 */
public class WhilePlan extends Plan
{

	private Test condition;
	private PlanSeq plan;
	
	public WhilePlan(Test condition, PlanSeq plan)
	{
		this.condition = condition;
		this.plan = plan;
	}
	
	public PlanResult execute(APLModule module)
	{
		SubstList<Term> theta = condition.test(module);
				
		if (theta!=null) {
			PlanSeq copy = plan.clone();
			parent.addFirst(copy);
		
			copy.applySubstitution(theta);
			
			ArrayList<String> unfresh = parent.getVariables();
			ArrayList<String> own = new ArrayList<String>();
			ArrayList<ArrayList<String>> changes = new ArrayList<ArrayList<String>>();
			copy.freshVars(unfresh,own,changes);
			theta.applyChanges(changes);						
		}
		else {
			parent.removeFirst();
		}
		return new PlanResult(this, PlanResult.SUCCEEDED);
	}
	

	public String toString()
	{
		return "while "+condition.toString()+" do "+plan.toScopeString();
	}
	
	public void applySubstitution(SubstList<Term> theta)
	{
		condition.applySubstitution(theta);
		plan.applySubstitution(theta);
	}
	
	public Test getCondition()
	{
		return condition;
	}
	
	public PlanSeq getPlan()
	{
		return plan;
	}
	
	public WhilePlan clone()
	{
		return new WhilePlan(condition.clone(),plan.clone());
	}
	
	public String pp(int t)
	{
		return "while\t"+condition+"\n"
		+		Base.tabs(t) + "do\t"+plan.pp(t+1);
	}
	
	public String toRTF(int t)
	{
		return "\\cf2 while\\cf0\\tab "+condition.toRTF(true)+"\\par\n"
		+		Base.rtftabs(t) + "\\cf2 do\\cf0\\tab "+plan.toRTF(t+1);
	}
	
	public String toRTF()
	{
		return	"\\cf2 while\\cf0  "+condition.toRTF(true)+" "
		+		"\\cf2 DO\\cf0  "+plan.toRTF();
	}
	
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		condition.freshVars(unfresh,own,changes);
		plan.freshVars(unfresh,own,changes);
	}
	
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = plan.getVariables();
		vars.addAll(condition.getVariables());
		return vars;
	}
	
	public ArrayList<String> getPlanVariables()
	{
		return plan.getPlanVariables();
	}
	
	public ArrayList<String> mustBeBounded()
	{
		ArrayList<String> mustBeBounded = new ArrayList<String>();
		ArrayList<String> a = condition.getVariables();
		
		for(Plan p : plan)
		for(String s : p.mustBeBounded())
			if (!a.contains(s)) mustBeBounded.add(s);
			
		return mustBeBounded;
	}
	
	public ArrayList<String> canBeBounded()
	{
		ArrayList<String> canBeBounded = new ArrayList<String>();
		ArrayList<String> a = condition.getVariables();
		
		for(Plan p : plan)
			canBeBounded.addAll(p.canBeBounded());
			
		return canBeBounded;
	}
	
	public void checkPlan(LinkedList<String> warnings, APLModule module)
	{
		for (Plan p : plan) p.checkPlan(warnings,module);
	}

	public APLIdent getPlanDescriptor() {
		return new APLIdent("whileplan");
	}
}

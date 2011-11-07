package apapl.plans;

import apapl.data.Test;
import apapl.APLModule;
import apapl.data.APLIdent;
import apapl.data.Query;
import apapl.data.Term;
import apapl.program.Base;
import apapl.program.Beliefbase;
import apapl.program.Goalbase;
import java.util.ArrayList;
import java.util.LinkedList;
import apapl.SubstList;

/**
 * A conditional plan.
 */
public class ConditionalPlan extends Plan
{
	
	private Test condition;
	private PlanSeq thenPlan;
	private PlanSeq elsePlan;
	
	public ConditionalPlan(Test condition, PlanSeq thenPlan, PlanSeq elsePlan)
	{
		this.condition = condition;
		this.thenPlan = thenPlan;
		this.elsePlan = elsePlan;
	}	

	public PlanResult execute(APLModule module)
	{
		SubstList<Term> theta = condition.test(module);
		if (theta!=null) {
			thenPlan.applySubstitution(theta);
			parent.removeFirst();
			parent.addFirst(thenPlan);
			
		}
		else {
			parent.removeFirst();
			parent.addFirst(elsePlan);
			
		}
		
		return new PlanResult(this, PlanResult.SUCCEEDED);
	}
	

	public String toString()
	{
		return "if "+condition.toString()+" then "+thenPlan.toScopeString()
		+ (elsePlan.isEmpty()?"":" else "+elsePlan.toScopeString());
	}
	
	public void applySubstitution(SubstList<Term> theta)
	{
		condition.applySubstitution(theta);
		thenPlan.applySubstitution(theta);
		elsePlan.applySubstitution(theta);
	}
	
	public ConditionalPlan clone()
	{
		return new ConditionalPlan(condition.clone(),thenPlan.clone(),elsePlan.clone());
	}
	
	public PlanSeq getThenPlan()
	{
		return thenPlan;
	}
	
	public PlanSeq getElsePlan()
	{
		return elsePlan;
	}
	
	public Test getCondition()
	{
		return condition;
	}
	
	public String pp(int t)
	{
		return "if\t"+condition.toString()+"\n"
		+		Base.tabs(t) + "then\t"+thenPlan.pp(t+1)
		+		(elsePlan.isEmpty()?"": "\n"+Base.tabs(t) + "else\t"+elsePlan.pp(t+1));
	}
	
	public String toRTF(int t)
	{
		return "\\cf2 if\\cf0 \\tab "+condition.toRTF(true)+"\\par\n"
		+		Base.rtftabs(t) + "\\cf2 then\\cf0 \\tab "+thenPlan.toRTF(t+1)
		+		(elsePlan.isEmpty()?"": "\\par\n"+Base.rtftabs(t) + "\\cf2 else\\cf0 \\tab "+elsePlan.toRTF(t+1));
	}
	
	public String toRTF()
	{
		return "\\cf2 if\\cf0  "+condition.toRTF(true)+" "
		+		"\\cf2 then\\cf0  "+thenPlan.toRTF()
		+		(elsePlan.isEmpty()?"": "\\cf2 else\\cf0  "+elsePlan.toRTF());
	}
	
	public ArrayList<String> getPlanVariables()
	{
		ArrayList<String> vars = thenPlan.getPlanVariables();
		vars.addAll(elsePlan.getPlanVariables());
		return vars;
	}
	
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = thenPlan.getVariables();
		vars.addAll(elsePlan.getVariables());
		vars.addAll(condition.getVariables());
		return vars;
	}
	
	
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		condition.freshVars(unfresh,own,changes);
		thenPlan.freshVars(unfresh,own,changes);
		if (elsePlan!=null) elsePlan.freshVars(unfresh,own,changes);
	}
	
	public ArrayList<String> canBeBounded()
	{
		ArrayList<String> canBeBounded = new ArrayList<String>();
		canBeBounded.addAll(condition.getVariables());
		
		for(Plan p : thenPlan)
			canBeBounded.addAll(p.canBeBounded());
		
		for(Plan p : elsePlan)
			canBeBounded.addAll(p.canBeBounded());
		
		return canBeBounded;
	}
	
	public ArrayList<String> mustBeBounded()
	{
		ArrayList<String> mustBeBounded = new ArrayList<String>();
		ArrayList<String> a = condition.getVariables();
		
		for(Plan p : thenPlan)
		for(String s : p.mustBeBounded())
			if (!a.contains(s)) mustBeBounded.add(s);
		
		for(Plan p : elsePlan)
		for(String s : p.mustBeBounded())
			if (!a.contains(s)) mustBeBounded.add(s);
		
		return mustBeBounded;
	}

	public void checkPlan(LinkedList<String> warnings, APLModule module)
	{
		for (Plan p : thenPlan) p.checkPlan(warnings,module);
		for (Plan p : elsePlan) p.checkPlan(warnings,module);
	}

	public APLIdent getPlanDescriptor() {
		return new APLIdent("ifplan");
	}
}

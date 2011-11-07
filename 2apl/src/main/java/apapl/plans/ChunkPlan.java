package apapl.plans;

import apapl.ActivationGoalAchievedException;
import apapl.APLModule;
import apapl.ModuleDeactivatedException;
import apapl.program.Base;
import apapl.program.Goalbase;
import apapl.program.Beliefbase;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import apapl.data.APLIdent;
import apapl.data.Literal;
import apapl.data.Term;
import apapl.deliberation.DeliberationResult;
import apapl.deliberation.ExecutePlansResult;
import apapl.deliberation.DeliberationResult.InfoMessage;
import apapl.SubstList;
import java.util.NoSuchElementException;

/**
 * An atomic (chunk) plan.
 */
public class ChunkPlan extends Plan implements ParentPlan
{

    private LinkedList<Plan> plans = new LinkedList<Plan>();
    private SubstList<Term> theta = new SubstList<Term>();
    
    public ChunkPlan()
    {
    }
    
    public PlanSeq toPlanSeq()
    {
        PlanSeq r = new PlanSeq();
        for (Plan p : plans) r.addLast(p);
        return r;
    }
    
    public void addPlan(Plan p)
    {
        p.setParent(this);
        plans.add(p);
    }
    
    public void addFirst(PlanSeq pl)
    {
        LinkedList<Plan> newplans = pl.getPlans();
        try
        {
            Plan p = newplans.removeLast();
            while (p!=null)
            {
                p.setParent(this);
                if (p instanceof ChunkPlan) addFirst((ChunkPlan)p);
                else plans.addFirst(p);
                p = newplans.removeLast();
            }
        }
        catch (NoSuchElementException e) {return;}
    }
    
    public void addFirst(ChunkPlan pl)
    {
        LinkedList<Plan> newplans = pl.getPlans();
        try
        {
            Plan p = newplans.removeLast();
            while (p!=null)
            {
                p.setParent(this);
                plans.addFirst(p);
                p = newplans.removeLast();
            }
        }
        catch (NoSuchElementException e) {return;}
    }

    public PlanResult execute(APLModule module) 
    throws ActivationGoalAchievedException, ModuleDeactivatedException
    {
        PlanResult lastResult = null;
        List<PlanResult> results = new ArrayList<PlanResult>();
        
        Goalbase gb = module.getGoalbase();
        Beliefbase bb = module.getBeliefbase();
        
        while (lastResult == null || lastResult.succeeded()) {
            if (!testActivationGoal(gb,bb)) 
                throw new ActivationGoalAchievedException();
            try {
                Plan plan = plans.getFirst();
                lastResult = plan.execute(module);
                results.add(lastResult);
            }
            catch (NoSuchElementException e) {
                parent.removeFirst();
                parent.applySubstitution(theta);
                
                return new PlanResult(this, PlanResult.SUCCEEDED, "Atomic Plan Fully Executed:\n" + planResultsToString(results));
            }           
        }
        
        // Plan executed only partially.
        parent.applySubstitution(theta);
        return new PlanResult(this, lastResult.getOutcome(), "Atomic Plan Failed:\n" + planResultsToString(results));
    }
    
    /**
     * Converts a list of plan results to a string.
     * 
     * @param results a list of plan results
     * @return the string that contains string representation of all provided
     *         plan results, each of them on a new line
     */
    private static String planResultsToString(List<PlanResult> results) {
        StringBuilder s = new StringBuilder();
        for (PlanResult result : results){
            InfoMessage msg = (new DeliberationResult.InfoMessage(result));
            s.append(msg.getMessage());
            s.append('\n');
        }
        return s.toString();
    }
    
    public boolean testActivationGoal(Goalbase gb, Beliefbase bb)
    {
        return parent.testActivationGoal(gb,bb);
    }
    
    public void removeFirst()
    {
        Plan p = plans.removeFirst();
    }
    
    public LinkedList<Plan> getPlans()
    {
        return plans;
    }
    
    public String toString()
    {
        if (plans.size()==1) return plans.get(0).toString();
        return "["+Base.concatWith(plans,"; ")+"]";
    }
    
    public void applySubstitution(SubstList<Term> theta)
    {
        this.theta.putAll(theta);
        
        for (Plan p : plans) 
            p.applySubstitution(theta);
    }
    
    public ChunkPlan clone()
    {
        ChunkPlan copy = new ChunkPlan();
        for (Plan p : plans) copy.addPlan(p.clone());
        return copy;
    }
    
    public String pp(int t)
    {
        if (plans.size()==1) return plans.get(0).pp(t);
        
        String s = "["+"\t";
        for (Plan p : plans)
            s = s + p.pp(t+1)+";\n"+Base.tabs(t+1);
        
        if (s.length()>=(t+3)) s = s.substring(0,s.length()-(t+3)); 
        
        return s+"\n"+Base.tabs(t)+"]";
    }
    
    public String toRTF(int t)
    {
        if (plans.size()==1) return plans.get(0).toRTF(t);
        
        String s = "["+"\\tab";
        for (Plan p : plans)
            s = s + p.toRTF(t+1)+"\\cf1 ;\\cf0 \\par\n"+Base.rtftabs(t+1);
        int l = ("\\cf1 ;\\cf0 \\par\n"+Base.rtftabs(t+1)).length();
        if (s.length()>=l) s = s.substring(0,s.length()-l); 
        
        return s+"\\par\n"+Base.rtftabs(t)+"]";
    }
    
    public ArrayList<String> getVariables()
    {
        ArrayList<String> vars = new ArrayList<String>();
        for (Plan p : plans) vars.addAll(p.getVariables());
        return vars;
    }
    
    //COPY PASTED FROM PlanSeq
    public ArrayList<String> getPlanVariables()
    {
        ArrayList<String> vars = new ArrayList<String>();
        for (Plan p : plans) {
            if (p instanceof ConditionalPlan) vars.addAll(((ConditionalPlan)p).getPlanVariables());
            else if (p instanceof WhilePlan) vars.addAll(((WhilePlan)p).getPlanVariables());
            else if (p instanceof ChunkPlan) vars.addAll(((ChunkPlan)p).getPlanVariables());
            else if (p instanceof PlanVariable) vars.add(((PlanVariable)p).getName());
        }
        return vars;
    }
    
    public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
    {
        for (Plan plan : plans) plan.freshVars(unfresh,own,changes);
    }
    
    public void checkPlan(LinkedList<String> warnings, APLModule module)
    {
        for (Plan p : plans) p.checkPlan(warnings,module);
    }
    
    public ArrayList<String> mustBeBounded()
    {
        ArrayList<String> mustBeBounded = new ArrayList<String>();
        for (Plan p : plans) mustBeBounded.addAll(p.mustBeBounded());
        return mustBeBounded;
    }
    
    public ArrayList<String> canBeBounded()
    {
        ArrayList<String> canBeBounded = new ArrayList<String>();
        for (Plan p : plans) canBeBounded.addAll(p.canBeBounded());
        return canBeBounded;
    }
    
    //COPY PASTED FROM PlanSeq
    public void applyPlanSubstitution(SubstList<PlanSeq> theta)
    {
        LinkedList<Plan> newPlans = new LinkedList<Plan>();
        for (Plan p : plans) {
            if (p instanceof PlanVariable)  {
                PlanSeq s = theta.get(((PlanVariable)p).getName());
                if (s!=null) for (Plan p2 : s) {
                    p2.setParent(this);
                    newPlans.addLast(p2);
                }
                else newPlans.addLast(p);
            }
            else if (p instanceof ConditionalPlan) {
                ConditionalPlan v = (ConditionalPlan)p;
                PlanSeq thenPlan = v.getThenPlan();
                PlanSeq elsePlan = v.getElsePlan();
                thenPlan.applyPlanSubstitution(theta);
                elsePlan.applyPlanSubstitution(theta);
                Plan s = new ConditionalPlan(v.getCondition(),thenPlan,elsePlan);
                newPlans.addLast(s);
            }
            else if (p instanceof WhilePlan) {
                WhilePlan v = (WhilePlan)p;
                PlanSeq plan = v.getPlan();
                plan.applyPlanSubstitution(theta);
                Plan s = new WhilePlan(v.getCondition(),plan);
                newPlans.addLast(s);
            }
            else if (p instanceof ChunkPlan) {
                ((ChunkPlan)p).applyPlanSubstitution(theta);
                newPlans.addLast(p);
            }
            else newPlans.addLast(p);
        }
        plans = newPlans;
    }

    /** 
     * Determines if any of the atomic plan actions is of a certain type. 
     * 
     * Overrides generic behavior defined in @link apapl.plans.Plan#isType(apapl.data.APLIdent).
     * @param typeIdent the queried plan type.
     * @return true if there is an action of a queried type as a part of the atomic plan, false otherwise.
     */

    public boolean isType(APLIdent typeIdent) {
        if (plans.size() > 0) {
            for(Plan p: plans) {            
                if (p.isType(typeIdent)) 
                    return true;
            }
        }   
        // nothing found
        return false;
    }

    public APLIdent getPlanDescriptor() {
        return new APLIdent("atomicplan");
    }
    
}

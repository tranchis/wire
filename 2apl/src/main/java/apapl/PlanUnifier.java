package apapl;

import apapl.data.*;
import apapl.plans.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Stack;
import java.util.LinkedList;

/**
 * A unification between a plan and a plan query. A plan unifier consists of a 
 * {@link apapl.plans.PlanSeq} query containing {apapl.plans.PlanVariable}s and a 
 * {@link apapl.plans.PlanSeq} plan. Plan queries typically occur in the 
 * head of a {@link apapl.program.PRrule}. 
 */
public class PlanUnifier
{
	private int i = 0;	//indicates the current position in the query list.
	private int j = 0;	//indicstes the current position in the plan list.

	private Plan[] query;
	private Plan[] plans;
	
	/**
	 * Constructs a PlanUnifier.
	 * 
	 * @param q the query
	 * @param p the plan that is unified
	 */
	public PlanUnifier(PlanSeq q, PlanSeq p)
	{
		query = q.getPlans().toArray(new Plan[1]);
		plans = p.getPlans().toArray(new Plan[1]);
	}
	
	/**
	 * Unifies a plan with a plan query.
	 * @param theta term subtition map that will be filled with the resulting term substitutions.
	 * @param thetaP plan subtition map that will be filled with the resulting plan substitutions.
	 * @param rest this will be filled with the part of the plan that doen't match with the query.
	 * @param ignoreChunks if true, chunks (atomic blocks) are ignored 
	 */
	public boolean unify(SubstList<Term> theta,SubstList<PlanSeq> thetaP, PlanSeq rest, boolean ignoreChunks)
	{
		//Do till the end of the query is reached.
		while (i<query.length) {
			//If the there is some query left while the end of the plan is reached, matching fails.
			if (j>=plans.length) return false;
			//If the item in the query is a variable and it is the last item in the query, this variable has to be bounded to the rest of the plan.
			else if (query[i] instanceof PlanVariable && i==query.length-1) {
				PlanSeq n = new PlanSeq();
				for (int k = j; k<plans.length; k++) n.addLast(plans[k]);
				thetaP.put(((PlanVariable)query[i]).getName(),n);
				i++;
				j=plans.length;
			}
			//In all other cases where you find a variable in the query, bound that to just one plan.
			else if (query[i] instanceof PlanVariable) {
				// A variable in Q, but not the last one. Beacuse we don't know wat follows, we'll bound this to only one variable.
				// If this variable is gonna be bounded to more variables, it must be found a a last seen variable when looking back.
				// If this varibale is followed by another variable, it can never be the last seen variable from a non-var so in that case,
				// it will never become bounded to more than one plan.
				PlanSeq n = new PlanSeq();
				n.addLast(plans[j]);
				thetaP.put(((PlanVariable)query[i]).getName(),n);
				i++; j++;
			}
			//If the query is a conditonal plan. try to match it with a conditional plan from the plans.
			else if (query[i] instanceof ConditionalPlan) {
				if (plans[j] instanceof ConditionalPlan) {
					if (Unifier.unify((ConditionalPlan)query[i],(ConditionalPlan)plans[j],theta,thetaP,ignoreChunks)) {i++; j++;}
					//If the conditional plan from the plans does not match then bound the plan to the last seen variable.
					else if (!boundExtraToLastSeenVar(theta, thetaP)) return false;
				}
				//Or when the plan is not a contional plan at all then also bound the plan to the last seen variable.
				else if (!boundExtraToLastSeenVar(theta, thetaP)) return false;
			}
			//same story for while plans and chunk plans.
			else if (query[i] instanceof WhilePlan) {
				 if (plans[j] instanceof WhilePlan) {
					if (Unifier.unify((WhilePlan)(query[i]),(WhilePlan)(plans[j]),theta,thetaP,ignoreChunks)) {i++; j++;}
					else if (!boundExtraToLastSeenVar(theta, thetaP)) return false;
				 }
				 else if (!boundExtraToLastSeenVar(theta, thetaP)) return false;
			}
			else if (!ignoreChunks & query[i] instanceof ChunkPlan) {
				if (plans[j] instanceof ChunkPlan) {
					if (Unifier.unify((ChunkPlan)(query[i]),(ChunkPlan)(plans[j]),theta,thetaP,ignoreChunks)) {i++; j++;}
					else if (!boundExtraToLastSeenVar(theta, thetaP)) return false;
				}
				else if (!boundExtraToLastSeenVar(theta, thetaP)) return false;
			}
			//If there occurs a plan in the query, this should be matched with the plan in the plan.
			else { //must be a normal Plan
				//Try to match 2 normal plans.
				if (Unifier.unify(query[i],plans[j],theta)) {i++; j++; }
				//There seems to be no match so the previous bounded variable might be bounded to not enough plan.
				//Bound that variable to one more plan.
				else if (!boundExtraToLastSeenVar(theta, thetaP)) return false;
			}
		}
		
		for (int k=j; k<plans.length; k++) rest.addLast(plans[k]);
		return true;
	}
	
	/**
	 * Searches for the last seen variable in the query and bounds that variable to an extra plan.
	 * @return true if a variable was found.
	 */
	private boolean boundExtraToLastSeenVar(SubstList<Term> theta, SubstList<PlanSeq> thetaP)
	{
		//Search for the last visited variable in Q.
		//This variable might not be bounded to enough plans.
		int k=1; // Start with the previous plan.
		while (i-k>=0 && !(query[i-k] instanceof PlanVariable)) k++; //And go back till you find a variable.
		if (i-k>=0) {  //last seen variable in Q is Q(i-k)
			// Try to bound this variable t one more plan.
			thetaP.get(((PlanVariable)query[i-k]).getName()).addLast(plans[j+1-k]);
			//Term bounding made in q[i+1-k] should be made undone because they don't hold anymore.
			ArrayList<String> vars = (query[i+1-k]).getVariables();
			vars.addAll((plans[i+1-k]).getVariables());
			ArrayList<String> planVars = new ArrayList<String>();
			if (query[i+1-k] instanceof PlanVariable) planVars.add(((PlanVariable)query[i+1-k]).getName());
			if (query[i+1-k] instanceof ConditionalPlan) planVars.addAll(((ConditionalPlan)query[i+1-k]).getPlanVariables());
			if (query[i+1-k] instanceof WhilePlan) planVars.addAll(((WhilePlan)query[i+1-k]).getPlanVariables());
			if (query[i+1-k] instanceof ChunkPlan) planVars.addAll(((ChunkPlan)query[i+1-k]).getPlanVariables());
			for (String v : vars) theta.remove(v);
			for (String v : planVars) thetaP.remove(v);
			i = i+1-k; // Matching continues after the variable, these (k-1) non-vars have to be matched again.
			j = j+2-k; // P[j+1-k] was bounded to a variable so continue with P[j+2-k],
			return true;
		}
		else return false; // no variables seen in Q, so P[j] can become bounded to any variable so matchin fails.
	}		
}

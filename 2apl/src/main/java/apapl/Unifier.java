package apapl;

import apapl.data.*;
import apapl.plans.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;

/**
 * Convenience class providing functionality for unification of plans, goals, belief queries,
 * etc.
 */
public class Unifier
{
	public static boolean unify(ConditionalPlan q, ConditionalPlan p, SubstList<Term> theta,SubstList<PlanSeq> thetaP, boolean ignoreChunks)
	{
		if (!unify(q.getCondition(),p.getCondition(),theta)) return false;

		PlanSeq tq = q.getThenPlan(); tq.applySubstitution(theta);
		PlanSeq tp = p.getThenPlan(); tp.applySubstitution(theta);
		PlanSeq rest = new PlanSeq();
			
		PlanUnifier pu = new PlanUnifier(tq,tp);
		if (!pu.unify(theta,thetaP,rest,ignoreChunks)) return false;
		if (!rest.isEmpty()) return false;
		
		PlanSeq eq = q.getElsePlan(); eq.applySubstitution(theta);
		PlanSeq ep = p.getElsePlan().clone(); ep.applySubstitution(theta);
		
		if (eq.isEmpty() && ep.isEmpty()) return true;
		if (eq.isEmpty() || ep.isEmpty()) return false;
				
		pu = new PlanUnifier(eq,ep);
		if (!pu.unify(theta,thetaP,rest,ignoreChunks)) return false;
		if (!rest.isEmpty()) return false;
		return true;
	}
	
	public static boolean unify(ChunkPlan q, ChunkPlan p, SubstList<Term> theta,SubstList<PlanSeq> thetaP, boolean ignoreChunks)
	{
		SubstList<Term> theta2 = new SubstList<Term>();
		SubstList<PlanSeq> thetaP2 = new SubstList<PlanSeq>();
		PlanSeq rest2 = new PlanSeq();
		PlanUnifier pu = new PlanUnifier(new PlanSeq(q.getPlans()),p.toPlanSeq());
		if (pu.unify(theta2,thetaP2,rest2,ignoreChunks))
		if (rest2.isEmpty()||true) {
			theta.putAll(theta2);
			thetaP.putAll(thetaP2);
			return true;
		}
		return false;
	}
	
	
	public static boolean unify(WhilePlan q, WhilePlan p, SubstList<Term> theta,SubstList<PlanSeq> thetaP, boolean ignoreChunks)
	{
		if (!unify(q.getCondition(),p.getCondition(),theta)) return false;
		PlanSeq wq = q.getPlan(); wq.applySubstitution(theta);
		PlanSeq wp = p.getPlan(); wp.applySubstitution(theta);
		
		boolean r;
		PlanSeq rest = new PlanSeq();
		PlanUnifier pu = new PlanUnifier(wq,wp);
		if (!pu.unify(theta,thetaP,rest,ignoreChunks)) return false;
		if (!rest.isEmpty()) return false;
		return true;
	}
	
	
	public static boolean unify(Plan p1a, Plan p2a, SubstList<Term> theta)
	{
		Plan p1 = p1a.clone();
		Plan p2 = p2a.clone();
		p1.applySubstitution(theta);
		p2.applySubstitution(theta);
		
		if (p1 instanceof AbstractAction && p2 instanceof AbstractAction)
		{
			AbstractAction a1 = (AbstractAction)p1;
			AbstractAction a2 = (AbstractAction)p2;
			return unify(a1.getPlan(),a2.getPlan(),theta);
		}
		else if (p1 instanceof BeliefUpdateAction && p2 instanceof BeliefUpdateAction)
		{
			BeliefUpdateAction a1 = (BeliefUpdateAction)p1;
			BeliefUpdateAction a2 = (BeliefUpdateAction)p2;
			return unify(a1.getPlan(),a2.getPlan(),theta);
		}
		else if (p1 instanceof ConditionalPlan && p2 instanceof ConditionalPlan)
		{
			ConditionalPlan a1 = (ConditionalPlan)p1;
			ConditionalPlan a2 = (ConditionalPlan)p2;
			
			Test q1 = a1.getCondition();
			Test q2 = a2.getCondition();
			if (!unify(q1,q2,theta)) return false;
						
			PlanSeq tp1 = a1.getThenPlan();
			PlanSeq tp2 = a2.getThenPlan();
			PlanSeq ep1 = a1.getElsePlan();
			PlanSeq ep2 = a2.getElsePlan();
			
			tp1.applySubstitution(theta);tp2.applySubstitution(theta);
			ep1.applySubstitution(theta);ep2.applySubstitution(theta);
			
			for (int i=0; i<tp1.getPlans().size(); i++)
			if (!unify(tp1.getPlans().get(i),tp2.getPlans().get(i),theta)) return false;
			else {tp1.applySubstitution(theta);tp2.applySubstitution(theta);};
			
			if (ep1==null&&ep2==null) return true;
			if (ep1==null||ep2==null) return false;
			
			for (int i=0; i<ep1.getPlans().size(); i++)
			if (!unify(ep1.getPlans().get(i),ep2.getPlans().get(i),theta)) return false;
			else {ep1.applySubstitution(theta);ep2.applySubstitution(theta);};
			
			return true;
		}
		else if (p1 instanceof WhilePlan && p2 instanceof WhilePlan)
		{
			WhilePlan a1 = (WhilePlan)p1;
			WhilePlan a2 = (WhilePlan)p2;
			
			Test q1 = a1.getCondition();
			Test q2 = a2.getCondition();
			if (!unify(q1,q2,theta)) return false;
						
			PlanSeq w1 = a1.getPlan();
			PlanSeq w2 = a2.getPlan();
						
			w1.applySubstitution(theta);w2.applySubstitution(theta);
			
			for (int i=0; i<w1.getPlans().size(); i++)
			if (!unify(w1.getPlans().get(i),w2.getPlans().get(i),theta)) return false;
			else {w1.applySubstitution(theta);w2.applySubstitution(theta);};
			
			return true;
		}
		else if (p1 instanceof TestAction && p2 instanceof TestAction)
		{
			TestAction a1 = (TestAction)p1;
			TestAction a2 = (TestAction)p2;
			
			Test q1 = a1.getTest();
			Test q2 = a2.getTest();
			
			return unify(q1,q2,theta);
		}
		else if (p1 instanceof GoalAction && p2 instanceof GoalAction)
		{
			GoalAction a1 = (GoalAction)p1;
			GoalAction a2 = (GoalAction)p2;
			
			// Check whether action is performed on the same module
			if (a1.getModuleId() != null && a2.getModuleId() != null ) {
				if (!a1.getModuleId().equals(a2.getModuleId())) return false;
			}
			else if (a1.getModuleId() != null || a2.getModuleId() != null)
				return false;
			
			if (!a1.getAction().equals(a2.getAction())) return false;			
			
			Goal g1 = a1.getGoal();
			Goal g2 = a2.getGoal();
			
			//The first goal argument must me grounded.
			return unify(g2,g1,theta);
		}
		else if (p1 instanceof Skip && p2 instanceof Skip)
		{
			return true;
		}
		else if (p1 instanceof ExternalAction && p2 instanceof ExternalAction)
		{
			ExternalAction a1 = (ExternalAction)p1;
			ExternalAction a2 = (ExternalAction)p2;
			
			if (!unify(a1.getTimeout(),a2.getTimeout(),theta)) return false;
			a1.applySubstitution(theta);
			a2.applySubstitution(theta);
				
			String s1 = a1.getEnv();
			String s2 = a2.getEnv();
			if (!s1.equals(s2)) return false;
			
			APLFunction f1 = a1.getAction();
			APLFunction f2 = a2.getAction();
			
			if (!unify(f1,f2,theta)) return false;
			
			APLVar v1 = a1.getResultVar();
			APLVar v2 = a2.getResultVar();
			v1.applySubstitution(theta);
			v2.applySubstitution(theta);
			
			return unify(v1,v2,theta);
		}
		else if (p1 instanceof SendAction && p2 instanceof SendAction)
		{
			SendAction a1 = (SendAction)p1;
			SendAction a2 = (SendAction)p2;
		
			Term a,b;
		
			a = a1.getReceiver(); a.applySubstitution(theta);
			b = a2.getReceiver(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			a = a1.getPerformative(); a.applySubstitution(theta);
			b = a2.getPerformative(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			a = a1.getLanguage(); a.applySubstitution(theta);
			b = a2.getLanguage(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			a = a1.getOntology(); a.applySubstitution(theta);
			b = a2.getOntology(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			a = a1.getContent(); a.applySubstitution(theta);
			b = a2.getContent(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			return true;
		}
		else if (p1 instanceof CreateModuleAction && p2 instanceof CreateModuleAction)
		{
			CreateModuleAction a1 = (CreateModuleAction)p1;
			CreateModuleAction a2 = (CreateModuleAction)p2;
			
			Term a,b;
			a = a1.getIdentifier(); a.applySubstitution(theta);
			b = a2.getIdentifier(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			a = a1.getSpecification(); a.applySubstitution(theta);
			b = a2.getSpecification(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			return true;
		}
		else if (p1 instanceof CloneModuleAction && p2 instanceof CloneModuleAction)
		{
			CloneModuleAction a1 = (CloneModuleAction)p1;
			CloneModuleAction a2 = (CloneModuleAction)p2;
			
			Term a,b;
			a = a1.getIdentifier(); a.applySubstitution(theta);
			b = a2.getIdentifier(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			a = a1.getModel(); a.applySubstitution(theta);
			b = a2.getModel(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			return true;
		}
		else if (p1 instanceof ExecuteModuleAction && p2 instanceof ExecuteModuleAction)
		{
			ExecuteModuleAction a1 = (ExecuteModuleAction)p1;
			ExecuteModuleAction a2 = (ExecuteModuleAction)p2;
			
			Term a,b;
			a = a1.getModuleId(); a.applySubstitution(theta);
			b = a2.getModuleId(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			
			Test ta, tb;
			ta = a1.getStoppingCond(); ta.applySubstitution(theta);
			tb = a2.getStoppingCond(); tb.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;
			return true;
		}
		else if (p1 instanceof ReleaseModuleAction && p2 instanceof ReleaseModuleAction)
		{
			ReleaseModuleAction a1 = (ReleaseModuleAction)p1;
			ReleaseModuleAction a2 = (ReleaseModuleAction)p2;
			
			Term a,b;
			a = a1.getIdentifier(); a.applySubstitution(theta);
			b = a2.getIdentifier(); b.applySubstitution(theta);
			if (!unify(a,b,theta)) return false;

			return true;
		}
		else if (p1 instanceof UpdateBeliefbaseAction && p2 instanceof UpdateBeliefbaseAction)
		{
			UpdateBeliefbaseAction a1 = (UpdateBeliefbaseAction)p1;
			UpdateBeliefbaseAction a2 = (UpdateBeliefbaseAction)p2;
			
			// Check whether action is performed on the same module
			if (a1.getModuleId() != null && a2.getModuleId() != null ) {
				if (!a1.getModuleId().equals(a2.getModuleId())) return false;
			}
			else if (a1.getModuleId() != null || a2.getModuleId() != null)
				return false;
			
			ArrayList<Literal> ls1 = a1.getLiterals();			
			ArrayList<Literal> ls2 = a2.getLiterals();
			
			if (ls1.size() != ls2.size()) 
				return false;
			else
			{
				for(int i = 0; i < ls1.size(); i++)
				{
					if (!unify(ls1.get(i), ls2.get(i), theta))
						return false;
				}
			}

			return true;
		}	
		else if (p1 instanceof ChunkPlan && p2 instanceof ChunkPlan)
		{
			ChunkPlan c1 = (ChunkPlan)p1;
			ChunkPlan c2 = (ChunkPlan)p2;
			LinkedList<Plan> plans1 = c1.getPlans();
			LinkedList<Plan> plans2 = c2.getPlans();
			if (plans1.size()!=plans2.size()) return false;
			
			while (!plans1.isEmpty()&&!plans2.isEmpty()) {
				Plan a = plans1.poll();
				Plan b = plans2.poll();
				a.applySubstitution(theta);
				b.applySubstitution(theta);
				if (!unify(a,b,theta)) return false;
			}
			return true;
		}
		else return false;
	}
	
	// Two different implementations of goal unify. Use the most efficient one.
	public static boolean unify(Goal g1, Goal g2, SubstList<Term> theta)
	{
		// Prolog prolog = new Prolog();
		Query query = g2.convertToQuery();
		//for (Literal l : g1) prolog.addTerm(l.getBody().toString());
		ArrayList<SubstList<Term>> solutions  = g1.possibleSubstitutions(query);
		if (solutions.size()>0 && g1.uniqueItems()==g2.uniqueItems()) {
			//theta.putAll(bestSolution(solutions));
			theta.putAll(solutions.get(0));
			return true;
		}
		else return false;
		
	}
	
	public static boolean unify(Test t1, Test t2, SubstList<Term> theta)
	{
		if (!t1.getClass().getName().equals(t2.getClass().getName())) return false;
		
		// Check whether action is performed on the same module
		if (t1.getModuleId() != null && t2.getModuleId() != null ) {
			if (!t1.getModuleId().equals(t2.getModuleId())) return false;
		}
		else if (t1.getModuleId() != null || t2.getModuleId() != null)
			return false;
		
		Query query1 = t1.getQuery();
		Query query2 = t2.getQuery();
			
		if (!unify(query1, query2, theta)) 
			return false;
		
		Test n1 = t1.getNext();
		Test n2 = t2.getNext();
		
		if (n1==null&&n2==null) return true;
		if (n1==null||n2==null) return false;
		
		n1.applySubstitution(theta);
		n2.applySubstitution(theta);
		
		return unify(n1,n2,theta);
	}
	
	/**
	 * Reorders a list. Each unique <code>a</code> gives a unique order of the 
	 * different n elements in the list. There are fac(n) permutations. This 
	 * method returns the <code>a</code>th permutation.
	 * 
	 * @param list the list to reorder
	 * @param a the permutation
	 */
	public static <E> void reOrder(LinkedList<E> list, int a)
	{
		int n = list.size();
		if (n<2) return;
		E head = list.poll();
		reOrder(list,a % fac(n-1));
		list.add(a / fac(n-1),head);
	}
		
	/**
	 * Helper function for generating the faculty of n.
	 * 
	 * @param n
	 * @return
	 */
	public static int fac(int n)
	{
		return (n<2?1:n*fac(n-1));
	}
   
	public static boolean unify(Query q1, Query q2,  SubstList<Term> theta)
	{
		if (q1 instanceof Literal && q2 instanceof Literal)
		{
			Literal l1 = (Literal)q1;
			Literal l2 = (Literal)q2;
			if (l1.getSign()!=l2.getSign()) return false;
			return unify(l1.getBody(),l2.getBody(),theta);
		}
		else if (q1 instanceof AndQuery && q2 instanceof AndQuery)
		{
			AndQuery a1 = (AndQuery)q1;
			AndQuery a2 = (AndQuery)q2;
			
			if (!unify(a1.getLeft(),a2.getLeft(),theta)) return false;
			Query q3 = a1.getRight().clone(); q3.applySubstitution(theta);
			Query q4 = a2.getRight().clone(); q4.applySubstitution(theta);
		
			return unify(q3,q4,theta);
		}
		else if (q1 instanceof OrQuery && q2 instanceof OrQuery)
		{
			OrQuery a1 = (OrQuery)q1;
			OrQuery a2 = (OrQuery)q2;
			
			if (!unify(a1.getLeft(),a2.getLeft(),theta)) return false;
			Query q3 = a1.getRight().clone(); q3.applySubstitution(theta);
			Query q4 = a2.getRight().clone(); q4.applySubstitution(theta);
			
			return unify(q1,q2,theta);
		}
		else return false;
	}
	
	public static boolean unify(Term t1a, Term t2a, SubstList<Term> theta)
	{
		SubstList<Term> theta2 = new SubstList<Term>();
		Term t1 = t1a.clone();
		Term t2 = t2a.clone();
		t1.applySubstitution(theta);
		t2.applySubstitution(theta);
		
		if (t1 instanceof APLVar && t2 instanceof APLVar)	{
			APLVar v1 = (APLVar)t1;
			APLVar v2 = (APLVar)t2;
			if (!v1.isBounded()&&!v2.isBounded()) {
				if (!v1.getName().equals(v2.getName())) theta.put(v1.getName(),v2);
				return true;
			}
			else if (v1.isBounded()&&!v2.isBounded()) return unify(v1.getSubst(),v2,theta);
			else if (!v1.isBounded()&&v2.isBounded()) return unify(v1,v2.getSubst(),theta);
			else return unify(v1.getSubst(),v2.getSubst(),theta);
		}
		else if (t1 instanceof APLVar)	{
			APLVar v1 = (APLVar)t1;
			if (v1.isBounded()) return unify(v1.getSubst(),t2,theta);
			else {theta.put(v1.getName(),t2); return true;}
		}
		else if (t2 instanceof APLVar)	{
			APLVar v2 = (APLVar)t2;
			if (v2.isBounded()) return unify(t1,v2.getSubst(),theta);
			else {theta.put(v2.getName(),t1); return true;}
		}
		else if (t1 instanceof APLIdent && t2 instanceof APLIdent) {
			APLIdent i1 = (APLIdent)t1;
			APLIdent i2 = (APLIdent)t2;
			return i1.getName().equals(i2.getName());
		}
		else if (t1 instanceof APLNum && t2 instanceof APLNum) {
			APLNum d1 = (APLNum)t1;
			APLNum d2 = (APLNum)t2;
			return d1.equals(d2);
		}
		else if (t1 instanceof APLFunction && t2 instanceof APLFunction) {
			APLFunction a1 = (APLFunction)t1;
			APLFunction a2 = (APLFunction)t2;
			
			if (a1.getName().equals(a2.getName())) {
				ArrayList<Term> params1 = a1.clone().getParams();
				ArrayList<Term> params2 = a2.clone().getParams();
				if (params1.size()==params2.size())	{
					for (int i=0; i<params1.size(); i++) {
						Term arg1 = params1.get(i);
						Term arg2 = params2.get(i);
						if (!unify(arg1,arg2,theta2)) return false;
						for (int j=i+1; j<params1.size(); j++) {
							params1.get(j).applySubstitution(theta2);
							params2.get(j).applySubstitution(theta2);
						}
					}
					theta.putAll(theta2);
					return true;
				}
			}
			return false;
		}
		else if (t1 instanceof APLList && t2 instanceof APLList)
		{
			APLList l1 = (APLList)t1;
			APLList l2 = (APLList)t2;

			if (l1.isEmpty()&&l2.isEmpty()) return true;
			else if (l1.isEmpty()) return false;
			else if (l2.isEmpty()) return false;
			else if (unify(l1.getHead(),l2.getHead(),theta2))	{
				APLListVar v1 = l1.getTail().clone();
				APLListVar v2 = l2.getTail().clone();
				v1.applySubstitution(theta2);
				v2.applySubstitution(theta2);
				
				if (unify(v1,v2,theta2)) {
					theta.putAll(theta2);
					return true;
				} else return false;
			}
			else return false;
		}
		//else if (t1 instanceof EmptyList && t2 instanceof EmptyList) return true;
		//else if (t1 instanceof Skip && t2 instanceof Skip) return true;
		else return false;
	}
}

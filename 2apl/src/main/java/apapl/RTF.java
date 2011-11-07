package apapl;

import apapl.data.*;
import apapl.plans.*;
import apapl.program.*;

/**
 * Convenience class used for RTF representation of different 2APL objects with the purpose
 * of displaying them. Objects that can be converted to RTF include the bases and prolog 
 * formulae. At this moment, conversion to RTF is done by the classes themselves. In the 
 * future this functionality should be handled by this class.
 */
public class RTF
{
	
	public static String newline = "\\par\n";
	public static String tab = "\\tab ";
	public static String lbrace = "\\{";
	public static String rbrace = "\\}";
	public static String color1(String x) { return "\\c1 " + x + "\\c0 "; }
	public static String color2(String x) { return "\\c2 " + x + "\\c0 "; }
	public static String color3(String x) { return "\\c3 " + x + "\\c0 "; }
	public static String color4(String x) { return "\\c4 " + x + "\\c0 "; }
	public static String color5(String x) { return "\\c5 " + x + "\\c0 "; }
	public static String color6(String x) { return "\\c6 " + x + "\\c0 "; }
	public static String color7(String x) { return "\\c7 " + x + "\\c0 "; }
	public static String bold(String x) { return "\\b " + x + "\\b0 "; }
	public static String quoted(String x) { return "\" " + x + "\""; }
	
	public static String toView(Test t, boolean inplan)
	{
		String r = "";
		if (t instanceof BeliefTest)
			r = bold("B")+"("+toView(t.getQuery(),inplan)+")";
		else if (t instanceof GoalTest)
			r = bold("G")+"("+toView(t.getQuery(),inplan)+")";
		Test n = t.getNext();
		if (n!=null) r = r + " "+color3("&") + " "+toView(n,inplan);
		return r;
	}
	
	public static String toView(Term term, boolean inplan)
	{
		//APLFunction
		if (term instanceof APLFunction)
		{
			APLFunction function = (APLFunction)term;
			if (function.isInfix())	return 
				"("
				+ toView(function.getParams().get(0),inplan)
				+ function.getName()
				+ toView(function.getParams().get(1),inplan)
				+ ")";
			else return
				bold(function.getName())
				+ "("
				+ concat(function.getParams(),",",inplan)
				+ ")";
		}
		//APLIdent
		else if (term instanceof APLIdent)
		{
			APLIdent ident = (APLIdent)term;
			
			if (ident.quoted()) return quoted(color6(ident.getName()));
			else return color6(ident.getName());
		}
		//APLlist
		else if (term instanceof APLList)
		{
			APLList list = (APLList)term;
			if (list.isEmpty()) return "[]";
			else if (list.oneElement()) return
				"["
				+ toView(list.getHead(),inplan)
				+ "]";
			if (list.getTail() instanceof APLList)
				return
					"["
					+ toView(list.getHead(),inplan)
					+ ","
					+ toView(list.getTail(),inplan).substring(1);
			else if (list.getTail() instanceof APLVar) {
				APLVar var = (APLVar)(list.getTail());
				if (var.isBounded()) return
					"["
					+ list.getHead()
					+ ","
					+ toView(var,inplan).substring(1);
				else return 
					"["
					+ toView(list.getHead(),inplan)
					+ color1("|")
					+ toView(var,inplan)
					+ "]";
			}
			else return "[]";
		}
		//APLNum
		else if (term instanceof APLNum)
		{
			APLNum num = (APLNum)term;
			if (num.toDouble()%1==0) return num.toInt()+"";
			else return ""+num.toDouble();
		}
		else if (term instanceof APLVar)
		{
			APLVar var = (APLVar)term;
			if (var.isBounded()) {
				if (inplan) return
					"["
					+ var.getName()
					+ "/"
					+ toView(var.getSubst(),inplan)
					+ "]";
				else return	toView(var.getSubst(),inplan);
			}
			else return var.getName();
		}
		else return "*unknown term*";
	}
	
	public static String toView (Plan plan,  int tabs)
	{
		//AbstractAction
		if (plan instanceof AbstractAction)
		{
			AbstractAction p = (AbstractAction)plan;
			return toView(p.getPlan(),true);
		}
		//AdoptGoal
		else if (plan instanceof GoalAction)
		{
			GoalAction p = (GoalAction)plan;
			return
				color4(p.getAction())
				+ "("
				+ toView(p.getGoal(),true)
				+ ")";
		}
		//BeliefUpdateAction
		else if (plan instanceof BeliefUpdateAction)
		{
			BeliefUpdateAction p = (BeliefUpdateAction)plan;
			return toView(p.getPlan(),true);
		}
		//TestAction
		else if (plan instanceof TestAction)
		{
			TestAction p = (TestAction)plan;
			return toView(p.getTest(),true);
		}
		//ConditionalPlan
		else if (plan instanceof ConditionalPlan)
		{
			ConditionalPlan p = (ConditionalPlan)plan;
			if (tabs<0)	return
					color2("IF")
					+ toView(p.getCondition(),true)
					+ " "
					+ color2("THEN")
					+ toView(p.getThenPlan(),tabs)
					+ (p.getElsePlan().isEmpty() ? "" :
						color2("ELSE")
						+ toView(p.getElsePlan(),tabs)
						);
			else return
				color2("if")
				+ tab
				+ toView(p.getCondition(),true)
				+ newline
				+ tabs(tabs)
				+ color2("then")
				+ tab
				+ toView(p.getThenPlan(),tabs+1)
				+ (p.getElsePlan().isEmpty()? "" : 
					newline
					+ tabs(tabs)
					+ color2("else")
					+ tab
					+ toView(p.getElsePlan(),tabs+1)
					);
		}
		//WhilePlan
		else if (plan instanceof WhilePlan)
		{
			WhilePlan p = (WhilePlan)plan;
			if (tabs<0)	return
					color2("while")
					+ toView(p.getCondition(),true)
					+ " "
					+ color2("do")
					+ toView(p.getPlan(),tabs);
			else return
				color2("while")
				+ tab
				+ toView(p.getCondition(),true)
				+ newline
				+ tabs(tabs)
				+ color2("do")
				+ tab
				+ toView(p.getPlan(),tabs+1);
		}
		//Skip
		else if (plan instanceof Skip)
		{
			return color5("skip");
		}
		//ChunkPLan
		else if (plan instanceof ChunkPlan)
		{
			ChunkPlan p = (ChunkPlan)plan;
			if (tabs<0){
				if (p.getPlans().size()==1) return p.getPlans().get(0).toString();
				return "["+concat(p.getPlans(),"; ",true)+"]";
			}
			else {
				if (p.getPlans().size()==1) return toView(p.getPlans().get(0),tabs);
				String s = "[" + tab;
				String seperator = color1(";") + newline + tabs(tabs+1);
				for (Plan sp : p.getPlans()) s = s + toView(sp,tabs+1) + seperator;
				int l = seperator.length();
				if (s.length()>=l) s = s.substring(0,s.length()-l);	
				return s + newline + tabs(tabs) + "]";
			}
		}
		//ExternalAction
		else if (plan instanceof ExternalAction)
		{
			ExternalAction p = (ExternalAction)plan;
			return
				color4("@"+p.getEnv())
				+ "("
				+ toView(p.getAction(),true)
				+ ","
				+ toView(p.getResultVar(),true)
				+ ","
				+ p.getTimeout()
				+ ")";
		}
		else return "*unknow plan*";
	}
	
	public static String toView (Query query, boolean inplan)
	{
		//Literal
		if (query instanceof Literal)
		{
			Literal literal = (Literal)query;
			return
				(literal.getSign() ? "" : color1("not"))
				+ toView(literal.getBody(),inplan);
		}
		//Or
		else if (query instanceof OrQuery)
		{
			OrQuery orQuery = (OrQuery)query;
			return
				toView(orQuery.getLeft(),inplan)
				+ color1("or")
				+ toView(orQuery.getRight(),inplan);
		}
		//And
		else if (query instanceof AndQuery)
		{
			AndQuery andQuery = (AndQuery)query;
			String a = toView(andQuery.getLeft(),inplan);
			String b = toView(andQuery.getRight(),inplan);
			if (andQuery.getLeft() instanceof OrQuery) a = "("+a+")";
			if (andQuery.getRight() instanceof OrQuery) b = "("+b+")";
				return a + color1("and") + b;
		}
		else return "*unknown query*";
	}
	
	public static String toView (Goal goal, boolean inplan)
	{
		if (goal.size()<=0) return "";
		String r = "";
		String s = color1("and");
		for (Literal l : goal) r = r + toView(l,inplan) + s;
		if (r.length()>=s.length()) r = r.substring(0,r.length()-s.length());	
		return r;
	}
	
	public static String toView (PlanSeq plans,  int tabs)
	{
		if (tabs<0) {
			if (plans.getPlans().size()==1) return plans.getPlans().get(0).toRTF(true);
			String s = "";
			String separator = color1(";");
			for (Plan p : plans) s = s + toView(p,0) + separator;
			int l = separator.length();
			if (s.length()>=l) s = s.substring(0,s.length()-l);	
			return lbrace + s + rbrace;
		}
		else {
			String s = "";
			String seperator = color1(";") + newline + tabs(tabs+1);
			for (Plan p : plans) s = s + toView(p,tabs+1)+seperator;
			int l = seperator.length();
			if (s.length()>=l) s = s.substring(0,s.length()-l);	
			return	rbrace + tab + s + newline + tabs(tabs) + rbrace;
		}
	}
	
	public static String toView(PCrule rule)
	{
		return 
			toView(rule.getHead(),false)
			+ color1(" <-")
			+ toView(rule.getGuard(),false)
			+ color1(" |")
			+ (rule.getBody().oneliner() ? 
				" " + toView(rule.getBody(),-1) :
				" " + newline + tab + toView(rule.getBody(),1)
				);
	}
	
	public static String toView(PRrule rule)
	{
		return 
			toView(rule.getHead(),0)
			+ color1(" <-")
			+ toView(rule.getGuard(),false)
			+ color1(" |")
			+ (rule.getBody().oneliner()
				? " " + toView(rule.getBody(),-1)
				: " " + newline + tab + toView(rule.getBody(),1)
				);
	}
	
	public static String toView(PGrule rule)
	{
		return
			(	(rule.getHead() instanceof True)
			?	""
			:	toView(rule.getHead(),false) + " "
			)
			+	color1("<- ")
			+	toView(rule.getGuard(),false)
			+	color1(" |")
			+	(	rule.getBody().oneliner()
				?	toView(rule.getBody(),-1)
				:	newline + tab + toView(rule.getBody(),1)
				)
			;
	}
	
	public static String toView(Beliefbase bb)
	{
		return "";
	}
	
	
	public static String toView(BeliefUpdate b)
	{
		String r = "";
		for (Literal l : b.getPost()) r = r + toView(l,false) + ", ";
		if (r.length()>=2) r = r.substring(0,r.length()-2);	
		
		return
			lbrace
			+( b.getPre() instanceof True
				? "" 
				: toView(b.getPre(),false)
				)
			+ rbrace + " "
			+ toView(b.getAct(),false)
			+ " " + lbrace + " " +  r + " " + rbrace;
	}

	public static String tabs(int t)
	{
		String s = "";
		while (t>0) {s = s + tab; t--;}
		return s;
	}
	
	public static String concat(Iterable<?> list, String s, boolean inplan)
	{
		String r = "";
		for (Object a : list)  {
			if (a instanceof Plan) r = r + toView((Plan)a,0) + s;
			else if (a instanceof PlanSeq) r = r + toView((PlanSeq)a,0) + s;
			else if (a instanceof Term) r = r + toView((Term)a,inplan) + s;
			else return r;
		}
		if (r.length()>=s.length()) r = r.substring(0,r.length()-s.length());	
		return r;
	}
	
	

}
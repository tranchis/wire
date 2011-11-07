package apapl.data;

import apapl.program.Base;

import java.math.BigDecimal;
import java.util.ArrayList;
import apapl.SubstList;

/**
 * A function term or a logical fact. Although a logical fact is not a term, syntactically
 * they have the same form. That explains why in this case no distinction is made between 
 * a predicate and term.
 */
public class APLFunction extends Fact
{
	private boolean infix = false;
	private String name;
	ArrayList<Term> params;
	
	/**
	 * Construct a new atomic formula.
	 * 
	 * @param name Name of this atom.
	 * @param params ArrayList containing parameters for this atom.
	 */
	public APLFunction(String name, ArrayList<Term> params)
	{
		this.name = name;
		this.params = params;

	}

	/**
	 * Construct a new atomic formula.
	 * @param name Name of this atom.
	 * @param params ArrayList containing parameters for this atom.
	 */
	public APLFunction(String name, Term... params)
	{
		this.name = name;
		
		this.params = new ArrayList<Term>();
		for (Term t : params) this.params.add(t);
	}
	
	/**
	 * Constructs an infix atomic formula.
	 * @param left Left term.
	 * @param name Infix name of this atom.
	 * @param right Right term.
	 */
	public APLFunction(Term left, String name, Term right)
	{
		infix = true;
		this.name = name;
		params = new ArrayList<Term>();
		params.add(left);
		params.add(right);
		//evaluateArguments();
	}
	
	/**
	 * @return The name of this atom.
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * @return The list of parameters of this atom.
	 */
	public ArrayList<Term> getParams()
	{
		return params;
	}
	
	
	/**
	 * Evaluates all infix functions and replaces bounded variables with their substitution in the parameter list.
	 */
	public void evaluateArguments()
	{		
		for (int i = 0;  i < params.size(); i++) {
			Term t = params.get(i);
			if (t instanceof APLVar) 
				if (((APLVar)t).isBounded()) params.set(i, ((APLVar)t).getSubst());			
			if (t instanceof APLFunction) 
				params.set(i, ((APLFunction)t).evaluate());
		}
	}
	
	/**
	 * Evaluates this function if possible.
	 * @return the evaluation result.
	 */
	public Term evaluate()
	{
		if (isInfix()) {
			Term l = params.get(0);
			Term r = params.get(1);
			if (l instanceof APLVar) if (((APLVar)l).isBounded()) l = ((APLVar)l).getSubst();
			if (r instanceof APLVar) if (((APLVar)r).isBounded()) r = ((APLVar)r).getSubst();
			if (l instanceof APLFunction) l = ((APLFunction)l).evaluate();
			if (r instanceof APLFunction) r = ((APLFunction)r).evaluate();
			Term e = eval(l,name,r);
			if (e!=null) return e;
		}
		return this;
	}

	
	/**
	 * Applies evaluation.
	 * @param left The left <code>Term</code> for this function.
	 * @param name The function name.
	 * @param right The right <code>Term</code> for this function.
	 * @return The result of this function applied to the parameters.
	 */
	private static Term eval(Term left, String name, Term right)
	{
		if (left instanceof APLNum && right instanceof APLNum) {
			BigDecimal l = ((APLNum)left).getVal();
			BigDecimal r = ((APLNum)right).getVal();
			
			if (name.equals("+")) return new APLNum(l.add(r));
			if (name.equals("-")) return new APLNum(l.subtract(r));
			if (name.equals("*")) return new APLNum(l.multiply(r));
			if (name.equals("/")) return new APLNum(l.divide(r));
			if (name.equals("%")) return new APLNum(l.divideAndRemainder(r)[1]);
		}
		
		return null;

	}
	
	/**
	 * @return true if this is an infix function, false otherwise.
	 */
	public boolean isInfix()
	{
		return infix;
	}
	
	/**
	 * Applies substitution to this function.
	 * @param theta Substitution to be applied.
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
		for (Term t : params)
			t.applySubstitution(theta);
		evaluateArguments();
	}
	
	/**
	 * @return All variables contained in this atom.
	 */
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = new ArrayList<String>();
		for (Term t : params) vars.addAll(t.getVariables());
		return vars;
	}
	
	/**
	 * Clones this object.
	 */
	public APLFunction clone()
	{
		if (infix) return new APLFunction(params.get(0).clone(),name,params.get(1).clone());
		else {
			ArrayList<Term> paramcopy = new ArrayList<Term>();
			for (Term t : params) 
				paramcopy.add(t.clone());
			return new APLFunction(new String(name),paramcopy);
		}
	}
	
	/**
	 * Returns the priority of the arithmetic operations.
	 * @return priority
	 */
	public int infixLevel()
	{
		if (!infix) return 0;
		else if (name.equals("+")) return 3;
		else if (name.equals("-")) return 3;
		else if (name.equals("*")) return 2;
		else if (name.equals("/")) return 2;
		else if (name.equals("<")) return 1;
		else if (name.equals(">")) return 1;
		else if (name.equals("=")) return 1;
		else if (name.equals("=<")) return 1;
		else if (name.equals(">=")) return 1;
		else return 0;
	}
	
	/**
	 * Gives a string representation of this object
	 */
	public String toString(boolean inplan)
	{
		
	    if (infix) {
	     // Infix Notation
			Term left = params.get(0);
			Term right = params.get(1);
			String leftString = left.toString(inplan);
			String rightString = right.toString(inplan);
			int leftLevel = 0;
			int rightLevel = 0;
			int ownLevel = infixLevel();
			if (left instanceof APLFunction) leftLevel = ((APLFunction)left).infixLevel();
			if (right instanceof APLFunction) rightLevel = ((APLFunction)right).infixLevel();
			
			if (leftLevel>ownLevel) leftString = "(" + leftString + ")";
			if (rightLevel>ownLevel) rightString = "(" + rightString + ")";
			
			return leftString + name + rightString;
			//return  "("+params.get(0).toString(inplan)+name+params.get(1).toString(inplan)+")";
		} else
		{
            if (params.size() > 0) {
                String r = "";
                for (Term t : params)
                    r = r + t.toString(inplan) + ", ";
                if (r.length() >= 1)
                    r = r.substring(0, r.length() - 2);

                return name + "(" + r + ")";
            } else {
                // Function has no parameters
                return name;
            }
        }
		

	}
	
	public String toRTF(boolean inplan)
	{
		if (params.size()==1) if (params.get(0).toString().equals("arityequaltozero")) return "\\b "+name+"\\b0 ()";
		
		if (infix) return  "("+params.get(0).toRTF(inplan)+name+params.get(1).toRTF(inplan)+")";
		else return "\\b "+name+"\\b0 ("+Base.rtfconcatWith(params,",",inplan)+")";
	}
	
	/**
	 * Converts this string to a format s.t. it can be queried in JIProlog
	 * For example, JIProlog does not accept the query f(a,b) but
	 * does accept X=a, Y=b, f(X,Y).
	 * 
	 * @return the querable string
	 */
	public String toQueryString()
	{
		// QAZXSW is a 'random' and 'unique' string
		
		if (infix) return toString();
		
		String s=name+"(";
		for (int i=1;i<=params.size();i++) {
			s=s+"QAZXSW"+i+",";
		}
				
		s = s.substring(0,s.length()-1)+")";
		
		int i=1;
		for (Term t : params) {
			s=s+",QAZXSW"+i+" = "+t;
			//s=s+",is(POEP"+i+","+t+")";
			i++;
		}
				
		return s;		
	}
	
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		for (Term t : params) t.freshVars(unfresh,own,changes);
	}
	
	public boolean equals(Term other)
	{
		if (other instanceof APLFunction)
		{
			APLFunction term = (APLFunction)other;
			if (!name.equals(term.getName())) return false;
			if (params.size()!=term.getParams().size()) return false;
			for (int i=0; i<params.size(); i++)
				if (!params.get(i).equals(term.getParams().get(i))) return false;
			return true;
		}
		else if (other instanceof APLVar)
		{
			APLVar term = (APLVar)other;
			if (!term.isBounded()) return false;
			else return equals(term.getSubst());
		}
		else return false;
	}
}
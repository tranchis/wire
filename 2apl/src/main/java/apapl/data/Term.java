package apapl.data;

import apapl.Substitutable;
import apapl.UnboundedVarException;
import apapl.SubstList;
import java.util.ArrayList;

/**
 * A logical term. The root of all term types.
 */
public abstract class Term implements Substitutable
{
	/**
	 * Applies a substitution to this term.
	 * 
	 * @param theta the substituion to apply.
	 */
	public abstract void applySubstitution(SubstList<Term> theta) ;
	
	/**
	 * Clones this term.
	 * @return the clone
	 */
	public abstract Term clone();
	
	public abstract String toRTF(boolean inplan) ;
	public String toRTF() {return toRTF(false);}
	
	/**
	 * Turn all variables of this term into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this term. 
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public abstract void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes) ;
	
	/**
	 * Returns all variables that occur in this term.
	 * 
	 * @return a list of variables
	 */
	public abstract ArrayList<String> getVariables();
	
	public abstract String toString(boolean inplan);
	
	public String toString() {return toString(false);}
	public abstract boolean equals(Term other);

	/**
	 * Changes all bounded instances of <code>Var</code> to the type of the substituted term. 
	 * Note that a bounded variable is normally still of type <code>Var</code>.
	 */
	public static Term unvar(Term x) throws UnboundedVarException
	{
		if (x instanceof APLVar) {
			APLVar v = (APLVar)x;
			if (v.isBounded()) return unvar(v.getSubst());
			else throw new UnboundedVarException(""+x);
		}
		else if (x instanceof APLList) {
			APLList l = (APLList)x;
			if (l.isEmpty()) return l;
			Term head = unvar(l.getHead());
			Term tail = unvar(l.getTail());
			if (!(tail instanceof APLListVar)) throw new UnboundedVarException(""+x);
			return new APLList(true,head,(APLListVar)tail);
		}
		else if (x instanceof APLFunction) {
			APLFunction a = (APLFunction)x;
			ArrayList<Term> p = a.getParams();
			for (int i=0; i<p.size(); i++) p.set(i,unvar(p.get(i)));
			return new APLFunction(a.getName(),p);
		}
		else return x;
	}

}


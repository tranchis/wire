package apapl.data;

import apapl.UnboundedVarException;
import apapl.Unifier;
import apapl.SubstList;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A literal is a signed atomic formula. In this case it is either a {@link APLFunction} 
 * or {@link APLIdent}. A literal can contain variables and can be queried to the 
 * Prolog engine.
 */
public class Literal extends Query
{
	// the sign of this query, false == negation
	private boolean sign;
	private Term body;  //should be either APLFunction or APLIdent
	
	/**
	 * Constructs a literal.
	 * 
	 * @param body the term
	 * @param sign the sign (false == negation)
	 */
	public Literal(Term body, boolean sign)
	{
		this.sign = sign;
		this.body = body;
	}
	
	/**
	 * Converts this literal to a format Prolog can handle.
	 * 
	 * @return the Prolog format string
	 */
	public String toPrologString()
	{
		if (body instanceof APLFunction) {
			((APLFunction)body).evaluate();
			return (sign?"":"not ") + ((APLFunction)body).toString();
		}
		else return (sign?"":"not ") + body.toString();
	}
	
	/**
	 * Returns the sign of this literal.
	 * 
	 * @return true if possitive, false if negated
	 */
	public boolean getSign()
	{
		return sign;
	}
	
	/**
	 * Returns the term without the sign.
	 * 
	 * @return the term without the sign
	 */
	public Term getBody()
	{
		return body;
	}

	/**
	 * Applies a substitution to this literal.
	 * 
	 * @param theta the substitution to apply
	 */
	public void applySubstitution(SubstList<Term> theta)
	{
		body.applySubstitution(theta);
	}
	
	/**
	 * Clones this literal.
	 * 
	 * @return the clone
	 */
	public Literal clone()
	{
		return new Literal(body.clone(),sign);
	}
	
	/**
	 * Returns the variables that occur in this literal.
	 * 
	 * @return the list of variables
	 */
	public ArrayList<String> getVariables()
	{
		return body.getVariables();
	}
	
	/**
	 * Evaluates this literal. Evaluating a literal means to evaluate to the
	 * value of the expression (if this literal is an expression that can be
	 * evaluated).
	 */
	public void evaluate()
	{
		if (body instanceof APLFunction) ((APLFunction)body).evaluateArguments();
	}
	
	public String toRTF(boolean inplan)
	{
		return (sign?"":"\\cf1 not \\cf0 ")+body.toRTF(inplan);
	}
	
		
	public String toString(boolean inplan)
	{
		return (sign?"":"not ")+body.toString(inplan);
	}
	
	/**
	 * Turn all variables of this literal into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this literal.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		body.freshVars(unfresh,own,changes);
	}
	
	/**
	 * Converts this literal to a list of literals.
	 * 
	 * @return a list with this literal as single element 
	 */
	public LinkedList<Literal> toLiterals()
	{
		LinkedList<Literal> literals = new LinkedList<Literal>();
		literals.add(this);
		return literals;
	}
	
	/**
	 * @return 1
	 */
	public int size()
	{
		return 1;
	}
	
	/**
	 * Tests whether this literals is negated or not.
	 * 
	 * @return true if negated, false otherwise
	 */
	public boolean containsNots()
	{
		return !sign;
	}
	
	/**
	 * Tests whether this literal is equal to another literal.
	 * 
	 * @param l the literal to compare with
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Literal l)
	{
		if (sign==l.getSign()) 
			//return body.equals(l.getBody());
			return body.toString().equals(l.getBody().toString());
		else return false;
	}
	
	/**
	 * Tests whether this literal contains disjunctions. A literal cannot 
	 * contain disjunctions.
	 * 
	 * @return false
	 */
	public boolean containsDisjunct()
	{
		return false;
	}
	
	public void unvar() throws UnboundedVarException
	{
		body = Term.unvar(body);
	}
}
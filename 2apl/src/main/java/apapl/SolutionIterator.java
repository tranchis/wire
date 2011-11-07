package apapl;

import apapl.data.Term;
import com.ugos.JIProlog.engine.JIPRuntimeException;
import apapl.data.*;
import com.ugos.JIProlog.engine.*;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Implements an iterator over all solutions of a query performed on the Prolog engine.
 * More solutions (if any) are calculated at runtime only when the next solution is 
 * requested.
 */
public class SolutionIterator implements Iterable<SubstList<Term>>, Iterator<SubstList<Term>>
{
	private JIPQuery jipQuery = null;
	private Prolog prolog;
	
	private SubstList<Term> nextSol = null;
	
	/**
	 * Constructs a SolutionIterator.
	 */
	public SolutionIterator()
	{
	}
	
	/**
	 * Constructs a SolutionIterator. Calculates the first solution when created, by
	 * firing the query on the prolog engine provided.
	 * 
	 * @param prolog the prolog engine to perform the query on
	 * @param query the query to perform
	 */
	public SolutionIterator(Prolog prolog, Query query)
	{
		this.prolog = prolog;
		try {
			ArrayList<SubstList<Term>> solutions = new ArrayList<SubstList<Term>>();
			jipQuery = prolog.getJIP().openSynchronousQuery(prolog.getParser().parseTerm(query.toPrologString()));
		}
		catch (JIPParameterTypeException e) {
			throw e;
		}
		calculateNext();
	}
	
	/**
	 * Calculates and returns the next solution. Should only be called
	 * if {@link apapl.SolutionIterator#hasNext} returns true.
	 * 
	 * @return the next solution,
	 *         null if there is no next solution
	 */
	public SubstList<Term> next()
	{
		SubstList<Term> r = nextSol;
		calculateNext();
		return r;
	}
	
	/**
	 * Calculates the next solution.
	 */
	private void calculateNext()
	{
		if (prolog==null) {
			nextSol = null;
		}
		else if (jipQuery==null) {
			nextSol = null;
		}
		else if (!jipQuery.hasMoreChoicePoints()) {
			nextSol = null;
		}
		else {
			JIPTerm sol = jipQuery.nextSolution();
			if (sol==null) nextSol = null;
			else nextSol = Prolog.getSubstitutions(sol);
		}
	}
	
	/**
	 * Returns whether next solution exists.
	 * 
	 * @return true if next solution exists, false otherwise
	 */
	public boolean hasNext()
	{
		if (jipQuery==null) return false;
		else return (nextSol!=null);
	}
	
	/**
	 * DO NOT USE! Implementation enforced by {@link java.util.Iterator} interface.
	 */
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return the iterator used to iterate over the solutions.
	 * 
	 * @return the iterator (this object)
	 */
	public Iterator<SubstList<Term>> iterator()
	{
		return this;
	}

}

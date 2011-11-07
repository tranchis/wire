package apapl;


import com.ugos.JIProlog.engine.JIPRuntimeException;
import apapl.data.*;
import apapl.program.Beliefbase;
import apapl.program.Base;
import com.ugos.JIProlog.engine.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * Convenience class that provides the interface to the JIProlog engine.
 */
public class Prolog
{
	boolean recordview = true;
	
	JIPEngine jip = new JIPEngine();
	JIPTermParser tp = jip.getTermParser();
	ArrayList<String> view = new ArrayList<String>();
	
	/**
	 * Constructs a Prolog engine.
	 */
	public Prolog()
	{
	}

	/**
	 * Returns the parser that is used by JIProlog.
	 * 
	 * @return JIProlog parser
	 */
	public JIPTermParser getParser()
	{
		return tp;
	}
	
	/**
	 * Returns the JIProlog engine. Should only be used by {@link apapl.SolutionIterator}
	 * @return JIProlog engine
	 */
	public JIPEngine getJIP()
	{
	  return jip;
	}
	
	/**
	 * Removes a predicate from the prolog engine. 
	 * 
	 * @param predicate to remove
	 */
	public void removePredicate(String pred)
	{
		JIPTerm t = toJIP(pred);
		String s = t.toStringq(jip);
		
		jip.retract(t);
		view.remove(s+".");
	}
	
	/**
	 * Adds a predicate from the prolog engine. 
	 * 
	 * @param predicate to add
	 */
	public void addPredicate(String pred)
	{	
		JIPTerm t = tp.parseTerm(pred);
		String s = t.toStringq(jip) + ".";

		if (!view.contains(s)) {
			jip.assertz(t);
			if (isPrologRule(t)) view.add(pred);
			else view.add(s);
		}
	}
	
	/**
	 * Performs a query on the prolog engine and returns the solution (could be empty).
	 * 
	 * @param q the query to perform
	 * @param killPrologWhenReady deinstantiate the prolog engine when ready
	 * @return the solution
	 */
	public SolutionIterator doTest(Query q)
	{
		return new SolutionIterator(this, q);
	}
	
	/**
	 * Performs a query on the prolog engine and returns the solution.
	 * 
	 * @param query the qeury to perform
	 * @return the solution
	 * @deprecated {@link apapl.Prolog#doTest} should be used instead. This is a more
	 *             efficient implementation.
	 */
	public ArrayList<SubstList<Term>> doQueryAll(Query query)
	{
		try {
				ArrayList<SubstList<Term>> solutions = new ArrayList<SubstList<Term>>();
				JIPQuery q = jip.openSynchronousQuery(tp.parseTerm(query.toPrologString()));
				JIPTerm sol = q.nextSolution();
				while (sol!=null) {
					solutions.add(getSubstitutions(sol));
					sol = q.nextSolution();
				}
				return solutions;
			}
			catch (JIPParameterTypeException e) {
				throw e;
			}
	}
	
	/**
	 * Converts a JIProlog solution to a SubstList<Term>. Should only be used by
	 * {@link apapl.SolutionIterator}.
	 * 
	 * @param sol JIProlog substitution
	 * @return converted substitution
	 */
	public static SubstList<Term> getSubstitutions(JIPTerm sol)
	{
		SubstList<Term> theta = new SubstList<Term>();
		Hashtable t = sol.getVariablesTable();
		Enumeration<String> e = t.keys();
		while (e.hasMoreElements())	{
			String k = e.nextElement();
			JIPTerm j = (JIPTerm)(t.get(k));
			Term a = fromJIP(j);
			if (!(a instanceof APLVar)) theta.put(k,a);
		}
		return theta;
	}
	
	/**
	 * Converts a query to an <code>JIPTerm</code> so it can b queried to the <code>JIPEngine</code>.
	 * @param q The query to be converted.
	 * @return The <code>JIPTerm</code> representing the input q.
	 */
	private JIPTerm toJIP(Query q)
	{
		String s = q.toPrologString();
		JIPTerm r = tp.parseTerm(s);
		return r;
	}
	
	/**
	 * Converts a Term to an <code>JIPTerm</code> so it can b queried to the <code>JIPEngine</code>.
	 * @param t The term to be converted.
	 * @return The <code>JIPTerm</code> representing the input t.
	 */
	private JIPTerm toJIP(Term t)
	{
		return tp.parseTerm(t+".");
	}

	/**
	 * Converts a String to an <code>JIPTerm</code> so it can b queried to the <code>JIPEngine</code>.
	 * @param s The string to be converted.
	 * @return The <code>JIPTerm</code> representing the input t.
	 */
	private JIPTerm toJIP(String s)
	{
		JIPTerm r = tp.parseTerm(s);
		return r;
	}
	
	/**
	 * Loads the additional JIProlog libraries. These libraries provide extra
	 * functionality which is not always needed. For the sake of efficiency these
	 * libraries are not loaded by default. for instance, when performing a simple query,
	 * these libraries are not needed.  
	 */
	public void loadLibs() 
	{
		File dir = new File( "lib"+File.separatorChar+"jiprolog" );
		if (dir.isDirectory()) {
		String[] files = dir.list();
    		for( int i = 0; i < files.length; i++ ) {
    			String fileName = files[ i ];
    			if( fileName.startsWith( "jipx" ) && fileName.endsWith( ".jar" ) ) {
    				try {
    					 jip.loadLibrary( dir.getAbsolutePath()
    			                                + File.separatorChar + fileName );
    				}
    				catch( Exception e ) {
    					System.err.println( "Unable to load JIProlog library \""+ dir.getAbsolutePath()+"\".");
    					e.printStackTrace( System.err );
    				}
    			}
    		}
		}
	}
	
	/**
	 * Checks whether the formula is an implication rule or not.
	 * 
	 * @param j the formula to check
	 * @return true if it is an implication, false otherwise
	 */
	private boolean isPrologRule(JIPTerm j)
	{
		if (!(j instanceof JIPFunctor)) return false;
		if (((JIPFunctor)j).getName().equals(":-")) return true;
		else return false;
	}
	
	/**
	 * Returns the beliefs as a list of strings.
	 *  
	 * @return string representation of the belief base
	 */
	public ArrayList<String> getBeliefsAsStrings()
	{
		return view;
	}
	
	public String toString()
	{
			if (view.size()>0)
			return Base.concatWith(view,"\n")+"\n\n";
			else return "";
	}
	
	public String toRTF()
	{
		String r = "";
		boolean quote = false;
		for (String s : view) {
			int a = 0;
			for (int i=0; i<s.length(); i++) {
				char c = s.charAt(i);
				if (c=='(') {
					r = r + RTF.bold(s.substring(a,i)) + c;
					a = i+1;
				}
				else if (c==':') {
					if (i<s.length()-1) if (s.charAt(i+1)=='-') {
						if (i>a) r = r + s.substring(a,i);
						r = r + RTF.color1(":- ");
						a = i + 2;
					}
				}
				else if (c=='\"') {
					if (i>a) r = r + s.substring(a,i);
					r = r + (quote?"\"\\cf0 ":"\\cf6 \"");
					a = i+1;
					quote = !quote;
				}
				else if (!isLetterOrDigit(c)) {
					if (i>a) {
						r = r + s.substring(a,i);
						a = i;
					}
				}
			}
			r = r + s.substring(a+1,s.length())+"."+RTF.newline;
		}
		return r;
	}
	
	/**
	 * Checks whether a character is a letter or a digit.
	 * 
	 * @param c the character
	 * @return true if it is a letter or a digit, false otherwise
	 */
	private boolean isLetterOrDigit(char c)
	{
		return	c>='a' && c<='z'
		||		c>='A' && c<='Z'
		||		c>='0' && c<='9';
	}
	
	/**
	 * Convert a JIProlog Term to a 2APL term.
	 * 
	 * @param t the JIProlog term
	 * @return 2APL term
	 */
	private static Term fromJIP(JIPTerm t)
	{
		if (t instanceof JIPVariable) {
			JIPVariable v = (JIPVariable)t;
			if (v.isBounded()) return fromJIP(v.getValue());
			else return new APLVar(v.getName());
		}
		else if (t instanceof JIPList) {
			JIPList l = (JIPList)t;
			JIPTerm jiphead = l.getHead();
			JIPTerm jiptail = l.getTail();
			
			if (jiphead==null) return new APLList();
			else if (jiptail==null) {
				Term head = fromJIP(jiphead);
				return new APLList(true,head,new APLList());
			}
			else {
				Term head = fromJIP(jiphead);
				Term tail = fromJIP(jiptail);
				if (tail instanceof APLList) return new APLList(true,head,(APLList)tail);
				else if (tail instanceof APLVar) return new APLList(true,head,(APLVar)tail);
			}
		}
		else if (t instanceof JIPFunctor) {
			JIPFunctor f = (JIPFunctor)t;
			
			// Negative numbers are in JIProlog represented using the unary negation operator "-". E.g. x(-3) will become x(-(3)).  
			// Within 2APL, we will use more compact representation in which negative numbers are stored directly as APLNum atom.
			if (f.getName().equals("-") && 
			    f.getArity() == 1       && 
			    f.getParams().getHead() instanceof JIPNumber  )
			{
				JIPNumber n = (JIPNumber)f.getParams().getHead();
				return new APLNum( - n.getValue());
			}
			// Normal functor
			else
			{
				ArrayList<Term> p = new ArrayList<Term>();
				fromJIPCons(f.getParams(),p);
				return new APLFunction(f.getName(),p);
			}
		}
		else if (t instanceof JIPAtom) {
			JIPAtom a = (JIPAtom)t;
			return new APLIdent(a.getName());
		}
		else if (t instanceof JIPNumber) {
			JIPNumber n = (JIPNumber)t;
			return new APLNum(n.getValue());
		}
		
		// Should not return null, case ommitted?
		return null;
	}
	
	/**
	 * Converts a JIPCons (JIProlog specific, a list of terms) to a list of 2APL terms.
	 * This method uses a call by reference mechanism for the list of 2APL terms.
	 * 
	 * @param c list of JIProlog terms
	 * @param p list of 2APL terms (the result of the conversion)
	 */
	private static void fromJIPCons(JIPCons c, ArrayList<Term> p)
	{
		p.add(fromJIP(c.getHead()));
		JIPTerm t = c.getTail();
		if (t instanceof JIPCons) fromJIPCons((JIPCons)t,p);
		else if (t!=null) p.add(fromJIP(t));
	}

	/**
	 * Release the resources taken by the Prolog engine.
	 */
	protected void finalize()
	{
		jip.releaseAllResources();
	}

	public void addFromFile(String absolutePath) throws IOException {

			jip.consultFile(absolutePath);
			
	}
}

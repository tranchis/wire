package apapl.program;

import apapl.Logger;
import apapl.SolutionIterator;
import apapl.Unifier;
import apapl.Prolog;
import apapl.APLModule;
import apapl.Parser;
import apapl.data.*;
import apapl.parser.ParseException;
import apapl.parser.Parser2apl;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import apapl.SubstList;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.StringBufferInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * The base in which the beliefs of a 2APL module are stored. The beliefs are stored as
 * a Prolog program that is interpreted by a {@link apapl.Prolog} engine.
 */
public class Beliefbase extends Base
{
	private Prolog belief;
	
	private Logger logger = null;
	
	/** @deprecated */
	private boolean showrules = true;
	
	/**
	 * Constructs a belief base.
	 */
	public Beliefbase()
	{		
		belief = new Prolog( );
		belief.loadLibs();
	}	
	
	/**
	 * Performs a query on the belief base and returns all solutions.
	 * 
	 * @param query the query to perform
	 * @return the solutions of this query
	 */
	public SolutionIterator doTest(Query query)
	{
		if( logger != null)
			logger.beliefQuery(query.toString(),"doTest");
		
		return belief.doTest(query);
	}
	
	/**
	 * Performs a query in the beliefbase. Returns only one of
	 * more possible solutions. If the query succeeds 
	 * with a substitution, this substitution is added to the input
	 * parameter theta.
	 * 
	 * @param query the query
	 * @param theta the substitution 
	 * @return true if the query is successful, false otherwise.
	 */
	public boolean doQuery(Query query, SubstList<Term> theta)
	{
		if( logger != null)
			logger.beliefQuery(query.toString(),"doQuery1");

		SubstList<Term> solution = doQueryOne(query);
		if (solution!=null) {
			theta.putAll(solution);
			return true;
		}
		else return false;
	}
	
	/**
	 * Performs a query on the belief base. Returns only one of more
	 * possible solutions if the query succeeds, null if the query does not 
	 * succeed.
	 * 
	 * @param query the query to be performed
	 * @return the list of substitutions
	 */
	public SubstList<Term> doQueryOne(Query query)
	{
		if( logger != null)
			logger.beliefQuery(query.toString(),"doQueryOne");

		query.evaluate();
		if (query instanceof True) {
			return new SubstList<Term>();
		}
		else {
			ArrayList<SubstList<Term>> solutions = belief.doQueryAll(query);
			if (solutions.size()<1) return null;
			else return solutions.get(0);
		}
	}
	
	/**
	 * Performs a query on the belief base. Returns all possible solutions
	 * of this query. Each solution is a possible substitution.
	 * 
	 * @param query the query to be performed
	 * @return the list of substitutions
	 */
	public ArrayList<SubstList<Term>> doQuery(Query query)
	{
		if( logger != null)
			logger.beliefQuery(query.toString(),"doQuery2");

		ArrayList<SubstList<Term>> solutions;
		query.evaluate();
		if (query instanceof True) {
			solutions = new ArrayList<SubstList<Term>>();
			solutions.add(new SubstList<Term>());
		}
		else solutions = belief.doQueryAll(query);
		return solutions;
	}
	
	/**
	 * Tests a goal on the beliefbase. Returns whether if the goal
	 * can be derived from the module's beliefs. 
	 * 
	 * @param g the goal to be tested on the beliefbase
	 * @param theta the resulting substitution
	 * @return true if the goalquery is succesfull, false otherwise
	 */
	public boolean doGoalQuery(Goal g, SubstList<Term> theta)
	{
		if( logger != null)
			logger.beliefQuery(g.toString(),"doGoalQuery");

		for (Literal l : g) {
			boolean b = doQuery(l,theta);
			if (!b) return false;
		}
		return true;
	}
	
	/**
	 * Checks whether the number of opening parentheses in the string 
	 * equals the number of closing parentheses. Used to check whether
	 * a string preceding a dot is a full term, e.g. in a(b,1.0). only
	 * the string preceded by the second dot is a full term.
	 *  
	 * @return true if the number of opening parentheses equals the number
	 * of closing parentheses, false otherwise.
	 */
	private boolean isTermSeparatingDot(String s)
	{
		int t = 0;
		byte[] bytes = s.getBytes();
		
		for (int i=0; i<bytes.length; i++)
		{
			if (bytes[i]=='(') t++;
			if (bytes[i]==')') t--;
		}
		
		return t==0;
	}
	

	/**
	 * Asserts a belief to the belief base. Asserting a positive literal
	 * is to add it to the belief base, a negated one is to remove it.
	 * 
	 * @param literal the literal to be asserted
	 */	
	public void assertBelief(Literal literal) 
	{
		if( logger != null)
			logger.beliefUpdate(literal.toString(),"assertBelief1");

		try {
			Term b = literal.getBody();
			if (b instanceof APLFunction) ((APLFunction)b).evaluateArguments();
			if (literal.getSign()) belief.addPredicate(b.toString());
			else belief.removePredicate(b.toString());
		}
		catch (Exception e) { }
	}
	
	/**
	 * Splits a string corresponding to a set of beliefs each separated by a dot 
	 * and adds each of them to the belief base.
	 * 
	 * @param b the string corresponding to a list of beliefs.
	 */
	public void  assertBelief(String b) throws ParseException, IOException
	{
		if( logger != null)
			logger.beliefUpdate(b,"assertBelief2");

		for (String s : separateBelief(b)) belief.addPredicate(s);
	}
	
	/**
	 * Converts a string consisting of multiple beliefs (either literals or rules) 
	 * each separated by a dot into a list of strings each corresponding to a 
	 * single belief.
	 * 
	 * @param b the string respresenting the beliefs
	 * @return the list of belief strings
	 */	
	private ArrayList<String> separateBelief(String b)
	{
		ArrayList<String> bs = new ArrayList<String>();
		String t = b;
		int i = t.indexOf(".");
		while (i>0)	{
			String s = t.substring(0,i);
			if (isTermSeparatingDot(s))	{
				s = s.trim();
				while (s.endsWith("\n")) s = s.substring(0,s.length()-1);
				while (s.startsWith("\n")) s = s.substring(1);
				s = s + ".";
				bs.add(s);
				if (t.length()>i) t = t.substring(i+1); else t = "";
				i = t.indexOf(".");
			}
			else i = t.indexOf(".",i+1);
		}
		return bs;
	}

			
	/**
	 * Converts this object to a <code>String</code> representation.
	 * @return The <code>String</code> representation of this object.
	 */
	public String toString()
	{
		return belief.toString();
	}
	
	/**
	 * Converts this belief base to a RTF representation.
	 * 
	 * @return the RTF string representation
	 */
	public String toRTF()
	{
		return belief.toRTF();
	}
	
	/**
	 * Returns the Prolog engine in which the beliefs are stored as a Prolog program.
	 * 
	 * @return the Prolog engine
	 */
	public Prolog getBelief()
	{
		return belief;
	}
	
	/**
	 * Clones the beliefbase
	 */
	
	public Beliefbase clone()
	{
		Beliefbase cloned = new Beliefbase();
		
		cloned.setLogger(logger);
		
		for (String b :  belief.getBeliefsAsStrings())
			cloned.getBelief().addPredicate(b);
		
		return cloned;		
	}

	/**
	 * Adds beliefs from a prolog-file. Either as shadow-beliefs (not rendered by the GUI) or as visible ones.
	 * 
	 * @param beliefsFile
	 * @param shadow
	 * @throws IOException
	 */
	public void addFromFile(File beliefsFile, boolean shadow) throws IOException {

		if( shadow == true ) {
			
			belief.addFromFile(beliefsFile.getAbsolutePath());
		
		}
		else {
			
			BufferedReader in = new BufferedReader(new FileReader(beliefsFile));
			String belief = "";
			String s = in.readLine();
			while (s != null) {
				belief = belief + s+"\n";
				s = in.readLine();
			}
			try {
				assertBelief(Parser.stripComment(belief));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			in.close();

		}
		
		
	
	}
	
	/**
	 * Returns a list of all beliefs. Does not include shadow-beliefs.
	 * @return
	 */
	public Collection<String> getBeliefs() {
	
		return belief.getBeliefsAsStrings();
		
	}
	
	public void setLogger(Logger logger) {
		
		this.logger = logger;
		
	}
	
}

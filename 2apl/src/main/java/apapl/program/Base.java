package apapl.program;

import apapl.plans.Plan;
import apapl.data.Term;
import apapl.plans.PlanSeq;
import apapl.APLModule;
import apapl.Parser;
import java.awt.Color;
import java.util.ArrayList;

/**
 * The root base class for all kinds of bases. This class implements functionality for
 * displaying bases.
 * 
 * @note this functionality should move to the view layer of the application.
 */
public class Base
{
	final static String tabString =  "\t";   
	final static String rtfTabString =  "\\tab ";
	
	/**
	 * Concats all elements from an Arraylist into one string. This can be 
	 * useful for displaying bases.
	 * 
	 * @param list the list to be concatenated 
	 * @param s all elements will be separated by this <code>String</code>
	 * @return the concatenated string
	 */
	public static String concatWith(Iterable list, String s)
	{
		String r = "";
		for (Object a : list) r = r + a + s;
				
		if (r.length()>=s.length()) r = r.substring(0,r.length()-s.length());	
		
		return r;
	}
	
	/**
	 * Concats all elements from an Arraylist into one RTF format string. This can be 
	 * useful for displaying bases.
	 * 
	 * @param list the list to be concatenated 
	 * @param s all elements will be separated by this <code>String</code>
	 * @param inplan indicating whether the list to display occurs inside a plan
	 * @return the concatenated RTF string
	 */
	public static String rtfconcatWith(Iterable<?> list, String s, boolean inplan)
	{
		String r = "";
		for (Object a : list)  {
			if (a instanceof Plan) r = r + ((Plan)a).toRTF(inplan) + s;
			else if (a instanceof PlanSeq) r = r + ((PlanSeq)a).toRTF(inplan) + s;
			else if (a instanceof Term) r = r + ((Term)a).toRTF(inplan) + s;
			else return r;
		}
		if (r.length()>=s.length()) r = r.substring(0,r.length()-s.length());	
		return r;
	}

	/**
	 * Returns a string of <code>t</code> tabs.
	 * 
	 * @param t number of tabs
	 * @return the string corresponding to <code>t</code> tabs
	 */
	public static String tabs(int t)
	{
		String s = "";
		while (t>0) {s = s + tabString; t--;}
		return s;
	}

	/**
	 * Returns a RTF format string of <code>t</code> tabs.
	 * 
	 * @param t number of tabs
	 * @return the string corresponding to <code>t</code> tabs
	 */
	public static String rtftabs(int t)	
	{
		String s = "";
		while (t>0) {s = s + rtfTabString; t--;}
		return s;
	}
}

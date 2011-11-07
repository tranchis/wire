package apapl.data;

import apapl.plans.ExternalAction;
import apapl.UnboundedVarException;

import apapl.SubstList;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * A list consisting of a head and a tail. The head is always a single term which
 * can also be a {@link apapl.data.APLVar} or a {@link apapl.data.APLListVar}, the tail can
 * be an {@link apapl.data.APLListVar} or a list again.
 */
public class APLList extends APLListVar
{
	private Term head;
	private APLListVar tail;
	
	/**
	 * Constructs a list.
	 */
	public APLList ()
	{
		head = null;
		tail = null;
	}
	
	/**
	 * Constructs a list out of a list of terms.
	 * 
	 * @param terms the list of terms
	 */
	public APLList(LinkedList<Term> terms)
	{
		head = terms.poll();
		if (head!=null) tail = new APLList(terms);
		else tail = null;
	}
	
	/**
	 * Constructs a list out of a list of terms.
	 * 
	 * @param terms the list of terms
	 */
	public APLList (Term... terms)
	{
		if (terms.length==0) {
			head = null;
			tail = null;
		}
		else {
			head = terms[0];
			tail = makeList(1,terms);
		} 
	}
	
	/**
	 * Tests whether this list is empty or not. A list is empty if its head is null.
	 * 
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return head==null&&tail==null;
	}
	
	/**
	 * Constructs a list with head <code>head</code> and tail the variable 
	 * <code>tail</code>.
	 * 
	 * @param makeADifference not used anymore
	 * @param head the head
	 * @param tail the tail
	 */
	public APLList(boolean makeADifference, Term head, APLListVar tail)
	{
		this.head = head;
		this.tail = tail;
	}
	
	private static APLListVar makeList(int i, Term... terms)
	{
		if (terms.length==i) return new APLList();
		else return new APLList(true,terms[i],makeList(i+1,terms));
	}
	
	private void setHeadAndTail(Term head, APLListVar tail)
	{
		this.head = head;
		this.tail = tail;
	}

	/**
	 * Constructs a list consisting of a list of terms and a list variable. This method
	 * is used in recursively constructing a list. An empty list is constructed if
	 * l is empty and r is null. If l is empty and r is not null the constructed list 
	 * will be equal to r.
	 * 
	 * @param l the list of terms
	 * @param r the list variable
	 * @return the list
	 */
	public static APLListVar constructList(LinkedList<Term> l, APLListVar r)
	{
		if (l.isEmpty()) return (r!=null?r:new APLList());
		else return new APLList(true,l.remove(),constructList(l,r));
	}
	
	public String toString(boolean inplan)
	{
		if (isEmpty()) return "[]";
		else if (oneElement()) return "["+head.toString(inplan)+"]";
		else if (tail instanceof APLList)
			return  "["+head.toString(inplan)+","+tail.toString(inplan).substring(1);
		else if (tail instanceof APLVar)
		{
			APLVar v = (APLVar) tail;
			if (v.isBounded()) return "["+head.toString(inplan)+","+v.toString(inplan).substring(1);
			else return "["+head.toString(inplan)+"|"+v+"]";
		}
		else return "[]";
	}
	
	public String toString()
	{
		return toString(false);
	}
	
	/**
	 * Tests whether this is a singleton list.
	 * 
	 * @return true if the list consists out of one element, false otherwise
	 */
	public boolean oneElement()
	{
		if (tail instanceof APLList) return ((APLList)tail).isEmpty();
		else if (tail instanceof APLVar) {
			if (((APLVar)tail).isBounded()) {
				Term t = ((APLVar)tail).getSubst();
				if (t instanceof APLList) return ((APLList)t).isEmpty();
				else return false;
			}
			else return false;
		}
		else return false;
	}
	
	public String toRTF(boolean inplan)
	{
		if (isEmpty()) return "[]";
		else if (oneElement()) return "["+head.toRTF(inplan)+"]";
		if (tail instanceof APLList)
			return  "["+head.toRTF(inplan)+","+tail.toRTF().substring(1);
		else if (tail instanceof APLVar)
		{
			APLVar v = (APLVar) tail;
			if (v.isBounded()) return "["+head+","+v.toString().substring(1);
			else return "["+head.toRTF(inplan)+"\\cf1 |\\cf0 "+v.toRTF()+"]";
		}
		else return "[]";
	}
	
	public Term getHead()
	{
		return head;
	}
	
	public APLListVar getTail()
	{
		return tail;
	}

	public void applySubstitution(SubstList<Term> theta)
	{
		if (!isEmpty()) {
			head.applySubstitution(theta);
			tail.applySubstitution(theta);
		}
	}
	
	public APLList clone()
	{
		if (isEmpty()) return new APLList();
		else return new APLList(true,head.clone(),tail.clone());
	}
	
	/**
	 * Turn all variables of this list into fresh (unique) variables.
	 * 
	 * @param unfresh the list of variables that cannot be used anymore
	 * @param own all the variables in the context of this list. These
	 *            are the variables that occur in the rule in which the query occurs.
	 * @param changes list [[old,new],...] of substitutions 
	 */
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		if (!isEmpty()) {
			head.freshVars(unfresh,own,changes);
			tail.freshVars(unfresh,own,changes);
		}
	}
	
	/**
	 * Return all variables that occur in this list.
	 * 
	 * @return the list of variables
	 */
	public ArrayList<String> getVariables()
	{
		if (isEmpty()) return new ArrayList<String>();
		
		ArrayList<String> vars = head.getVariables();
		vars.addAll(tail.getVariables());
		return vars;
	}
	
	/**
	 * Converts this list to a <code>LinkedList<Term></code>. 
	 * Ignores all unbounded variables.
	 * 
	 * @return the list of terms
	 */
	public LinkedList<Term> toLinkedList()
	{
		if (head==null) return new LinkedList<Term>();
		try {
			Term a = Term.unvar(tail);
			if (a instanceof APLList) {
				LinkedList<Term> l = ((APLList)a).toLinkedList();
				l.addFirst(head);
				return l;
			}
			else return new LinkedList<Term>();
 
		}
		catch (UnboundedVarException ex) {
			return new LinkedList<Term>();
		}
	}
	
	/**
	 * Test whether this list is equal to another list.
	 * 
	 * @param other the list to compare with
	 * @return true if the lists are equal, false otherwise
	 */
	public boolean equals(Term other)
	{
		if (other instanceof APLList)
		{
			APLList term = (APLList)other;
			if (term.isEmpty()&&isEmpty()) return true;
			else if (term.isEmpty()||isEmpty()) return false;
			if (!head.equals(term.getHead())) return false;
			else if (!tail.equals(term.getTail())) return false;
			else return true;
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
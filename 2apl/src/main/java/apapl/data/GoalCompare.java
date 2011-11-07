package apapl.data;

import java.util.Comparator;

/**
 * A convenience class for comparing goals. This class is implemented as a singleton.
 * 
 * @note consider implementing all the methods as static, it does not have a state
 */
public class GoalCompare implements Comparator<Literal>
{
	public static final GoalCompare INSTANCE  = new GoalCompare();
	
	private GoalCompare()
	{
	}
	
	/**
	 * Compares goal <code>a</code> and <code>b</code> based on a lexographical analysis
	 * of the string representations of both goals. 
	 * 
	 * @param a goal to compare
	 * @param b goal to compare with
	 * @return the value 0 if <code>a</code> equals <code>b</code> <br>
	 *         a value greater than 0 if <code>a</code> is lexographically smaller 
	 *         than <code>b</code> <br>
	 *         a value less than 0 if <code>a</code> is lexographically bigger 
	 *         than <code>b</code> 
	 */
	public int compare(Literal a, Literal b)
	{
		return a.toString().compareTo(b.toString());
    }
    
	/**
	 * Checks whether two goals are equal.
	 * 
	 * @param a goal to compare
	 * @param b goal to compare with
	 * @return true if the goals are equal, false otherwise.
	 */
    public boolean equals(Literal a, Literal b)
    {
    	return a.toString().equals(b.toString());
	}
}
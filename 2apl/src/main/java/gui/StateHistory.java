package gui;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * State history is a data structure used to store history of past states of a
 * module. It stores beliefs, goals, plans and additional log text for each
 * module state. The state history has a limited capacity. Its concrete value is
 * controlled by the {@link #capacity} class field.
 */
public class StateHistory
{
	private List<HashMap<String, String>> parts;
	
	public static int LOGS = 0;
	public static int BELIEFS = 1;
	public static int GOALS = 2;
	public static int PLANS = 3;
	
	private static int capacity = 1000;
	
	/** The lowest state number stored in the state history */	
	int firstState = 0;
	
	/** The highest state number stored in the state history */	
	int lastState = 0;
	
	public StateHistory()
	{
		clear();
	}
	
	public void addPart(int part, int stateNo, String content)
	{
		parts.get(part).put(stateNo + "",content);
		lastState = stateNo;
		
		// when capacity is exceeded by 100 states, drop the first 100 states from the history
		if (parts.get(LOGS).size() > capacity + 100) 
			dropFirst(100);
	}
	
	/**
	 * Returns specific part of the state history.
	 * 
	 * @param part part of the history to return. One of:
	 *        <code>StateHistory.LOGS</code>, <code>StateHistory.BELIEFS</code>,
	 *        <code>StateHistory.GOALS</code>, <code>StateHistory.PLANS</code>
	 * @param stateNo number of state to return
	 * @return specific part of the past state of the module. Returns null if
	 *         the desired state is not part of the history.
	 */
	public String getItem(int part, int stateNo)
	{
		String content = null;
		int i = stateNo;
		do {
			content = parts.get(part).get(i + "");
			i--;
		}
		while (content == null && i >= firstState);
		return content;
	}
	
	public int getFirstState()
	{
		return firstState;
	}
	
	public int getLastState()
	{
		return lastState;
	}
	
	private void dropFirst(int n)
	{
		int newFirstState = firstState + n; 
		for (int i=0; i<4;  i++) {
			parts.get(i).put(newFirstState + "", getItem(i, newFirstState));
			for (int j = firstState; j < newFirstState; j++) {
				parts.get(i).remove(j + "");
			}
		}
		firstState = newFirstState;
	}
	
	public void clear() {
		parts = new ArrayList<HashMap<String,String>>();
		for (int i=0; i<4; i++) 
			parts.add(new HashMap<String,String>());
	}
	
}
	

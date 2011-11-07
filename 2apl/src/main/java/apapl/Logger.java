package apapl;

import java.util.Collection;
import java.util.LinkedList;


/**
 * This class logs all activities of a module. Now it only includes belief- and goal-dynamics.
 * @author tristanbehrens
 *
 */
public class Logger {
	
	/** A list of belief updates */
	private Collection<LogEntry> beliefUpdates = new LinkedList<LogEntry>();

	/** A list of belief queries */
	private Collection<LogEntry> beliefQueries = new LinkedList<LogEntry>();

	/** A list of goal additions */
	private Collection<LogEntry> goalAdditions = new LinkedList<LogEntry>();

	/** A list of goal removals */
	private Collection<LogEntry> goalRemovals = new LinkedList<LogEntry>();
	
	/** A list of goal-queries */
	private Collection<LogEntry> goalQueries = new LinkedList<LogEntry>();
	
	public void beliefUpdate(String data, String comment) {
		beliefUpdates.add(new LogEntry(data,comment));
	}

	public void beliefQuery(String data, String comment) {
		beliefQueries.add(new LogEntry(data,comment));
	}

	public void goalAddition(String data, String comment) {
		goalAdditions.add(new LogEntry(data,comment));
	}

	public void goalRemoval(String data, String comment) {
		goalRemovals.add(new LogEntry(data,comment));
	}

	public void goalQuery(String data, String comment) {
		goalQueries.add(new LogEntry(data,comment));
	}

	public void clear() {
		
		beliefUpdates.clear();
		beliefQueries.clear();
		goalAdditions.clear();
		goalRemovals.clear();
		goalQueries.clear();
		
	}
	
	public String toString() {
	
		String ret = "";
		
		ret += "Belief-updates: " + beliefUpdates.toString() + "\n";
		ret += "Belief-queries: " + beliefQueries.toString() + "\n";
		ret += "Goal-additions: " + goalAdditions.toString() + "\n";
		ret += "Goal-removals : " + goalRemovals.toString() + "\n";
		ret += "Goal-queries  : " + goalQueries.toString() + "\n";
		
		return ret;
		
	}

	/**
	 * @return the beliefUpdates
	 */
	public Collection<LogEntry> getBeliefUpdates() {
		return beliefUpdates;
	}

	/**
	 * @return the beliefQueries
	 */
	public Collection<LogEntry> getBeliefQueries() {
		return beliefQueries;
	}

	/**
	 * @return the goalAdditions
	 */
	public Collection<LogEntry> getGoalAdditions() {
		return goalAdditions;
	}

	/**
	 * @return the goalRemovals
	 */
	public Collection<LogEntry> getGoalRemovals() {
		return goalRemovals;
	}

	/**
	 * @return the goalQueries
	 */
	public Collection<LogEntry> getGoalQueries() {
		return goalQueries;
	}
	
}

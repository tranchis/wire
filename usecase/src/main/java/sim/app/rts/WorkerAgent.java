/*
  Copyright 2011 by Sergio Alvarez-Napagao
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package sim.app.rts;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import sim.engine.SimState;
import sim.util.Double2D;

public class WorkerAgent extends Agent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9206417207764424933L;

	public static final double DIAMETER = 8;
	public String id;

	public WorkerAgent(final Double2D location, String id)
	{
		super(DIAMETER, Color.RED);

		this.agentLocation = location;
		this.id = id;
		actions = new LinkedList<Action>();
		currentAction = null;
		
		try
		{
			intID = Integer.parseInt(id.substring(6)); // "TARGET"
		}
		catch (IndexOutOfBoundsException e)
		{
			System.err.println("Exception generated: " + e);
			System.exit(1);
		}
		catch (NumberFormatException e)
		{
			System.err.println("Exception generated: " + e);
			System.exit(1);
		}
	}

	Double2D suggestedLocation = null;
	private Queue<Action>	actions;
	private Action			currentAction;

	public void step(final SimState state)
	{
		if(actions.isEmpty())
		{
			actions.add(getRandomAction(state));
		}
		
		if(currentAction == null || currentAction.isFinished())
		{
			currentAction = actions.poll();
		}
		
		currentAction.executeStep(state);
	}

	private Action getRandomAction(final SimState state)
	{
		switch(state.random.nextInt(2))
		{
		case 0:
//			return new WalkRandom(this, state);
			return new PickResource(this, state);
		case 1:
		default:
			return new PickResource(this, state);
		}
	}
}

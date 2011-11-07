/*
  Copyright 2011 by Sergio Alvarez-Napagao
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package sim.app.rts;

import java.awt.Color;

import sim.engine.SimState;
import sim.util.Double2D;

public class ResourceAgent extends Agent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9206417207764424933L;

	public String id;

	public ResourceAgent(final Double2D location, String id, double diameter)
	{
		super(diameter, Color.YELLOW);

		this.agentLocation = location;
		this.id = id;

		try
		{
			intID = Integer.parseInt(id.substring(8)); // "TARGET"
		}
		catch (IndexOutOfBoundsException e)
		{
			System.err.println("Exception generated: " + e);
			e.printStackTrace();
			System.exit(1);
		}
		catch (NumberFormatException e)
		{
			System.err.println("Exception generated: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	Double2D desiredLocation = null;
	Double2D suggestedLocation = null;
	int steps = 0;

	public void step(final SimState state)
	{
	}

	// application specific variables
	public static final int GOLD = 0;

	protected int agentState;

	public int getState()
	{
		return agentState;
	}

	public void removeResource(int i)
	{
		this.diameter = Math.max(diameter - i, 0);
		this.scale = diameter;
	}
}

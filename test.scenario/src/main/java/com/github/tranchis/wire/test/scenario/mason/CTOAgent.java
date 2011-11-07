/*
  Copyright 2011 by Sergio Alvarez-Napagao
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package com.github.tranchis.wire.test.scenario.mason;

import java.awt.Color;

import sim.engine.SimState;
import sim.util.Double2D;

public class CTOAgent extends Agent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9206417207764424933L;

	public static final double DIAMETER = 8;

	public CTOAgent(final Double2D location, String id)
	{
		super(DIAMETER, Color.BLACK, id);

		this.agentLocation = location;

		try
		{
			intID = Integer.parseInt(id.substring(5)); // "AGENT"
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

	Double2D desiredLocation = null;
	Double2D suggestedLocation = null;
	int steps = 0;

	public void step(final SimState state)
	{
		CooperativeObservation hb = (CooperativeObservation) state;

		Double2D location = agentLocation;// hb.environment.getObjectLocation(this);

		hb.agentPos[intID] = location;

		suggestedLocation = hb.kMeansEngine.getGoalPosition(intID);
		if (suggestedLocation != null)
		{
			desiredLocation = suggestedLocation;
		}
		else
		{
			steps--;
			if (steps <= 0)
			{
				desiredLocation = new Double2D(
						state.random.nextDouble()
								* (CooperativeObservation.XMAX
										- CooperativeObservation.XMIN - DIAMETER)
								+ CooperativeObservation.XMIN + DIAMETER / 2,
						state.random.nextDouble()
								* (CooperativeObservation.YMAX
										- CooperativeObservation.YMIN - DIAMETER)
								+ CooperativeObservation.YMIN + DIAMETER / 2);
				steps = 100;
			}
		}

		double dx = desiredLocation.x - location.x;
		double dy = desiredLocation.y - location.y;
		if (dx > 0.5)
			dx = 0.5;
		else if (dx < -0.5)
			dx = -0.5;
		if (dy > 0.5)
			dy = 0.5;
		else if (dy < -0.5)
			dy = -0.5;
		if (dx < 0.5 && dx > -0.5 && dy < 0.5 && dy > -0.5)
			steps = 0;

		dx *= 2.0;
		dy *= 2.0;

		if (!hb.acceptablePosition(this, new Double2D(location.x + dx,
				location.y + dy)))
		{
			steps = 0;
		}
		else
		{
			agentLocation = new Double2D(location.x + dx, location.y + dy);
			hb.environment.setObjectLocation(this, agentLocation);
		}

	}
}

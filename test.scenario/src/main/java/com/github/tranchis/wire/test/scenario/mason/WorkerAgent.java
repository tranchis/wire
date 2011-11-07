/*
  Copyright 2011 by Sergio Alvarez-Napagao
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package com.github.tranchis.wire.test.scenario.mason;

import java.awt.Color;

import sim.engine.SimState;
import sim.util.Double2D;

public class WorkerAgent extends Agent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9206417207764424933L;

	public static final double DIAMETER = 8;

	public WorkerAgent(final Double2D location, String id)
	{
		super(DIAMETER, Color.RED, id);

		this.agentLocation = location;
		
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

	private Double2D destination = null;

	public void step(final SimState state)
	{
		Double2D	location, desiredLocation;
		
		if(destination != null)
		{
			desiredLocation = destination;
			CooperativeObservation hb = (CooperativeObservation) state;
	
			location = this.getLocation();
			hb.targetPos[this.getID()] = location;
	
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
			{
				hb.environment.setObjectLocation(this, desiredLocation);
				this.setLocation(desiredLocation);
				hb.updateDistances(this);
				destination = null;
			}
			else
			{
				if (!hb.acceptablePosition(this, new Double2D(location.x + dx,
						location.y + dy)))
				{
					destination = null;
				}
				else
				{
					location = new Double2D(location.x + dx, location.y + dy);
					hb.environment.setObjectLocation(this, location);
					this.setLocation(location);
					hb.updateDistances(this);
				}
			}	
		}
	}

	public void setDestination(Double2D destination)
	{
		this.destination = destination;
	}
}

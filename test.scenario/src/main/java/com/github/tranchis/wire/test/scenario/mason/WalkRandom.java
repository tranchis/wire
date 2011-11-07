package com.github.tranchis.wire.test.scenario.mason;

import sim.engine.SimState;
import sim.util.Double2D;

public class WalkRandom implements Action
{
	Double2D desiredLocation = null;
	int steps;
	Agent	agent;
	
	public WalkRandom(Agent agent, SimState state)
	{
		this.agent = agent;
		desiredLocation = new Double2D(state.random.nextDouble()
				* (CooperativeObservation.XMAX
						- CooperativeObservation.XMIN - agent.getDiameter())
				+ CooperativeObservation.XMIN + agent.getDiameter() / 2,
				state.random.nextDouble()
						* (CooperativeObservation.YMAX
								- CooperativeObservation.YMIN - agent.getDiameter())
						+ CooperativeObservation.YMIN + agent.getDiameter() / 2);
		steps = 100;
	}

	public boolean isFinished()
	{
		return (steps <= 0);
	}

	public void executeStep(final SimState state)
	{
		Double2D	location;
		
		CooperativeObservation hb = (CooperativeObservation) state;

		location = agent.getLocation();
		hb.targetPos[agent.getID()] = location;

		steps--;

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

		if (!hb.acceptablePosition(agent, new Double2D(location.x + dx,
				location.y + dy)))
		{
			steps = 0;
		}
		else
		{
			location = new Double2D(location.x + dx, location.y + dy);
			hb.environment.setObjectLocation(agent, location);
			agent.setLocation(location);
		}
	}
}

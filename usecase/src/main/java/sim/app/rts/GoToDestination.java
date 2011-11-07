package sim.app.rts;

import sim.engine.SimState;
import sim.util.Double2D;

public class GoToDestination implements Action
{
	private Double2D				desiredLocation = null;
	private int						steps;
	private Agent					agent;
	private CooperativeObservation	co;
	private Agent					destination;
	private ResourceInfo ri;
	
	public GoToDestination(Agent agent, SimState state, ResourceInfo ri)
	{
		this.agent = agent;
		co = (CooperativeObservation)state;
		this.ri = ri;
	}

	public boolean isFinished()
	{
		return co.isTouching(agent, destination);
	}

	public void executeStep(final SimState state)
	{
		Double2D	location;
		
//		System.out.println("executeStep");
		this.destination = ri.getPosition();
		desiredLocation = destination.getLocation();
		CooperativeObservation hb = (CooperativeObservation) state;

		location = agent.getLocation();
		hb.targetPos[agent.getID()] = location;

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
			// Do nothing...
		}

		if (!hb.acceptablePosition(agent, new Double2D(location.x + dx,
				location.y + dy)))
		{
			// Just cross, for now
			// TODO: Make it fancy
			location = new Double2D(location.x + dx, location.y + dy);
			hb.environment.setObjectLocation(agent, location);
			agent.setLocation(location);
		}
		else
		{
			location = new Double2D(location.x + dx, location.y + dy);
			hb.environment.setObjectLocation(agent, location);
			agent.setLocation(location);
		}
	}
}

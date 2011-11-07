package sim.app.rts;

import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Double2D;

public class FindNearestResource implements Action
{
	private Agent			agent;
	private ResourceInfo	ri;
	private boolean			finished;

	public FindNearestResource(Agent agent, ResourceInfo ri)
	{
		this.agent = agent;
		this.ri = ri;
		this.finished = false;
	}

	public boolean isFinished()
	{
		return finished;
	}

	public void executeStep(SimState state)
	{
		CooperativeObservation	co;
		Bag						agents;
		Double2D				location;
		Agent					current;
		double					minDistance, candidate;
		int						i;
		
		co = (CooperativeObservation)state;
		location = agent.getLocation();
		minDistance = Double.POSITIVE_INFINITY;
		current = null;
		agents = co.environment.getAllObjects();
		
		for(i=0;i<agents.numObjs;i++)
		{
			if(agents.objs[i] instanceof ResourceAgent && ((Agent)agents.objs[i]).getDiameter() > 0)
			{
				candidate = location.distance(((Agent)agents.objs[i]).getLocation());
				if(candidate < minDistance)
				{
					minDistance = candidate;
					current = (Agent)agents.objs[i];
				}
			}
		}
		
		ri.setResourcePos(current);
		finished = true;
	}
}

package com.github.tranchis.wire.test.scenario.mason;

import sim.engine.SimState;

public class GetResource implements Action
{
	private boolean	finished;
	private Agent agent;
	private ResourceInfo ri;
	
	public GetResource(Agent agent, SimState state, ResourceInfo ri)
	{
		finished = false;
		this.agent = agent;
		this.ri = ri;
	}

	public boolean isFinished()
	{
		return finished;
	}

	public void executeStep(SimState state)
	{
		agent.addResource(1);
		ri.removeResource(1);
		finished = true;
	}
}

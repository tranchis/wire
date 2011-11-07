package com.github.tranchis.wire.test.scenario.mason;

import sim.engine.SimState;

public class PickResource extends ComplexAction
{
	private Agent 			agent;
	private ResourceInfo	ri;
	private SimState		state;

	public PickResource(Agent agent, SimState state)	
	{
		this.agent = agent;
		this.state = state;
		ri = new ResourceInfo();
	}
	
	@Override
	public Action[] getActionList()
	{
		return new Action[]
				{
					new FindNearestResource(agent, ri),
					new GoToDestination(agent, state, ri),
					new GetResource(agent, state, ri),
				};
	}
}

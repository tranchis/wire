package com.github.tranchis.wire.test.scenario.mason;

public class ResourceInfo
{
	private Agent	position;

	public void setResourcePos(Agent current)
	{
		position = current;
	}

	public Agent getPosition()
	{
		return position;
	}

	public void removeResource(int i)
	{
		ResourceAgent	ra;
		
		ra = (ResourceAgent)position;
		ra.removeResource(i);
	}
}

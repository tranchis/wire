package com.github.tranchis.wire.test.scenario.mason;

import java.awt.Color;

import sim.engine.Steppable;
import sim.util.Double2D;

public abstract class Agent extends sim.portrayal.simple.OvalPortrayal2D implements Steppable
{
	protected double	diameter;
	public int intID = -1;
	public String id;

	// for Object2D
	public Double2D agentLocation = null;
	private int resources;

	public double getDiameter()
	{
		return diameter;
	}
	
	public Agent(double diameter, Color color, String id)
	{
		super(diameter);
		this.diameter = diameter;
		paint = color;
		resources = 0;
		this.id = id;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8376551751387446050L;
	public static final double MAX_DIAMETER = 50;

	public int getID()
	{
		return intID;
	}

	public Double2D getLocation()
	{
		return agentLocation;
	}

	public void setLocation(Double2D location)
	{
		agentLocation = location;
	}

	public void addResource(int i)
	{
		this.resources = this.resources + i;
	}
}

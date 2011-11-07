/*
  Copyright 2011 by Sergio Alvarez-Napagao
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package com.github.tranchis.wire.test.scenario.mason;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import eis.iilang.Identifier;

import sim.engine.SimState;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Double2D;

public class CooperativeObservation extends SimState
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5341304850653413506L;
	public static final double XMIN = 0;
	public static final double XMAX = 400;
	public static final double YMIN = 0;
	public static final double YMAX = 400;

	public static final int NUM_TARGETS		= 10;
	public static final int NUM_AGENTS		= 3;
	public static final int NUM_RESOURCES	= 5;
	private static final double SAFETY_DISTANCE = 5;
	public static final int	DISTANCE_PERCEPT = 30;

	Double2D[]	agentPos;
	Double2D[]	targetPos;
	Double2D[]	resourcePos;
	public Continuous2D environment = null;
	private TreeMap<String,WorkerAgent>		agentSet;
	private TreeMap<String,Agent>			allAgents;
	private Map<Agent,Map<Agent,Double>>	distances;
	private Set<Agent>						agents;

	KMeansEngine kMeansEngine;

	/**
	 * Creates a CooperativeObservation simulation with the given random number
	 * seed.
	 */
	public CooperativeObservation(long seed)
	{
		super(seed);
		agentSet = new TreeMap<String,WorkerAgent>();
		allAgents = new TreeMap<String,Agent>();
		distances = new HashMap<Agent,Map<Agent,Double>>();
		agents = new HashSet<Agent>();
	}

	boolean conflict(final Object obj1, final Double2D a,
			final Object obj2, final Double2D b)
	{
		Agent	agent1, agent2;
		double	radius1, radius2;
		
		agent1 = (Agent)obj1;
		agent2 = (Agent)obj2;
		radius1 = agent1.getDiameter() / 2;
		radius2 = agent2.getDiameter() / 2;
		
//		System.out.println(a.x + ":" + a.y + ":" + radius1 + "," + b.x + ":" + b.y + ":" + radius2 + ", conflict = " + 
//				(((a.x > b.x && a.x - radius1 < b.x + radius2) ||
//						(a.x < b.x && a.x + radius1 > b.x - radius2))
//					&& ((a.y > b.y && a.y - radius1 < b.y + radius2) ||
//						(a.y < b.y && a.y + radius1 > b.y - radius2))));
		
		return (((a.x > b.x && a.x - radius1 < b.x + radius2) ||
				(a.x < b.x && a.x + radius1 > b.x - radius2))
			&& ((a.y > b.y && a.y - radius1 < b.y + radius2) ||
				(a.y < b.y && a.y + radius1 > b.y - radius2)));
	}

	boolean acceptablePosition(final Object obj, final Double2D location)
	{
		Agent	agent;
		double	diameter;
		
		if(!(obj instanceof WorkerAgent))
		{
			agent = (Agent)obj;
			diameter = agent.getDiameter();
			if (location.x < diameter / 2
					|| location.x > (XMAX - XMIN)/* environment.getXSize() */
							- diameter / 2
					|| location.y < diameter / 2
					|| location.y > (YMAX - YMIN)/* environment.getYSize() */
							- diameter / 2)
				return false;
			Bag misteriousObjects = environment.getObjectsWithinDistance(location, /* Strict */
					Math.max(2 * Agent.MAX_DIAMETER, 2 * Agent.MAX_DIAMETER));
			if (misteriousObjects != null)
			{
				for (int i = 0; i < misteriousObjects.numObjs; i++)
				{
					if (misteriousObjects.objs[i] != null
							&& misteriousObjects.objs[i] != agent)
					{
						Object ta = (Agent) (misteriousObjects.objs[i]);
						if (conflict(agent, location, ta,
								environment.getObjectLocation(ta)))
						{
	//						System.out.println("Conflict between " + agent.id + " and " + ((Agent)ta).id);
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public void start()
	{
		super.start(); // clear out the schedule

		Iterator<?>			it;
		Object				obj;
		Iterator<Agent>		it1, it2;
		Agent				a1, a2;
		Map<Agent,Double>	dmap;
		double				distance;
		
		agentPos = new Double2D[NUM_AGENTS];
		for (int i = 0; i < NUM_AGENTS; i++)
			agentPos[i] = new Double2D();

		targetPos = new Double2D[NUM_TARGETS];
		for (int i = 0; i < NUM_TARGETS; i++)
			targetPos[i] = new Double2D();

		resourcePos = new Double2D[NUM_RESOURCES];
		for (int i = 0; i < NUM_RESOURCES; i++)
			resourcePos[i] = new Double2D();

		kMeansEngine = new KMeansEngine(this);

		environment = new Continuous2D(8.0, XMAX - XMIN, YMAX - YMIN);

		// Schedule the agents -- we could instead use a RandomSequence, which
		// would be faster,
		// but this is a good test of the scheduler
		for (int x = 0; x < NUM_AGENTS + NUM_TARGETS + NUM_RESOURCES; x++)
		{
			Double2D loc = null;
			Agent agent = null;
			int times = 0;
			do
			{
				if (x < NUM_AGENTS)
				{
					loc = new Double2D(random.nextDouble()
							* (XMAX - XMIN - CTOAgent.DIAMETER) + XMIN + CTOAgent.DIAMETER / 2,
							random.nextDouble() * (YMAX - YMIN - CTOAgent.DIAMETER) + YMIN
									+ CTOAgent.DIAMETER / 2);
					agent = new CTOAgent(loc, "Guard" + x);
				}
				else if(x < NUM_AGENTS + NUM_TARGETS)
				{
					loc = new Double2D(random.nextDouble()
							* (XMAX - XMIN - CTOAgent.DIAMETER) + XMIN + CTOAgent.DIAMETER / 2,
							random.nextDouble() * (YMAX - YMIN - CTOAgent.DIAMETER) + YMIN
									+ CTOAgent.DIAMETER / 2);
					agent = new WorkerAgent(loc, "Target"
							+ (x - NUM_AGENTS));
					agentSet.put(((WorkerAgent)agent).id, (WorkerAgent)agent);
				}
				else
				{
					double diameter = (new Random()).nextDouble() * Agent.MAX_DIAMETER;
					loc = new Double2D(random.nextDouble()
							* (XMAX - XMIN - diameter) + XMIN + diameter / 2,
							random.nextDouble() * (YMAX - YMIN - diameter + YMIN
									+ diameter / 2));
					agent = new ResourceAgent(loc, "Resource" + (x - (NUM_AGENTS + NUM_TARGETS)), diameter);
				}
				allAgents.put(agent.id, agent);
				times++;
				if (times == 1000)
				{
					System.err.println("Cannot place agents. Exiting....");
					System.exit(1);
				}
			} while (!acceptablePosition(agent, loc));
			environment.setObjectLocation(agent, loc);
			schedule.scheduleRepeating(agent);
		}

		it = environment.allObjects.iterator();
		while(it.hasNext())
		{
			obj = it.next();
			if(obj instanceof Agent)
			{
				agents.add((Agent)obj);
			}
		}
		
		it1 = agents.iterator();
		while(it1.hasNext())
		{
			a1 = it1.next();
			dmap = new HashMap<Agent,Double>();
			distances.put(a1, dmap);
			it2 = agents.iterator();
			while(it2.hasNext())
			{
				a2 = it2.next();
				if(a1 != a2)
				{
					distance = getDistance(a1, a2);
					dmap.put(a2, distance);
				}
			}
		}
	}

	private double getDistance(Agent a1, Agent a2)
	{
		return a1.getLocation().distance(a2.getLocation()) - a1.diameter / 2 - a2.diameter / 2;
	}

	public static void main(String[] args)
	{
		doLoop(CooperativeObservation.class, args);
		System.exit(0);
	}

	public boolean isTouching(Agent agent, Agent destination)
	{		
		return (distances.get(agent).get(destination)) < SAFETY_DISTANCE;
	}

	public Collection<WorkerAgent> getAgentSet()
	{
		return agentSet.values();
	}

	public WorkerAgent getAgent(String entity)
	{
		return agentSet.get(entity);
	}

	public Agent findResource(WorkerAgent agent)
	{
		Double2D	location;
		Agent		current;
		double		minDistance, candidate;
		int			i;
		Bag			agents;
		
		location = agent.getLocation();
		minDistance = Double.POSITIVE_INFINITY;
		current = null;
		agents = environment.getAllObjects();
		
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
		
		return current;
	}

	public void moveTowards(WorkerAgent agent, Number x, Number y)
	{
		agent.setDestination(new Double2D(x.doubleValue(), y.doubleValue()));
	}

	public void updateDistances(WorkerAgent a1)
	{
		Map<Agent,Double>	dmap;
		Iterator<Agent>		it2;
		Agent				a2;
		double				distance;
		
		dmap = distances.get(a1);
		it2 = agents.iterator();
		while(it2.hasNext())
		{
			a2 = it2.next();
			if(a1 != a2)
			{
				distance = getDistance(a1, a2);
				dmap.put(a2, distance);
				distances.get(a2).put(a1, distance);
			}
		}
	}

	public Set<Agent> getCloseAgents(Agent a1)
	{
		Map<Agent,Double>	dmap;
		Iterator<Agent>		it2;
		Agent				a2;
		Set<Agent>			s;
		
		s = new HashSet<Agent>();
		dmap = distances.get(a1);
		it2 = dmap.keySet().iterator();
		while(it2.hasNext())
		{
			a2 = it2.next();
			{
				if(dmap.get(a2) <= DISTANCE_PERCEPT)
				s.add(a2);
			}
		}
		
		return s;
	}

	public boolean areNextTo(Agent a1, Agent a2)
	{
		return distances.get(a1).get(a2) <= SAFETY_DISTANCE;
	}

	public Agent getAgent(Identifier r)
	{
		return allAgents.get(r.getValue().replaceAll("\"", ""));
	}

	public boolean isSuitableSpot(Number x, Number y)
	{
		long		diameter;
		Double2D	location;
		
		location = new Double2D(x.doubleValue(), y.doubleValue());
		diameter = BuildingAgent.DEFAULT_DIAMETER;
		if (location.x < diameter / 2
				|| location.x > (XMAX - XMIN)/* environment.getXSize() */
						- diameter / 2
				|| location.y < diameter / 2
				|| location.y > (YMAX - YMIN)/* environment.getYSize() */
						- diameter / 2)
			return false;
		Bag misteriousObjects = environment.getObjectsWithinDistance(location, /* Strict */
				Math.max(2 * Agent.MAX_DIAMETER, 2 * Agent.MAX_DIAMETER));
		if (misteriousObjects != null)
		{
			for (int i = 0; i < misteriousObjects.numObjs; i++)
			{
				if (misteriousObjects.objs[i] != null)
				{
					double radius1, radius2;
					Agent ta = (Agent) (misteriousObjects.objs[i]);
					radius1 = ta.getDiameter() / 2;
					radius2 = diameter / 2;
					Double2D tal = ta.getLocation();
										
					if (((tal.x >= location.x && tal.x - radius1 <= location.x + radius2) ||
							(tal.x <= location.x && tal.x + radius1 >= location.x - radius2))
						&& ((tal.y >= location.y && tal.y - radius1 <= location.y + radius2) ||
							(tal.y <= location.y && tal.y + radius1 >= location.y - radius2)))
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}

	public void buildStructure(Number x, Number y)
	{
		double diameter = BuildingAgent.DEFAULT_DIAMETER;
		Double2D loc = new Double2D(x.doubleValue(), y.doubleValue());
		Agent agent = new BuildingAgent(loc, "Building" + (new Random()).nextInt(1000), diameter);
		environment.setObjectLocation(agent, loc);
		schedule.scheduleRepeating(agent);
	}
}

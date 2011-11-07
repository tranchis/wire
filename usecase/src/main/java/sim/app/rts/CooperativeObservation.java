/*
  Copyright 2011 by Sergio Alvarez-Napagao
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
 */

package sim.app.rts;

import java.util.Random;

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
	public static final int NUM_RESOURCES	= 30;
	private static final double SAFETY_DISTANCE = 5;

	Double2D[]	agentPos;
	Double2D[]	targetPos;
	Double2D[]	resourcePos;
	public Continuous2D environment = null;

	KMeansEngine kMeansEngine;

	/**
	 * Creates a CooperativeObservation simulation with the given random number
	 * seed.
	 */
	public CooperativeObservation(long seed)
	{
		super(seed);
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
						return false;
				}
			}
		}
		return true;
	}

	public void start()
	{
		super.start(); // clear out the schedule

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

	}

	public static void main(String[] args)
	{
		doLoop(CooperativeObservation.class, args);
		System.exit(0);
	}

	public boolean isTouching(Agent agent, Agent destination)
	{
		double distance, bigr;
		
		distance = agent.getLocation().distance(destination.getLocation());
		bigr = agent.getDiameter() / 2 + destination.getDiameter() / 2;
		
		return (distance - bigr) < SAFETY_DISTANCE;
	}
}

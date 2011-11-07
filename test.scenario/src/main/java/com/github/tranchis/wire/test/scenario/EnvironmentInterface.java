package com.github.tranchis.wire.test.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.github.tranchis.wire.test.scenario.mason.Agent;
import com.github.tranchis.wire.test.scenario.mason.CooperativeObservation;
import com.github.tranchis.wire.test.scenario.mason.CooperativeObservationWithUI;
import com.github.tranchis.wire.test.scenario.mason.ResourceAgent;
import com.github.tranchis.wire.test.scenario.mason.WorkerAgent;

import eis.EIDefaultImpl;
import eis.exceptions.EntityException;
import eis.exceptions.EnvironmentInterfaceException;
import eis.exceptions.ManagementException;
import eis.iilang.EnvironmentCommand;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.ParameterList;
import eis.iilang.Percept;

public class EnvironmentInterface extends EIDefaultImpl implements Runnable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2232178403832301137L;
	private CooperativeObservationWithUI	cowu;
	private CooperativeObservation			co;
//	private Environment env;
	private ArrayList<WorkerAgent>			availableAgents;

	public EnvironmentInterface()
	{
		Collection<WorkerAgent>	agents;
		Iterator<WorkerAgent>	it;
		
		cowu = new CooperativeObservationWithUI();
		cowu.createController();
		cowu.start();
		co = (CooperativeObservation)cowu.state;
		agents = co.getAgentSet();
		availableAgents = new ArrayList<WorkerAgent>();
		availableAgents.addAll(agents);

		try
		{
			it = agents.iterator();
			while(it.hasNext())
			{
				this.addEntity(it.next().id);
			}
		}
		catch (EntityException e)
		{
			e.printStackTrace();
		}
//		env = new Environment();

		Thread t = new Thread(this);
		// t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	@Override
	public void manageEnvironment(EnvironmentCommand command)
			throws ManagementException
	{
//		if(command.getName().equals("init"))
//		{
//		}
//		else
//		{
			throw new ManagementException("Couldn't understand environment command.");
//		}
	}

	public void run()
	{
		while (true)
		{
			cowu.step();
			// notify about free entities
			// for( String free : this.getFreeEntities() )
			// this.notifyFreeEntity(free);

			// tell current step
//			long step = env.getStepNumber();

			// block for 1 second
			try
			{
				Thread.sleep(5);
			}
			catch (InterruptedException e)
			{
				System.out.println(e);

			}
		}
	}
	
	public Percept actionfindResource(String entity)
	{
		WorkerAgent		wa;
		Percept			p;
		Agent			a;
		ParameterList	pl;
		
		wa = co.getAgent(entity);
		a = co.findResource(wa);
		
		pl = new ParameterList();
		pl.add(new Numeral(a.getID()));
		pl.add(new Numeral(a.getLocation().x));
		pl.add(new Numeral(a.getLocation().y));
		p = new Percept("resource", pl);
		
		return p;
	}
	
	public Percept actionobtainResource(String entity, Identifier r)
	{
		Percept			p;
		ResourceAgent	ra;
		Agent			a;
		
		a = co.getAgent(entity);
		ra = (ResourceAgent)co.getAgent(r);
		System.out.println("obtainResource: " + a.id + ", " + ra.id);		
		a.addResource(1);
		ra.removeResource(1);
		p = new Percept("success");
		
		return p;
	}
	
	public Percept actionisSuitableSpot(String entity, Numeral x, Numeral y)
	{
		Percept	p;
		
		if(co.isSuitableSpot(x.getValue(), y.getValue()))
		{
			p = new Percept("yes");
		}
		else
		{
			p = new Percept("no");
		}
		
		return p;
	}
	
	public Percept actionbuildStructure(String entity, Numeral x, Numeral y)
	{
		Percept	p;
		
		p = new Percept("success");
		co.buildStructure(x.getValue(), y.getValue());
		
		return p;
	}
	
	public Percept actionmoveTowards(String entity, Numeral x, Numeral y)
	{
		Percept			p, pn;
		WorkerAgent		wa;
		ParameterList	pl;
		Set<Agent>		closeAgents;
		Iterator<Agent>	it;
		Agent			a;

		p = new Percept("success");
		wa = co.getAgent(entity);
		co.moveTowards(wa, x.getValue(), y.getValue());
		
		pl = new ParameterList();
		pl.add(new Numeral(co.getAgent(entity).getLocation().x));
		pl.add(new Numeral(co.getAgent(entity).getLocation().y));
		pn = new Percept("location", pl);
		try
		{
			this.notifyAgentsViaEntity(pn, entity);
			closeAgents = co.getCloseAgents(wa);
			it = closeAgents.iterator();
			while(it.hasNext())
			{
				a = it.next();
				pl = new ParameterList();
				pl.add(new Identifier("\"" + a.id + "\""));
				pl.add(new Identifier("\"" + a.getClass().getSimpleName() + "\""));
				pl.add(new Numeral(a.getLocation().x));
				pl.add(new Numeral(a.getLocation().y));
				if(co.areNextTo(wa, a))
				{
					pl.add(new Identifier("true"));
				}
				else
				{
					pl.add(new Identifier("false"));
				}
				pn = new Percept("percept", pl);
				this.notifyAgentsViaEntity(pn, entity);
			}
		}
		catch (EnvironmentInterfaceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return p;
	}

	@Override
	public LinkedList<Percept> getAllPerceptsFromEntity(String entity)
	{
		LinkedList<Percept>	ret;
		Percept				p, pn;
		ParameterList		pl;
		
		ret = new LinkedList<Percept>();
		pl = new ParameterList();
		pl.add(new Numeral(CooperativeObservation.XMAX));
		pl.add(new Numeral(CooperativeObservation.YMAX));
		p = new Percept("dimensions", pl);
		ret.add(p);
		
		pl = new ParameterList();
		pl.add(new Numeral(co.getAgent(entity).getLocation().x));
		pl.add(new Numeral(co.getAgent(entity).getLocation().y));
		pn = new Percept("location", pl);
		try
		{
			this.notifyAgentsViaEntity(pn, entity);
		}
		catch (EnvironmentInterfaceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}
	
//	public Percept actionpush(String entity)
//	{
//		// push
//		if (entity.equals("robot1"))
//			env.robotPush1();
//		if (entity.equals("robot2"))
//			env.robotPush2();
//
//		return new Percept("success");
//	}
//
	public Percept actionwait(String entity)
	{
		// push
//		if (entity.equals("robot1"))
//			env.robotWait1();
//		if (entity.equals("robot2"))
//			env.robotWait2();

		return new Percept("success");
	}

	@Override
	public void release()
	{
//		env.release();
//		env = null;
		cowu.finish();
		cowu = null;
	}

	@Override
	public boolean isConnected()
	{
		return true;
	}

	// @Override
	public String requiredVersion()
	{
		return "0.2";
	}
}

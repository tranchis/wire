package msagentenvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import msagent.MSAgent;
import msagent.MSAgentActionEvent;
import msagent.MSAgentException;
import msagent.MSAgentListener;
import msagent.characters.Animation;
import msagent.characters.Character;
import msagent.characters.Genie;
import msagent.characters.Merlin;
import msagent.characters.Peedy;
import msagent.characters.Robby;

import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.EnvironmentListener;
import eis.exceptions.ActException;
import eis.exceptions.AgentException;
import eis.exceptions.EntityException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.exceptions.RelationException;
import eis.iilang.Action;
import eis.iilang.EnvironmentCommand;
import eis.iilang.Function;
import eis.iilang.Identifier;
import eis.iilang.Parameter;
import eis.iilang.Percept;

/***
 * This environment can be used to control the Microsoft Agents.
 * Since this class has been made EIS classified, knowledge of EIS is advisable.
 *
 * @see MSAgent
 * @author Ramon Hagenaars
 */
public class MSAgentEnvironment
implements EnvironmentInterfaceStandard, MSAgentListener
{
	private boolean initialized;
	private boolean paused;

	private EnvironmentCommand initializer;
	private ArrayList<EnvironmentListener> environmentListeners;
	private HashMap<String, AgentContainer> agentContainers;
	private HashMap<String, EntityContainer> entityContainers;
	private HashMap<String, MSAgent> entityObjects;
	private HashSet<MSAgent> hiddenForPause;

	/***
	 * This private method checks the action parameter and executes the desired action.
	 * @param action the action to be executed
	 * @param msAgent the MSAgent which executes the action
	 * @throws MSAgentException thrown when an error occured controlling the MSAgent
	 * @throws ActException thrown when an illegal number of arguments were passed with the action
	 */
	private static void performMSAgentAction(Action action, MSAgent msAgent)
	throws MSAgentException, ActException
	{
		String actionName = action.getName().toLowerCase();
		
		if (actionName.equals("speak"))
		{
			if (action.getParameters().size() < 1)
				throw new ActException("Invalid number of arguments passed for the action " + action.getName() + "; passed: " + action.getParameters().size() + ", required: 1");
			msAgent.speak(action.getParameters().get(0).toProlog());
		}
		else if (actionName.equals("think"))
		{
			if (action.getParameters().size() < 1)
				throw new ActException("Invalid number of arguments passed for the action " + action.getName() + "; passed: " + action.getParameters().size() + ", required: 1");			
			msAgent.think(action.getParameters().get(0).toProlog());
		}
		else if (actionName.equals("show"))
		{
			boolean animated = true;
			if (action.getParameters().size() > 0)
				animated = action.getParameters().get(0).toProlog().toLowerCase().equals("yes");// If the parameter == "yes", it will be converted to TRUE
			msAgent.show(animated);
		}
		else if (actionName.equals("hide"))
		{
			boolean animated = true;
			if (action.getParameters().size() > 0)
				animated = action.getParameters().get(0).toProlog().equals("yes");// If the parameter == "yes", it will be converted to TRUE			
			msAgent.hide(animated);
		}
		else if (actionName.equals("moveto"))
		{
			if (action.getParameters().size() < 2)
				throw new ActException("Invalid number of arguments passed for the action " + action.getName() + "; passed: " + action.getParameters().size() + ", required: 2");			
			
			int x;
			int y;
			try
			{
				x = Integer.parseInt(action.getParameters().get(0).toProlog());
				y = Integer.parseInt(action.getParameters().get(1).toProlog());
			}
			catch (ClassCastException cce) { throw new ActException("Invalid arguments passed for the action " + action.getName() + "; passed: '" + action.getParameters().get(0) + "', '" + action.getParameters().get(1) + "', required: int, int"); }
			
			int delay = 1000;
			try
			{
				if (action.getParameters().size() > 2)
					delay = Integer.parseInt(action.getParameters().get(2).toProlog());
			}
			catch (ClassCastException cce) { throw new ActException("Invalid argument passed for the action " + action.getName() + "; passed: '" + action.getParameters().get(2) + "' required: int"); }
				
			msAgent.moveTo(x, y, delay);
		}
		else if (actionName.equals("gestureat"))
		{
			if (action.getParameters().size() < 2)
				throw new ActException("Invalid number of arguments passed for the action " + action.getName() + "; passed: " + action.getParameters().size() + ", required: 2");			
			
			int x;
			int y;
			try
			{
				x = Integer.parseInt(action.getParameters().get(0).toProlog());
				y = Integer.parseInt(action.getParameters().get(1).toProlog());
			}	
			catch (ClassCastException cce) { throw new ActException("Invalid arguments passed for the action " + action.getName() + "; passed: '" + action.getParameters().get(0) + "', '" + action.getParameters().get(1) + "', required: int, int"); }
			
			msAgent.gestureAt(x, y);
		}
		else if (actionName.equals("perform")) // animation
		{
			if (action.getParameters().size() < 1)
				throw new ActException("Invalid number of arguments passed for the action " + action.getName() + "; passed: " + action.getParameters().size() + ", required: 1");
		
			Animation animation = Animation.getAnimation(msAgent.getCharacter(), action.getParameters().get(0).toProlog());
			if (animation == null)
				throw new MSAgentException("No animation exists with name " + action.getParameters().get(0).toProlog());
			
			msAgent.perform(animation);
		}
		else
			throw new ActException("No action exists with name " + action.getName());
	}
	
	/***
	 * Returns a HashSet of AgentListeners that are attached to a specific agent.
	 * @param agent the name of the agent as String
	 * @return a HashSet containing the AgentListeners
	 */
	protected HashSet<AgentListener> getAgentListeners(String agent)
	{
		HashSet<AgentListener> result = new HashSet<AgentListener>();
		result.addAll(agentContainers.get(agent).getListeners());
		
		return result;
	}
	
	/***
	 * Returns a HashSet of all attached EnvironmentListeners.
	 * @return a HashSet containing EnvironmentListeners
	 */
	protected HashSet<EnvironmentListener> getEnvironmentListeners()
	{
		HashSet<EnvironmentListener> result = new HashSet<EnvironmentListener>();
		result.addAll(environmentListeners);
		
		return result;
	}
	
	/***
	 * Main method for initializing the program. Use for test purposes only.
	 * @param args unused argument
	 */
	public static void main(String[] args)
	{
		MSAgentEnvironment env = new MSAgentEnvironment();
		
		try 
		{
			env.manageEnvironment(new EnvironmentCommand(1));

			env.registerAgent("MyAgent");
			env.associateEntity("MyAgent", "genie");
			env.performAction("MyAgent", new Action("show", new Identifier("yes")));
			env.performAction("MyAgent", new Action("speak", new Identifier("Hello world")));
			env.performAction("MyAgent", new Action("hide", new Identifier("yes")));
		} 
		catch (AgentException ae) { ae.printStackTrace(); } 
		catch (RelationException re) { re.printStackTrace(); }
		catch (NoEnvironmentException nee) { nee.printStackTrace(); }
		catch (ActException ae) { ae.printStackTrace(); }
		catch (ManagementException me) { me.printStackTrace(); }
	}
	
	/***
	 * Constructor; initializes all the collections of this class.
	 */
	public MSAgentEnvironment()
	{
		initializer = null;
		agentContainers = new HashMap<String, AgentContainer>();
		entityContainers = new HashMap<String, EntityContainer>();
		entityObjects = new HashMap<String, MSAgent>();
		environmentListeners = new ArrayList<EnvironmentListener>();
		hiddenForPause = new HashSet<MSAgent>();
	}
	
	/***
	 * Initializes the specified MSAgents. When the parameter msAgents is empty, all of the MSAgents are initialized.
	 * @param msAgents a HashSet containing the names of the MSAgents which are to be initialized, like for example "robby" and "merlin".
	 */
	public void init(HashSet<String> msAgents)
	{
		try
		{	
			for (String s : msAgents)
				if (!s.equals("peedy") && !s.equals("merlin") && !s.equals("genie") && !s.equals("robby"))
					throw new MSAgentException("Invalid entity: '" + s + "'. It should be of the following: 'peedy', 'merlin', 'genie', 'robby'");

			if (msAgents.size() == 0)
			{
				msAgents.add("peedy");
				msAgents.add("merlin");
				msAgents.add("genie");
				msAgents.add("robby");
			}
			
			if (msAgents.contains("peedy"))
			{//System.out.println("create peedy");
				entityObjects.put(Peedy.getInstance().getName().toLowerCase(), new MSAgent(Peedy.getInstance()));
				entityContainers.put(Peedy.getInstance().getName().toLowerCase(), new EntityContainer(Peedy.getInstance().getName().toLowerCase()));
			}
			if (msAgents.contains("merlin"))
			{//System.out.println("create merlin");
				entityObjects.put(Merlin.getInstance().getName().toLowerCase(), new MSAgent(Merlin.getInstance()));
				entityContainers.put(Merlin.getInstance().getName().toLowerCase(), new EntityContainer(Merlin.getInstance().getName().toLowerCase()));
			}
			if (msAgents.contains("genie"))
			{//System.out.println("create genie");
				entityObjects.put(Genie.getInstance().getName().toLowerCase(), new MSAgent(Genie.getInstance()));
				entityContainers.put(Genie.getInstance().getName().toLowerCase(), new EntityContainer(Genie.getInstance().getName().toLowerCase()));
			}
			if (msAgents.contains("robby"))
			{//System.out.println("create robby");
				entityObjects.put(Robby.getInstance().getName().toLowerCase(), new MSAgent(Robby.getInstance()));
				entityContainers.put(Robby.getInstance().getName().toLowerCase(), new EntityContainer(Robby.getInstance().getName().toLowerCase()));
			}
		}
		catch (MSAgentException mse) { mse.printStackTrace(); }
		
		MSAgent.addMSAgentListenerToAll(this);
		
		initialized = true;
	}
	
	/***
	 * Returns whether this environment was initialized.
	 * @return a boolean determining whether this environment was initialized
	 */
	public boolean isInitialized()
	{
		return initialized;
	}

	/***
	 * Attaches an EnvironmentListener to this environment.
	 */
	@Override
	public void attachEnvironmentListener(EnvironmentListener environmentListener) 
	{
		environmentListeners.add(environmentListener);
	}
	
	/***
	 * Detaches an EnvironmentListener to this environment.
	 */
	@Override
	public void detachEnvironmentListener(EnvironmentListener environmentListener) 
	{
		environmentListeners.remove(environmentListener);
	}	
	
	/***
	 * Attaches an AgentListener to this environment.
	 */
	@Override
	public void attachAgentListener(String agent, AgentListener agentListener) 
	{
		if (agentContainers.containsKey(agent))
			agentContainers.get(agent).addListener(agentListener);
	}

	/***
	 * Detaches an AgentListener to this environment.
	 */
	@Override
	public void detachAgentListener(String agent, AgentListener agentListener) 
	{
		if (agentContainers.containsKey(agent))
			agentContainers.get(agent).removeListener(agentListener);
	}
	
	/***
	 * Registers a new agent.
	 */
	@Override
	public void registerAgent(String agent) 
	throws AgentException 
	{
		if (agentContainers.containsKey(agent))
			throw new AgentException("An agent with the name '" + agent + "' already exists");
		
		agentContainers.put(agent, new AgentContainer(agent));
	}

	/***
	 * Unregisters an agent.
	 */
	@Override
	public void unregisterAgent(String agent) 
	throws AgentException 
	{
		if (!agentContainers.containsKey(agent))
			throw new AgentException("No such agent with name '" + agent + "' found");
		
		agentContainers.remove(agent);
	}
	
	/***
	 * Returns a LinkedList containing all registered agents.
	 */
	@Override
	public LinkedList<String> getAgents() 
	{
		return new LinkedList<String>(agentContainers.keySet());
	}
	
	/***
	 * Returns a LinkedList containing all entities.
	 */
	@Override
	public LinkedList<String> getEntities() 
	{
		return new LinkedList<String>(entityContainers.keySet());
	}

	/***
	 * Associates an agent with an entity.
	 */
	@Override
	public void associateEntity(String agent, String entity)
	throws RelationException 
	{
		entity = entity.toLowerCase();
		
		if (!agentContainers.containsKey(agent))
			throw new RelationException("No such agent with name '" + agent + "' found");
		if (!entityContainers.containsKey(entity))
			throw new RelationException("No such entity with name '" + entity + "' found");
		if (agentContainers.get(agent).getEntity(entity) != null)
			throw new RelationException("The agent '" + agent + "' is associated with the entity '" + entity + "' already");
		
		agentContainers.get(agent).addEntity(entityContainers.get(entity)); // The agent 'knows' the entity
		entityContainers.get(entity).addAgent(agentContainers.get(agent)); // The entity 'knows' the agent
	}

	/***
	 * Frees an entity; removing every existing relation with an agent.
	 */
	@Override
	public void freeEntity(String entity) 
	throws RelationException 
	{
		entity = entity.toLowerCase();
		
		if (!entityContainers.containsKey(entity))
			throw new RelationException("No such entity with name '" + entity + "' found");		
		
		entityContainers.get(entity).removeAllAgents();
	}
	
	/***
	 * Frees an agent; removing every existing relation with an entity.
	 */
	@Override
	public void freeAgent(String agent) 
	throws RelationException 
	{
		if (!agentContainers.containsKey(agent))
			throw new RelationException("No such agent with name '" + agent + "' found");		
		
		agentContainers.get(agent).removeAllEntities(); // Free the agent
	}

	/***
	 * Removes a specific relation between an agent and an entity.
	 */
	@Override
	public void freePair(String agent, String entity) 
	throws RelationException 
	{
		entity = entity.toLowerCase();
		
		if (!agentContainers.containsKey(agent))
			throw new RelationException("No such agent with name '" + agent + "' found");
		if (!entityContainers.containsKey(entity))
			throw new RelationException("No such entity with name '" + entity + "' found");
		if (agentContainers.get(agent).getEntity(entity) == null)
			throw new RelationException("The agent '" + agent + "' is not associated with the entity '" + entity + "'");
		
		entityContainers.get(entity).removeAgent(agent); // The entity no longer 'knows' the agent
		agentContainers.get(agent).removeEntity(entity); // The agent no longer 'knows' the entity
	}
	
	/***
	 * Returns a HashSet containing the names of all entities which are associated with an agent.
	 */
	@Override
	public HashSet<String> getAssociatedEntities(String agent)
	throws AgentException 
	{
		if (!agentContainers.containsKey(agent))
			throw new AgentException("No such agent with name '" + agent + "' found");
		
		HashSet<String> result = new HashSet<String>();
		for (EntityContainer entityContainer : agentContainers.get(agent).getEntities())
			result.add(entityContainer.getName());
		
		return result;
	}

	/***
	 * Returns a HashSet containing the names of all agents which are associated with an entity.
	 */
	@Override
	public HashSet<String> getAssociatedAgents(String entity)
	throws EntityException 
	{
		entity = entity.toLowerCase();
		
		if (!entityContainers.containsKey(entity))
			throw new EntityException("No such entity with name '" + entity + "' found");
		
		HashSet<String> result = new HashSet<String>();
		for (AgentContainer agentContainer : entityContainers.get(entity).getAgents())
			result.add(agentContainer.getName());
		
		return result;
	}
	
	/***
	 * Returns a LinkedList containing the names of all free entities.
	 */
	@Override
	public LinkedList<String> getFreeEntities() 
	{
		LinkedList<String> result = new LinkedList<String>();
		for (EntityContainer entityContainer : entityContainers.values())
			if (entityContainer.getAgents().size() == 0)
				result.add(entityContainer.getName());
		
		return result;
	}

	/***
	 * Returns the name of the class of an entity. 
	 */
	@Override
	public String getType(String entity) 
	throws EntityException 
	{
		entity = entity.toLowerCase();
		
		return entityObjects.get(entity).getClass().getCanonicalName();
	}
	
	/***
	 * Performs a specific action for an agent for all the given entities.
	 */
	@Override
	public LinkedList<Percept> performAction(String agent, Action action, String... entities) 
	throws ActException, NoEnvironmentException 
	{
		if (!agentContainers.containsKey(agent))
			throw new ActException("No such agent with name '" + agent + "' found");
		if (!isConnected())
			throw new NoEnvironmentException("The environment is not connected");
		if (agentContainers.get(agent).getEntities().size() == 0)
			throw new ActException("The agent " + agent + " is has no entities assigned to it");
		
		if (entities == null || entities.length == 0)
		{
			for (EntityContainer entityContainer : agentContainers.get(agent).getEntities())
			{
				MSAgent msAgent = entityObjects.get(entityContainer.getName());
				
				try
				{//System.out.println(action + " :: " + msAgent);
					MSAgentEnvironment.performMSAgentAction(action, msAgent);
				}
				catch (MSAgentException msae) { throw new ActException(msae.getMessage()); }
			}
		}
		else
		{
			for (String entity : entities)
			{
				entity = entity.toLowerCase();
				MSAgent msAgent = entityObjects.get(agentContainers.get(agent).getEntity(entity));
			
				try
				{
					MSAgentEnvironment.performMSAgentAction(action, msAgent);
				}
				catch (MSAgentException msae) { throw new ActException(msae.getMessage()); }			
			}
		}

		return new LinkedList<Percept>();
	}
	
	@Override
	public LinkedList<Percept> getAllPercepts(String agent, String... entities)
	throws PerceiveException, NoEnvironmentException 
	{
		return new LinkedList<Percept>();
	}
	
	/***
	 * Returns whether the environment is connected with the environment-interface and whether it is ready for use.
	 * @return a boolean, which is false when the operating system is not a Windows
	 */
	@Override
	public boolean isConnected() 
	{
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	/***
	 * Manages this environment by executing the specified EnvironmentCommand.
	 */
	@Override
	public void manageEnvironment(EnvironmentCommand environmentCommand)
	throws ManagementException, NoEnvironmentException 
	{
		switch (environmentCommand.getType())
		{
			case EnvironmentCommand.START :
				//System.out.println("EnvironmentCommand: START");
				
				if (paused)
				{
					try
					{
						for (MSAgent msAgent : hiddenForPause)
							msAgent.show(false);
						paused = false;
					}
					catch (MSAgentException msae) { throw new ManagementException(msae.getMessage()); }				
				}
				else
				{
					if (!initialized)
					{
						init(new HashSet<String>());
						initializer = environmentCommand;
					}
				}
				break;
			case EnvironmentCommand.KILL :
				//System.out.println("EnvironmentCommand: KILL");
				if (!initialized)
					throw new ManagementException("Could not execute the command 'stop'; this environment has not been initialized");			

				agentContainers.clear();
				entityContainers.clear();
				entityObjects.clear();
				environmentListeners.clear();
				
				initialized = false;
				break;
			case EnvironmentCommand.PAUSE :
				//System.out.println("EnvironmentCommand: PAUSE");
				try
				{
					for (String agent : this.getAgents())
					{
						for (String entity : this.getAssociatedEntities(agent))
						{
							if (entityObjects.get(entity).isVisible())
							{
								hiddenForPause.add(entityObjects.get(entity));
						
								entityObjects.get(entity).hide(false);
							}
						}
					}
					paused = true;
				}
				catch (MSAgentException msae) { throw new ManagementException(msae.getMessage()); }
				catch (AgentException ae) { throw new ManagementException(ae.getMessage()); }
				break;
			case EnvironmentCommand.RESET :	
				//System.out.println("EnvironmentCommand: RESET");
				MSAgent.detach();
				agentContainers.clear();
				entityContainers.clear();
				entityObjects.clear();
				environmentListeners.clear();
				initialized = false;
				manageEnvironment(initializer);
				break;
			case EnvironmentCommand.INIT :
				if (initialized)
					break;
				//System.out.println("EnvironmentCommand: INIT");
				initializer = environmentCommand;
				HashSet<String> args = new HashSet<String>();
				for (Parameter parameter : environmentCommand.getParameters())
				{
					try
					{
						args.add(((Identifier)((Function)parameter).getParameters().get(0)).getValue().toLowerCase());
					}
					catch (ClassCastException cca) { throw new ManagementException("Invalid argument: '" + parameter.toProlog() + "'. Was expecting an identifier"); }
				}
				init(args);
				break;
			case EnvironmentCommand.MISC :
				//System.out.println("EnvironmentCommand: MISC");
				break;
			default:
				throw new ManagementException("No such command with type '" + environmentCommand.getType() + "'. Please use one of the following types: 1, 2, 3, 4, 5, 6");
		}
	}

	/***
	 * Releases this environment by destroying the MSAgents.
	 */
	@Override
	public void release() 
	{
		MSAgent.detach();
	}

	/***
	 * Returns the version of EIS which is necessary for this Environment to run.
	 * @return returns a String holding the version in two decimals
	 */
	@Override
	public String requiredVersion() 
	{
		return "0.2";
	}

	@Override
	public void actionPerformed(MSAgentActionEvent msAgentActionEvent) 
	{
		//System.out.println(msAgentActionEvent);
		try
		{	
			HashSet<String> agents = new HashSet<String>();
			if (msAgentActionEvent.getPerformer() == null) // initial state
				for (String agent : getAgents())
					agents.add(agent);
			else
				agents = getAssociatedAgents(msAgentActionEvent.getPerformer().getCharacter().getName().toLowerCase());
		
			for (String agent : agents)
				for (AgentListener listener : agentContainers.get(agent).getListeners())
					listener.handlePercept(agentContainers.get(agent).getName(), new Percept(msAgentActionEvent.getTypeAsString().toLowerCase(), new Identifier(msAgentActionEvent.getPerformer().getCharacter().getName().toLowerCase())));
		}
		catch(Exception e) { /* To catch the Exceptions when the user killed the agent, while processing an event */ }
	}
}

/***
 * This class is used to actually connect an agent with an entity.
 * 
 * @author Ramon Hagenaars
 */
class AgentContainer
{
	private String name;
	private HashMap<String, EntityContainer> entities;
	private ArrayList<AgentListener> listeners;
	
	/***
	 * Constructor.
	 * @param name the name of the agent
	 */
	public AgentContainer(String name)
	{
		this.name = name;
		entities = new HashMap<String, EntityContainer>();
		listeners = new ArrayList<AgentListener>();
	}
	
	public void addEntity(EntityContainer entity) { entities.put(entity.getName(), entity); }
	public void addListener(AgentListener listener) { listeners.add(listener); }
	
	public void removeListener(AgentListener listener) { listeners.remove(listener); }
	public void removeEntity(String entity) { entities.remove(entity); }
	public void removeAllEntities() { entities.clear(); }
	
	public void setName(String name) { this.name = name; }
	
	public String getName() { return name; }
	public EntityContainer getEntity(String entity) { return entities.get(entity); }
	public ArrayList<AgentListener> getListeners() { return listeners; }
	public Collection<EntityContainer> getEntities() { return entities.values(); }
}

/***
 * This class is used to actually connect an entity with an agent.
 * 
 * @author Ramon Hagenaars
 */
class EntityContainer
{
	private String name;
	private HashMap<String, AgentContainer> agents;
	
	/***
	 * Constructor.
	 * @param name the name of the entity
	 */
	public EntityContainer(String name)
	{
		this.name = name;
		agents = new HashMap<String, AgentContainer>();
	}
	
	public void addAgent(AgentContainer agent) { agents.put(agent.getName(), agent); }
	
	public void removeAgent(String agent) { agents.remove(agent); }
	public void removeAllAgents() { agents.clear(); }
	
	public void setName(String name) { this.name = name; }
	
	public String getName() { return name; }
	public Collection<AgentContainer> getAgents() { return agents.values(); }
}

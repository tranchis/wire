package apapl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import eis.EnvironmentInterfaceStandard;

import apapl.data.APLFunction;
import apapl.data.APLIdent;
import apapl.data.Term;
import apapl.data.Test;
import apapl.deliberation.Deliberation;
import apapl.messaging.Messenger;
import apapl.plans.ChunkPlan;
import apapl.plans.ExecuteModuleAction;
import apapl.plans.ParentPlan;
import apapl.plans.Plan;
import apapl.plans.PlanSeq;
import apapl.program.BeliefUpdate;
import apapl.program.BeliefUpdates;
import apapl.program.Beliefbase;
import apapl.program.Goalbase;
import apapl.program.PCrule;
import apapl.program.PCrulebase;
import apapl.program.PGrule;
import apapl.program.PGrulebase;
import apapl.program.PRrule;
import apapl.program.PRrulebase;
import apapl.program.Planbase;

/**
 * The implementation of a 2APL module. A 2APL module has a mental state
 * consisting of a belief base, goal base, beliefupdates, pg-rules, pr-rules,
 * pc-rules and plans. Messages and events that are sent to the module are
 * stored in this class, as well as the external environments the module can
 * interact with.
 * <p>
 * A module does not implement a strategy for executing itself. The
 * {@link apapl.deliberation.Deliberation} class is responsible for performing
 * the deliberation steps pertaining to the execution strategy of the module.
 * <p>
 * Typically, a module is created by a {@link apapl.APAPLBuilder}.
 * <p>
 * When the internals of this module need be accessed outside the module
 * execution thread, it is necessary to first acquire the monitor on the module
 * to ensure that the deliberation cycle will not proceed during the access.<br>
 * <p>
 * Access to the module internals outside the module execution thread should be
 * therefore always wrapped in the synchronized block. For example: <blockquote>
 * 
 * <pre>
 * APLModule module;
 * synchronized (module) {
 *     String s = module.getPlanbase().toString();
 * }
 * 
 * System.out.println(&quot;Module's planbase: &quot; + s);
 * </pre>
 * 
 * </blockquote>
 * <p>
 * Note: When invoking one of the listeners' methods from module execution
 * thread, it is crucial to keep in mind that they are typically handled by GUI.
 * The GUI updates are usually implemented to be executed on event dispatching
 * thread (by means of {@link javax.swing.SwingUtilities#invokeAndWait}) and may
 * need to obtain lock on the module. Therefore, it must be ensured at all times
 * that callback methods are invoked only when module's monitor is free. They
 * may not be called from synchronized blocks or methods, as it can easily lead
 * to dead locks.
 */
public class APLModule {
    /** List of received internal events. */
    private ArrayList<Integer> iEvents = new ArrayList<Integer>();
    /** List of received external events. */
    private LinkedList<APLFunction> eEvents = new LinkedList<APLFunction>();
    /** List of environments where this module operates in. */
    private HashMap<String, EnvironmentInterfaceStandard> envs;
    /** Module's deliberation cycle. */
    private Deliberation delib;
    /** The messenger used for sending messages. */
    private Messenger messenger;
    /** The full composed name of the module. */
    private String name;
    /** Multi-agent system in which the module resides. */
    private APLMAS mas;
    /** Parent module. */
    private APLModule parent;
    /** Stopping condition. */
    private Test stoppingCond;

    // The mental state of the module
    private Beliefbase beliefs;
    private Goalbase goals;
    private BeliefUpdates beliefupdates;
    private PGrulebase pgrules;
    private PRrulebase prrules;
    private PCrulebase pcrules;
    private Planbase plans;

    // Denoting whether currently performing an external action
    private boolean inEnvironment = false;

    private Logger logger = null;
    
    /**
     * Constructs an empty module. Typically, the bases are filled while parsing
     * the 2APL module specification code.
     */
    public APLModule() {
        this.beliefs = new Beliefbase();
        this.goals = new Goalbase();
        this.beliefupdates = new BeliefUpdates();
        this.pgrules = new PGrulebase();
        this.pcrules = new PCrulebase();
        this.prrules = new PRrulebase();
        this.plans = new Planbase();
        this.envs = new HashMap<String, EnvironmentInterfaceStandard>();
        this.delib = new Deliberation();
        this.stoppingCond = null;
        this.setLogger(new Logger());
    }

    /**
     * Constructs a module based on the parameters passed to the constructor.
     * Typically used when the module is created as a result of cloning.
     * 
     * @param ievents the list of received internal events
     * @param eevents the list of received external events
     * @param envs the list of environments in which this module operates
     * @param delib the module's deliberation cycle
     * @param messenger the messenger used for sending messages
     * @param name the full name of the module
     * @param mas the multi-agent system in which the module resides
     * @param parent the parent module
     * @param stoppingCond the stopping condition
     * @param beliefs the beliefbase
     * @param goals the goalbase
     * @param beliefupdates the belief updates
     * @param pgrules the PG-rulebase
     * @param prrules the PR-rulebase
     * @param pcrules the PC-rulebase
     * @param plans the planbase
     * @param inEnvironment the flag denoting whether module is currently
     *        performing an external action
     */
    public APLModule(ArrayList<Integer> ievents,
            LinkedList<APLFunction> eevents,
            HashMap<String, EnvironmentInterfaceStandard> envs,
            Deliberation delib, Messenger messenger, String name, APLMAS mas,
            APLModule parent, Test stoppingCond, Beliefbase beliefs,
            Goalbase goals, BeliefUpdates beliefupdates, PGrulebase pgrules,
            PRrulebase prrules, PCrulebase pcrules, Planbase plans,
            boolean inEnvironment) {
        super();
        iEvents = new ArrayList<Integer>(ievents);
        eEvents = new LinkedList<APLFunction>(eevents);
        this.envs = new HashMap<String, EnvironmentInterfaceStandard>(envs);
        this.delib = delib.clone();
        this.messenger = messenger;
        this.name = new String(name);
        this.mas = mas;
        this.parent = parent;
        this.stoppingCond = stoppingCond != null ? stoppingCond.clone() : null;
        this.beliefs = beliefs.clone();
        this.goals = goals.clone();
        this.beliefupdates = beliefupdates.clone();
        this.pgrules = pgrules.clone();
        this.prrules = prrules.clone();
        this.pcrules = pcrules.clone();
        this.plans = plans.clone();
        this.inEnvironment = inEnvironment;
        this.setLogger(new Logger());
    }

    /**
     * Sets the messenger that this module uses for its communication (sending
     * and receiving messages). The messenger should not be changed at runtime
     * and should be the same for all modules running in this multi-agent
     * system.
     * 
     * @param msgr the messenger to be used by this module
     */
    public void setMessenger(Messenger msgr) {
        this.messenger = msgr;
    }

    /**
     * Sets the name by which the module is uniquely identified. The name of the
     * module should be set only by the parser or clone action.
     * 
     * @param name the name the module should be identified by
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the module change listener. The change listener is an object
     * notified when mental state of the module has changed.
     * 
     * @param listener the listener to be added
     **/
    public void addModuleChangeListener(ModuleChangeListener listener) {
        delib.addModuleChangeListener(listener);
    }

    /**
     * Sets whether the module is performing an action in an external
     * environment. This method should only be used by the class responsible for
     * executing external actions (e.g. {@link apapl.plans.ExternalAction}).
     * 
     * @param inEnvironment <code>true</code> if the module is performing an
     *        action in the environment, <code>false</code> otherwise.
     */
    public void inEnvironment(boolean inEnvironment) {
        this.inEnvironment = inEnvironment;
    }

    /**
     * Determines whether is this module performing an action in the external
     * environment. This method can be used to check whether the module is still
     * performing some action (note that external actions might block) when
     * taking the module down.
     * 
     * @return <code>true</code> if the module is performing an action in the
     *         environment, <code>false</code> otherwise.
     */
    public boolean inEnvironment() {
        return inEnvironment;
    }

    /**
     * Performs one deliberation step. The execution of this step is implemented
     * by the {@link apapl.deliberation.Deliberation} cycle of the module. This
     * method should only be called by an {@link apapl.Executor} implementing a
     * specific execution strategy for a multi-agent system.
     */
    public void step() {
        delib.step(this);
    }

    /**
     * Adds an environment with which the module can interact. The module can
     * access these environments by the name. Performing an action in the
     * environment is to write in 2APL code: @name(action, returnvalue).
     * 
     * @param name the name by which the module can access the environment
     * @param env the environment
     */
    public void addEnvironmentInterface(String name,
            EnvironmentInterfaceStandard env) {
        envs.put(name, env);
    }
    
    /**
     * Removes an environment with which the module can interact.
     * 
     * @param name the name of the environment to remove
     */
    public void removeEnvironmentInterface(String name) {
        envs.remove(name);
    }

    /**
     * Returns the environment identified by <code>name</code>.
     * 
     * @param name the name of the environment
     * @return the environment identified by name, <code>null</code> if the
     *         environment is not linked to the module.
     */
    public EnvironmentInterfaceStandard getEnvironmentInterface(String name) {
        return (envs.get(name));
    }

    /**
     * This method is invoked to notify that an external event is received.
     * External events are sent to the module by external environments. This
     * method merely stores the event. Any action that should be taken on the
     * basis of an event should be implemented by the deliberation step.
     * 
     * @param a the received event
     * @param env the source environment where this events comes from
     */
    public void notifyEEevent(APLFunction a, String env) {
        synchronized (eEvents) {
            eEvents.offer(new APLFunction("event", a, new APLIdent(env)));
        }
        wakeUp();
    }

    /**
     * This method is invoked to notify that an internal event is thrown. An
     * internal event is thrown when the execution of an action fails. This
     * method merely stores the event. Any action that should be taken on the
     * basis of an event should be implemented by the deliberation step.
     * 
     * @param id The identifier of the plan that caused this event.
     */
    public void notifyIEvent(int id) {
        iEvents.add(new Integer(id));
    }

    /**
     * Clears the internal event base.
     */
    public void clearIEvents() {
        iEvents = new ArrayList<Integer>();
    }

    /**
     * Returns full name of this module.
     * 
     * @return the name of the module.
     */
    public String toString() {
        return getName();
    }

    /**
     * Returns short module name relative to the parent module.
     * 
     * @return the relative name of the module
     */
    public String getRelativeName() {
        int i;
        if ((i = name.lastIndexOf(".")) != -1) {
            return name.substring(i + 1);
        } else {
            return name;
        }
    }

    /**
     * Returns the local name of the module. The name is local with respect to
     * the current agent platform.
     * 
     * @return the name of the module
     */
    public String getLocalName() {
        return getName();
    }

    /**
     * Returns the full composed name by which is this module identified.
     * 
     * @return the name of the module
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the name of the agent this module belongs to. The agent name is
     * the same as the name of the agent's main module.
     * 
     * @return name of the agent
     */
    public String getAgentName() {
        int i = name.indexOf('.');
        if (i != -1)
            return name.substring(0, i);
        else
            return name;
    }

    /**
     * Returns the internals events that are received.
     * 
     * @return the list of internal events
     */
    public ArrayList<Integer> getIEvents() {
        return iEvents;
    }

    /**
     * Returns the external events that are received.
     * 
     * @return the list of external events
     */
    public LinkedList<APLFunction> getEEvents() {
        return eEvents;
    }

    /**
     * Returns the messenger the module uses for communication.
     * 
     * @return the messenger
     */
    public Messenger getMessenger() {
        return messenger;
    }

    /**
     * Returns the belief base of the module.
     * 
     * @return the belief base
     */
    public Beliefbase getBeliefbase() {
        return beliefs;
    }

    /**
     * Returns the goal base of the module.
     * 
     * @return the goal base
     */
    public Goalbase getGoalbase() {
        return goals;
    }

    /**
     * Returns the belief updates of the module.
     * 
     * @return the belief updates
     */
    public BeliefUpdates getBeliefUpdates() {
        return beliefupdates;
    }

    /**
     * Returns the plan base of the module.
     * 
     * @return the plan base
     */
    public Planbase getPlanbase() {
        return plans;
    }

    /**
     * Returns the PG-rules of the module.
     * 
     * @return the PG-rules base
     */
    public PGrulebase getPGrulebase() {
        return pgrules;
    }

    /**
     * Returns the PR-rules of the module.
     * 
     * @return the PR-rules base
     */
    public PRrulebase getPRrulebase() {
        return prrules;
    }

    /**
     * Returns the PC-rules of the module.
     * 
     * @return the PC-rules base
     */
    public PCrulebase getPCrulebase() {
        return pcrules;
    }

    /**
     * Checks the module and generates warnings if any. Checks, for instance,
     * for unbound variables.
     * 
     * @return the list of warning messages
     */
    public LinkedList<String> check() {
        LinkedList<String> warnings = new LinkedList<String>();

        for (PlanSeq ps : plans)
            ps.checkVars(warnings);

        for (PlanSeq ps : plans)
            for (Plan p : ps)
                p.checkPlan(warnings, this);

        for (PCrule r : pcrules)
            for (Plan p : r.getBody())
                p.checkPlan(warnings, this);

        for (PGrule r : pgrules)
            for (Plan p : r.getBody())
                p.checkPlan(warnings, this);

        for (PRrule r : prrules)
            for (Plan pv : r.getBody())
                if (pv instanceof Plan)
                    ((Plan) pv).checkPlan(warnings, this);

        for (BeliefUpdate b : beliefupdates)
            b.checkVars(warnings);

        for (PCrule r : pcrules)
            r.checkVars(warnings);

        for (PGrule r : pgrules)
            r.checkVars(warnings);

        for (PRrule r : prrules)
            r.checkVars(warnings);

        return warnings;
    }

    /**
     * Returns the multi-agent system in which the module resides.
     * 
     * @return the multi-agent system
     */
    public APLMAS getMas() {
        return mas;
    }

    /**
     * Sets the multi-agent system assigned to the module. The reference to
     * multi-agent system is used to create, execute or release child modules.
     * 
     * @param mas the multi-agent system to set
     */
    public void setMas(APLMAS mas) {
        this.mas = mas;
    }

    /**
     * Gets the parent of the current module. The parent is the module that has
     * created this module.
     * 
     * @return the parent module
     */
    public APLModule getParent() {
        return parent;
    }

    /**
     * Sets the parent of the current module. The parent is the module that has
     * created this module.
     * 
     * @param parent the parent to set
     */
    public void setParent(APLModule parent) {
        this.parent = parent;
    }

    /**
     * Invoked by the multi-agent system to indicate that a child module has
     * satisfied the stopping condition and this module will become active
     * again.
     * 
     * @param module the child module
     * @param theta the substitution that satisfied stopping condition
     */
    public void controlReturned(APLModule module, SubstList<Term> theta) {
        // Find the plan that executed the module and remove execute action
        for (PlanSeq ps : getPlanbase()) {
            LinkedList<Plan> plans = ps.getPlans();
            if (plans.size() > 0) {
                Plan p = plans.getFirst();
                ParentPlan parent = ps;

                // Isn't ExecuteModuleAction wrapped in an atomic plan?
                // Implemented as while loop to handle recursive atomic plans
                // correctly.
                while (p instanceof ChunkPlan) {
                    parent = (ChunkPlan) p;
                    p = ((ChunkPlan) p).getPlans().getFirst();
                }

                if (p instanceof ExecuteModuleAction) {
                    // Has this plan executed the returning module?
                    if (((ExecuteModuleAction) p).getExecutedModule() == module) {
                        // Remove the execute action
                        parent.removeFirst();

                        if (!ps.isEmpty()) {
                            // Apply substitution to the rest of the plan
                            ps.applySubstitution(theta);
                        } else {
                            // If the execute action was the last one, remove
                            // plan
                            getPlanbase().removePlan(ps);
                        }

                        break;
                    }
                }
            }
        }
    }

    /**
     * Sets the stopping condition. Once the stopping condition is satisfied,
     * the execution control is returned back to the parent module.
     * 
     * @param stoppingCond the stopping condition
     */
    public void setStoppingCond(Test stoppingCond) {
        this.stoppingCond = stoppingCond;
    }

    /**
     * Returns the stopping condition assigned to this module.
     * 
     * @return the stopping condition
     */
    public Test getStoppingCond() {
        return stoppingCond;
    }

    /**
     * Returns the environments to which has the module access.
     * 
     * @return the map of environment identifiers and environments
     */
    public HashMap<String, EnvironmentInterfaceStandard> getEnvs() {
        return envs;
    }

    /**
     * Determines whether this module is active in the multi-agent system. Only
     * active modules can be executed by the executor.
     * 
     * @return <code>true</code> if this module is active, <code>false</code> `
     *         otherwise.
     */
    public boolean isActive() {
        return getMas().isActive(this);
    }

    /**
     * Resets the deliberation cycle. The consequence of this action is that the
     * next deliberation step will be 'Apply PG-Rules'.
     */
    public void resetDeliberationCycle() {
        delib.reset();
    }

    /**
     * Determines whether this module can be accessed (executed, tested or
     * updated) by another module. Module can be only accessed from its
     * ancestors.
     * 
     * @param module the module testing the access
     * @return <code>true</code> if the access is allowed, <code>false</code>
     *         otherwise
     */
    public boolean isAccessibleFrom(APLModule module) {
        // composed module names already encode this information
        return name.startsWith(module.getName());
    }

    /**
     * Creates cloned instance of this module.
     * 
     * @return the clone of this module
     */
    public APLModule clone() {
        APLModule module = new APLModule(iEvents, eEvents, envs, delib,
                messenger, name, mas, parent, stoppingCond, beliefs, goals,
                beliefupdates, pgrules, prrules, pcrules, plans, inEnvironment);
        return module;
    }

    /**
     * Sleeps the execution of this module.
     */
    public void sleep() {
        getMas().sleep(this);
    }

    /**
     * Wakes up this module.
     */
    public void wakeUp() {
        getMas().wakeUp(this);
    }

    /**
     * Determines the length of the external events queue.
     * 
     * @return the number of unprocessed external events
     */
    public int getEEventCount() {
        synchronized (eEvents) {
            return eEvents.size();
        }
    }

    /**
     * Determines the length of the message queue.
     * 
     * @return the number of unprocessed messages
     */
    public synchronized int getMessageCount() {
        return messenger.getMessageCount(getName());
    }

	/**
	 * Parses additional beliefs from a prolog-file and adds them to the belief-base.
	 * @param beliefsFile
	 * @param shadow
	 * @throws IOException 
	 */
	void addAdditionalBeliefs(File beliefsFile, boolean shadow) throws IOException {
		
		this.beliefs.addFromFile(beliefsFile,shadow);
		
	}
	
	public void setLogger(Logger logger) {
		
		this.logger = logger;
		this.beliefs.setLogger(logger);
		this.goals.setLogger(logger);	
		
	}
	
	public Logger getLogger() {
		
		return logger;
		
	}
	
}
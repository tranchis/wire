package apapl;

import java.util.*;
import java.io.*;

import eis.AgentListener;
import eis.EnvironmentInterfaceStandard;
import eis.exceptions.AgentException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.iilang.EnvironmentCommand;
import eis.iilang.Percept;

import apapl.messaging.*;
import apapl.parser.ParseModuleException;
import apapl.parser.ParsePrologException;
import apapl.data.*;

/**
 * Implements the 2APL multi-agent system (MAS). The MAS keeps track of all the
 * modules that reside in the MAS, the environments in which modules perform
 * actions, and the files by which the modules are specified. The MAS has all
 * the functionality to execute a multi-agent system. The specific strategy by
 * which the modules are executed is implemented by the {@link apapl.Executor}.
 */
public class APLMAS implements MessageListener {
    /** The list of active modules that reside in this MAS. */
    private List<APLModule> activeModules;
    /** The list of inactive modules that reside in this MAS. */
    private List<APLModule> inactiveModules;

    /** The files that are used per module. */
    private HashMap<APLModule, LinkedList<File>> modulefiles;
    /** The environments used in this MAS. (needed for cleanup at destruction) */
    private LinkedList<EnvironmentInterfaceStandard> environmentInterfaces;
    /** The listeners used to forward percepts from environments to modules. 
     *  The key is a concatenation of the module name and the environment name. */
    private HashMap<String, AgentListener> eisAgentListeners;
    
    /** The executor used to execute the modules. */
    private Executor executor;
    /** The messenger used to send messages. */
    private Messenger msgr;
    /**
     * Directory in which module specifications are located. Typically the
     * directory with MAS specification file.
     */
    private File moduleSpecDir;
    /**
     * Listeners to be informed when a module is created, released, activated or
     * deactivated.
     */
    private ArrayList<MASChangeListener> listeners;

    /**
     * Constructs an empty MAS. Initially, a MAS is populated by modules created
     * by a parser.
     * 
     * @param moduleSpecDir directory in which module specifications will be
     *        searched for
     * @param msgr the messenger used by the modules for communication
     * @param exec the executor implementing the strategy for executing the
     *        modules
     */
    public APLMAS(File moduleSpecDir, Messenger msgr, Executor exec) {
        activeModules = new LinkedList<APLModule>();
        inactiveModules = new LinkedList<APLModule>();
        modulefiles = new HashMap<APLModule, LinkedList<File>>();
        environmentInterfaces = new LinkedList<EnvironmentInterfaceStandard>();
        eisAgentListeners = new HashMap<String, AgentListener>();
        listeners = new ArrayList<MASChangeListener>();
        

        this.moduleSpecDir = moduleSpecDir;
        this.msgr = msgr;
        this.executor = exec;

        this.msgr.addMessageListener(this);
    }

    /**
     * Adds module to this MAS.
     * 
     * @param module the module to be added
     * @param files the files by which is this module specified
     * @param active determines whether the module should be initially active
     */
    public void addModule(APLModule module, LinkedList<File> files,
            boolean active) {
        if (active) {
            activeModules.add(module);
            executor.addModule(module);
        } else {
            inactiveModules.add(module);
        }

        modulefiles.put(module, files);
        msgr.addModule(module.getName());
        module.setMessenger(msgr);
    }

    /**
     * Removes module from this MAS.
     * 
     * @param module the module that is performing the release action
     * @param identifier the identifier of the module to be released
     * @throws ModuleAccessException if the module name cannot be resolved or
     *         module to be removed is active
     */
    public void releaseModule(APLModule module, String identifier)
            throws ModuleAccessException {
        APLModule releasedModule = getModule(module, identifier);

        if (isActive(releasedModule))
            throw new ModuleAccessException(releasedModule.getName(),
                    "Cannot release active module.");

        // Notify the MAS Change Listeners
        for (MASChangeListener mcl : listeners) {
            mcl.moduleReleased(releasedModule);
        }

        modulefiles.remove(module);
        inactiveModules.remove(releasedModule);
        
        // Detach module from all its environments
        Set<String> envs = new HashSet<String>(module.getEnvs().keySet());
        for(String env : envs)
            detachModuleFromEnvironment(releasedModule, env);
        
        msgr.removeModule(releasedModule.getName());
    }

    /**
     * Moves module from set of inactive modules to the set of active modules.
     * 
     * @param module the module to be activated
     */
    public void activateModule(APLModule module) {
        // Removes module from inactive list. No effect if not there.
        inactiveModules.remove(module);
        activeModules.add(module);

        module.resetDeliberationCycle();

        // Notify the MAS Change Listeners
        for (MASChangeListener mcl : listeners) {
            mcl.moduleActivated(module);
        }
    }

    /**
     * Moves module from set of active modules to the set of inactive modules.
     * 
     * @param module the module to be deactivated
     */
    public void deactivateModule(APLModule module) {
        // Removes module from active list. No effect if not there.
        activeModules.remove(module);
        inactiveModules.add(module);

        // Notify the MAS Change Listeners
        for (MASChangeListener mcl : listeners) {
            mcl.moduleDeactivated(module);
        }
    }

    /**
     * Adds an environment interface to the MAS.
     * 
     * @param ei the environment interface to be added
     */
    public void addEnvironmentInterface(EnvironmentInterfaceStandard ei) {
        environmentInterfaces.add(ei);
    }
    
    /**
     * Attaches module to the environment. This includes:
     * <ul>
     *  <li> Registering the module in the environment.
     *  <li> Granting access to the environment from the module using the given name.
     *  <li> Establishing call-backs from the environment to the module.
     * </ul>    
     * 
     * @param module the module to attach
     * @param envName the name of the environment to attach
     * @param env the environment to attach
     */
    public void attachModuleToEnvironment(APLModule module, String envName,
            EnvironmentInterfaceStandard env) {
        try {   
            env.registerAgent(module.getLocalName());

            // Variables used in the AgentListener anonymous subclass
            final String environmentName = envName;
            final APLMAS mas = this;
            
            AgentListener agentListener =              
                new AgentListener() {
                    public void handlePercept(String moduleName, Percept percept) {
                        mas.notifyEEevent(moduleName, environmentName, IILConverter.convert(percept));
                    }               
                };

            
            // Attach callback handler
            env.attachAgentListener(module.getLocalName(), 
                    agentListener
            );    
            
            // Store AgentListener to allow later detachment
            this.eisAgentListeners.put(module.getName() + envName, agentListener);
           

            /* Support for old 2APL environments.
             * Creates an entity for each agent, which is then automatically
             * assigned to the agent. 
             */
                        
            if (env instanceof Environment) {
                ((Environment) env).addAgentEntity(module.getLocalName());
            }
            
        } catch (AgentException e) {
            throw new RuntimeException("Failed to register agent " 
                    + module.getLocalName() + "to environment " + envName + ".");
        }

        // Inform the module about the environment.
        module.addEnvironmentInterface(envName, env);
    }
    
    /**
     * Detaches module from the environment. This includes:
     * <ul>
     *  <li> De-registering the module from the environment.
     *  <li> Removing access to the environment from the module using the given name.
     *  <li> Removing call-backs from the environment to the module.
     * </ul>    
     * 
     * @param module the module to detach
     * @param envName the identifier of the environment
     * @param env the environment
     */
    public void detachModuleFromEnvironment(APLModule module, String envName) {
        try {
            EnvironmentInterfaceStandard env 
                = module.getEnvironmentInterface(envName);
            
            /* Support for old 2APL environments.
             * Creates an entity for each agent, which is then automatically
             * assigned to the agent. 
             */
            if (env instanceof Environment) {
                ((Environment) env).removeAgentEntity(module.getLocalName());
            }   
            
            env.unregisterAgent(module.getLocalName());

            // remove call-backs
            env.detachAgentListener(module.getLocalName(), 
                    this.eisAgentListeners.get(module.getName()+envName));
            
        } catch (AgentException e) {
            throw new RuntimeException("Failed to de-register agent " 
                    + module.getLocalName() + "from environment " + envName + ".");
        }

        module.removeEnvironmentInterface(envName);
    }

    /**
     * Obtains all the files by which is the module specified.
     * 
     * @param module the module to obtain the files for
     * @return the files that specify this module
     */
    public LinkedList<File> getFiles(APLModule module) {
        return (modulefiles.get(module));
    }

    /**
     * Returns all the modules (both active and inactive) that reside in this
     * MAS.
     * 
     * @return the modules in this MAS
     */
    public List<APLModule> getModules() {
        LinkedList<APLModule> allModules = new LinkedList<APLModule>(
                activeModules);
        allModules.addAll(inactiveModules);
        return allModules;
    }

    /**
     * Starts the execution of all modules in this MAS. All the modules will be
     * executed by the execution strategy implemented by the
     * {@link apapl.Executor}.
     */
    public void start() {
    	
    	// start the interfaces
    	this.startEnvironmentInterfaces();
    	
    	// start the executor
        executor.start();
    }

    /**
     * Performs n deliberation steps for all the modules in the MAS.
     * 
     * @param n number of deliberation steps to be executed
     */
    public void step(int n) {
    	
    	// start the interfaces
    	this.startEnvironmentInterfaces();

    	executor.step(n);
    }

    /**
     * Stops the execution of all modules in this MAS.
     */
    public void stop() {

    	// pauses all environment-interfaces
    	pauseEnvironmentInterfaces();
    	
    	// stop the executor
        executor.stop();
    }

    /**
     * Starts the execution of one module.
     * 
     * @param module the module to start
     */
    public void start(APLModule module) {
  
    	// start the interfaces
    	this.startEnvironmentInterfaces();

    	executor.start(module);

    }

    /**
     * Performs n deliberation steps on one module.
     * 
     * @param module the module on which the step should be performed
     * @param n the number of deliberation steps to be executed
     */
    public void step(APLModule module, int n) {
 
    	// start the interfaces
    	this.startEnvironmentInterfaces();

    	executor.step(module, n);
    
    }

    /**
     * Stops the execution of one module.
     * 
     * @param module the module to stop.
     */
    public void stop(APLModule module) {
  
    	// TODO pause the interfaces when all modules are paused
    	// this.startEnvironmentInterfaces();

    	executor.stop(module);

    }

    /**
     * Puts the module to sleep. The module's execution thread is suspended. The
     * module wakes up again on incoming message or external event.
     * 
     * @param module module to be put to sleep
     */
    public void sleep(APLModule module) {
        executor.sleep(module);
    }

    /**
     * Wakes up the module that was previously put to sleep.
     * 
     * @param module module to be waken up
     */
    public void wakeUp(APLModule module) {
        if (isActive(module))
            executor.wakeUp(module);
    }

    /**
     * Takes down the MAS. First stops the executor and then calls the takeDown
     * methods of all the modules and environments. This method should be called
     * whenever dereferencing this MAS.
     */
    public void takeDown() {
        // Stop the executor (stop modules)
        executor.stop();

        // Take all environments down
        for (EnvironmentInterfaceStandard e : environmentInterfaces) {
            
        	// send kill command
        	EnvironmentCommand cmd = new EnvironmentCommand(EnvironmentCommand.KILL);
        	try {
				e.manageEnvironment(cmd);
			} catch (ManagementException e1) {
				System.out.println("Environment interface did not accept KILL-command");
			} catch (NoEnvironmentException e1) {
				System.out.println("Environment interface did not accept KILL-command");
			}
        	
        	e.release();
        }

        activeModules = new LinkedList<APLModule>();
        inactiveModules = new LinkedList<APLModule>();
        modulefiles = new HashMap<APLModule, LinkedList<File>>();
        environmentInterfaces = new LinkedList<EnvironmentInterfaceStandard>();
    }

    /**
     * Loads given module specification from file and creates a new module
     * instance in the this MAS.
     * 
     * @param parent the parent of the newly created module
     * @param specification the name of the module specification
     * @param id the relative name of the newly created module
     */
    public void createModule(APLModule parent, String specification, String id)
            throws CreateModuleException {
        APAPLBuilder builder = new APAPLBuilder();
        // Constructs the full composed module name. e.g.
        // mainmodule.parent.child
        String composedId = parent.getName() + '.' + id;

        // Check if the module ID has not been already taken
        if (containsModule(composedId)) {
            throw new CreateModuleException("Module " + composedId
                    + " already exists.");
        }

        Tuple<APLModule, LinkedList<File>> t = null;
        try {
            // Build the module and let it be assigned to the current mas
            t = builder.buildModule(specification, composedId, this);
        } catch (ParseModuleException e) {
            throw new CreateModuleException("Syntax error in " + e.getFile()
                    + ":\n" + e.getMessage());
        } catch (ParsePrologException e) {
            throw new CreateModuleException("Syntax error in " + e.getFile()
                    + ":\n" + e.getMessage());
        }

        // The object that invoked create action is the parent of new module
        t.l.setParent(parent);

        // Inherit access to environments from the parent
        for (Map.Entry<String, EnvironmentInterfaceStandard> entry : parent
                .getEnvs().entrySet()) {
            attachModuleToEnvironment(t.l, entry.getKey(), entry.getValue());
        }

        // Adds to the system as inactive
        this.addModule(t.l, t.r, false);

        // Notify the MAS Change Listeners
        for (MASChangeListener mcl : listeners) {
            mcl.moduleCreated(t.l);
        }
    }

    /**
     * Creates a new module instance by cloning a module which is already
     * present in this MAS.
     * 
     * @param parent the parent of the newly created module
     * @param model the relative name of the module to be cloned
     * @param id the relative name of the newly created module
     */

    public void cloneModule(APLModule parent, String model, String id)
            throws CreateModuleException {

        // Constructs the full composed module name. e.g.
        // mainmodule.parent.child
        String composedId = parent.getName() + '.' + id;

        // Check if the module ID has not been already taken
        if (containsModule(composedId)) {
            throw new CreateModuleException("Module " + composedId
                    + " already exists.");
        }
        try {
            APLModule modelModule = getModule(parent, model);
            APLModule module = modelModule.clone();
            module.setName(composedId);

            // Copy the list of the source files from the model module
            Tuple<APLModule, LinkedList<File>> t = new Tuple<APLModule, LinkedList<File>>(
                    module, new LinkedList<File>(modulefiles.get(modelModule)));

            // Adds to the system as inactive
            this.addModule(t.l, t.r, false);

            // Notify the MAS Change Listeners
            for (MASChangeListener mcl : listeners) {
                mcl.moduleCreated(t.l);
            }

        } catch (ModuleAccessException e) {
            throw new CreateModuleException("Unable to access module " + model
                    + ".");
        }
    }

    /**
     * Returns the directory in which are the module specification files
     * expected. Typically, the directory with the MAS specification file.
     * 
     * @return the moduleSpecDir the directory with the module specifications
     */
    public File getModuleSpecDir() {
        return moduleSpecDir;
    }

    /**
     * Sets the MAS change listener. The MAS change listener is an object
     * notified when a module is created, released, activated or deactivated.
     * 
     * @param listener the listener to be set
     */
    public void addMASChangeListener(MASChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Resolves the relative local module identifier and returns the instance of
     * the module if it exists in the system. If <code>moduleId</code> is null,
     * the method returns the instance of the calling module.
     * 
     * @param module the module to which is the given module identifier relative
     * @param moduleId the relative module name
     * @return resolved instance of the module, null if not found
     * @throws ModuleAccessException if the module name cannot be resolved
     */
    public APLModule getModule(APLModule module, String moduleId)
            throws ModuleAccessException {
        if (moduleId == null)
            return module;

        String fullName;
        if (module != null)
            fullName = module.getName() + "." + moduleId;
        else
            fullName = moduleId;

        for (APLModule m : getModules()) {
            if (m.getName().equals(fullName)) {
                return m;
            }
        }

        // Module with the given name does not exist
        throw new ModuleAccessException(fullName,
                "Cannot resolve module name: " + fullName);
    }

    /**
     * Hands control from the parent module over to the child module.
     * 
     * @param parent the module that performs the execute module action
     * @param child the module to be executed
     */
    public void executeChildModule(APLModule parent, APLModule child) {
        activateModule(child);
        deactivateModule(parent);
        executor.passControl(parent, child);
    }

    /**
     * Hands control from the executed module back to the parent.
     * 
     * @param module the child module, which is about to return
     */
    public void returnFromModule(APLModule module, SubstList<Term> theta) {
        // No action if the module is the main module
        if (module.getParent() == null)
            return;

        // Inform parent that the child module has finished
        module.getParent().controlReturned(module, theta);
        activateModule(module.getParent());
        deactivateModule(module);
        executor.passControl(module, module.getParent());
    }

    /**
     * Determines if the given module is in the set of active modules.
     * 
     * @param module the module to check
     * @return <code>true</code> is the module is active, <code>false</code>
     *         otherwise
     */
    public boolean isActive(APLModule module) {
        return activeModules.contains(module);
    }

    /**
     * Determines whether this MAS contains module with the given identifier.
     * 
     * @param moduleId the full identifier of module
     * @return <code>true</code> if the system contains the module,
     *         <code>false</code> otherwise
     */
    public boolean containsModule(String moduleId) {
        for (APLModule m : getModules()) {
            if (m.getName().equals(moduleId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds the listener that will be informed when module has started, stopped,
     * has been put to sleep or waken up. The listeners have to be fully set up
     * before any modules are added to the executor.
     * 
     * @param listener the listener to add
     */
    public void addMASExecutionListener(MASExecutionListener listener) {
        executor.addMASExecutionListener(listener);
    }

    /**
     * Listens for the sent messages in order to wake up possible sleeping
     * recipients.
     * 
     * @param message the message being sentS
     */
    public void messageSent(APLMessage message) {
        try {
            APLModule module = this.getModule(null, message.getReceiver());
            if (isActive(module))
                wakeUp(module);

        } catch (ModuleAccessException e) {
            return;
        }
    }
    
    /**
     * Notifies a particular module that an external event has been generated by an environment.
     * 
     * @paran moduleName the module to receive the event
     * @param a the received event
     * @param env the environment that generated the event
     */
    public void notifyEEevent(String moduleName, String env, APLFunction a) {
        
    	try {
            APLModule module = this.getModule(null, moduleName);            
            module.notifyEEevent(a, env);
            
        } catch (ModuleAccessException e) {
            return;
        }
    }

    private void startEnvironmentInterfaces() {

    	// pauses the interfaces
    	EnvironmentCommand cmd = new EnvironmentCommand(EnvironmentCommand.START);
    	for( EnvironmentInterfaceStandard ei : environmentInterfaces ) {
    		
    		try {
				ei.manageEnvironment(cmd);
			} catch (ManagementException e) {
				System.out.println("Starting environment not supported");
				System.out.println( e.getMessage() );
			} catch (NoEnvironmentException e) {
				System.out.println("Starting environment not supported");
				System.out.println("No environment");
			}
    		
    	}

    }
    
    private void pauseEnvironmentInterfaces() {

    	// pauses the interfaces
    	EnvironmentCommand cmd = new EnvironmentCommand(EnvironmentCommand.PAUSE);
    	for( EnvironmentInterfaceStandard ei : environmentInterfaces ) {
    		
    		try {
				ei.manageEnvironment(cmd);
			} catch (ManagementException e) {
				e.printStackTrace();
			} catch (NoEnvironmentException e) {
				e.printStackTrace();
			}
    		
    	}

    }

}

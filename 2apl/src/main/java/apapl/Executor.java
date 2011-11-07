package apapl;

import java.util.Set;

/**
 * Defines an interface for the specific implementation of execution strategy of
 * multi-agent system. The executor takes set of modules and executes
 * deliberation cycle on each of them according to a specific execution strategy.
 * 
 * @see apapl.MultiThreadedExecutor
 */
public interface Executor
{
	/**
	 * Adds module to the set of executed modules.
	 * 
	 * @param module the module to be added
	 */
	public void addModule(APLModule module);

	/**
	 * Removes module from the set of executed modules.
	 * 
	 * @param module the module to be removed
	 */
	public void removeModule(APLModule module);

	/**
	 * Returns set of modules executed by this executor.
	 * 
	 * @return the set of executed modules
	 */
	public Set<APLModule> getModules();

	/**
	 * Adds the listener that will be informed when module started, stopped, was
	 * put to sleep or wake up. The listeners have to be fully set up before any
	 * modules are added to the executor.
	 * 
	 * @param listener listener to add
	 */

	public void addMASExecutionListener(MASExecutionListener listener);

	/**
	 * Starts the execution of all modules.
	 */
	public void start();

	/**
	 * Performs n deliberation steps on all modules.
	 * 
	 * @param n number of steps to execute
	 */
	public void step(int n);

	/**
	 * Stops the execution of all modules.
	 */
	public void stop();

	/**
	 * Starts the execution of one module.
	 * 
	 * @param module the module to start
	 */
	public void start(APLModule module);

	/**
	 * Performs n deliberation steps on one module.
	 * 
	 * @param module the module to step
	 * @param n the number of steps to execute
	 */
	public void step(APLModule module, int n);

	/**
	 * Stops the execution of one module.
	 * 
	 * @param module the module to stop
	 */
	public void stop(APLModule module);

	/**
	 * Hands over execution control from one module to another.
	 * 
	 * @param sourceModule the module that looses execution control
	 * @param destModule the module that gains execution control
	 */
	public void passControl(APLModule sourceModule, APLModule destModule);

	/**
	 * Makes module to go to sleeping mode.
	 * 
	 * @param module the module to be put to sleep
	 */
	public void sleep(APLModule module);

	/**
	 * Wakes one of the modules up.
	 * 
	 * @param module the module to wake up
	 */
	public void wakeUp(APLModule module);
}

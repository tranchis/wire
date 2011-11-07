package apapl;

/**
 * Defines an interface for an object which listens for changes of the
 * multi-agent system execution state. The object is informed when a module
 * starts executing, when its stops, when it is put to sleep and when it wakes
 * up from sleep.
 */
public interface MASExecutionListener
{
	/**
	 * Invoked before module starts executing. After this method is invoked, the
	 * deliberation on the module will start. Other threads should not attempt
	 * to access its internal state.
	 * 
	 * @param module the module that will start
	 */
	public void moduleWillStart(APLModule module);

	/**
	 * Invoked after module has stopped its deliberation. After this method is
	 * invoked, other threads can access its state again.
	 * 
	 * @param module the module that stopped
	 */
	public void moduleStopped(APLModule module);

	/**
	 * Invoked before module is waken up, that is, the module execution will
	 * be resumed. Other threads should not attempt to access its internal 
	 * state.
	 * 
	 * @param module the module that will wake up
	 */
	public void moduleWillWakeUp(APLModule module);

	/**
	 * Invoked after the module has been put to sleep. After this method is
	 * invoked, other threads can access its state again.
	 * 
	 * @param module the module that has been put to sleep
	 */
	public void modulePutToSleep(APLModule module);

}

package apapl;

/**
 * Defines interface for an object that listens for changes in the multi-agent
 * system configuration. This object is informed when new modules are
 * created, activated, deactivated or released.
 */
public interface MASChangeListener
{

	/**
	 * Invoked when a new module has been created in the multi-agent system.
	 * 
	 * @param module the newly created module
	 */
	public void moduleCreated(APLModule module);

	/**
	 * Invoked right before a module is removed from the multi-agent system.
	 * 
	 * @param module the removed module
	 */
	public void moduleReleased(APLModule module);

	/**
	 * Invoked after a module has been activated.
	 * 
	 * @param module the activated module
	 */
	public void moduleActivated(APLModule module);

	/**
	 * Invoked after a module has been deactivated.
	 * 
	 * @param module the deactivated module
	 */
	public void moduleDeactivated(APLModule module);

}

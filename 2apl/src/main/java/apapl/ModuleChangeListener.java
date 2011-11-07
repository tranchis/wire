package apapl;

import apapl.deliberation.*;

/**
 * Defines an interface for an object that listens for changes in the modules'
 * mental states.
 */
public interface ModuleChangeListener
{
	/**
	 * Invoked when the module's mental state has changed. This is typically
	 * done after each deliberation step.
	 * 
	 * @param module the module of which the mental state has changed
	 * @param result the result of the deliberation step
	 */
	public void moduleChanged(APLModule module, DeliberationResult result);
}

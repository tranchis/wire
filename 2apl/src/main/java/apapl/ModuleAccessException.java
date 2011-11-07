package apapl;

import apapl.data.APLIdent;

/**
 * Signals that the access to the module was denied. A module can
 * only access modules that are both its descendants and inactive.
 */

public class ModuleAccessException extends Exception
{
	// Requested module ID
	private String moduleId;

	/**
	 * Constructs a module access exception.
	 * 
	 * @param moduleId the name of the requested module
	 * @param message context specific message
	 */
	public ModuleAccessException(String moduleId, String message)
	{
		super(message);
		this.moduleId = moduleId;
	}
	
	/**
	 * Constructs a module access exception.
	 * 
	 * @param moduleId the name of the requested module
	 * @param message context specific message
	 */
	public ModuleAccessException(APLIdent moduleId, String message)
	{
		super(message);
		this.moduleId = moduleId.toString();
	}

	/**
	 * Returns the name of the requested module.
	 * 
	 * @return the name of the module
	 */
	public String getModuleId()
	{
		return moduleId;
	}
}

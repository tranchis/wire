package apapl;

/**
 * Signals that an error occurred while loading the environment. Loading an environment
 * can cause a range of errors, such as the non-existence of the environment class, a
 * wrong implementation, etc.
 */
public class LoadEnvironmentException extends Exception
{
  // Name of the environment
  private String name;

  /**
   * Constructs a load environment exception. 
   * 
   * @param name the name of the environment being loaded
   * @param message information about the specific cause
   */
  public LoadEnvironmentException(String name, String message)
  {
	  super(message);
	  this.name = name;
  }

  /**
   * Returns the name of the environment for which this exception was generated.
   * 
   * @return the name of the environment
   */
  public String getName()
  {
	  return name;
  }
}

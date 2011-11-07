package apapl.deliberation;

import java.util.*;
import apapl.program.*;
import apapl.plans.*;
import apapl.*;

/**
 * The result of the deliberation step {@link apapl.deliberation.ExecuteAllPlans}. Contains
 * information about which plans have been executed and which plans failed in their execution.
 */
public class ExecuteAllPlansResult extends ExecutePlansResult
{
	/**
	 * The name of the deliberation step this result belongs to.
	 * 
	 * @return the name of this step
	 */
	public String stepName()
	{
	  return("Execute All Plans");
	}
}

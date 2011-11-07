package apapl.deliberation;

import apapl.*;
import apapl.program.*;
import apapl.data.APLFunction;
import apapl.plans.PlanSeq;
import java.util.ArrayList;

/**
 * Defines an object that implements a deliberation step. Each deliberation step
 * implements an <code>execute</code> method that realizes the actual performance 
 * of the particular step.
 */
public interface DeliberationStep
{
	/**
	 * Executes this deliberation step. 
	 * 
	 * @param module the module that performs this step
	 * @return the result containing information about the execution of the step
	 */
	public DeliberationResult execute( APLModule module );
}

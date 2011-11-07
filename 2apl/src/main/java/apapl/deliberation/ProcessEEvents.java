package apapl.deliberation;

import apapl.*;
import apapl.program.*;
import apapl.data.APLFunction;
import apapl.plans.PlanSeq;
import java.util.ArrayList;

/**
 * The deliberation step in which external events are processed. For each received external
 * event one applicable PC-rule (if any) is applied. External events themselve are stored in
 * the {@link apapl.APLModule}. Events for which no applicable rule is found are discarded.
 */
public class ProcessEEvents implements DeliberationStep
{
	/**
	 * Processes all received external events. 
	 * 	
	 * @return the result of the performance of this deliberation step
	 */
	public DeliberationResult execute( APLModule module )
	{
		ProcessEEventsResult result = new ProcessEEventsResult( );			

		// The function representing the event
		APLFunction e = null;

		// Obtain necessary module bases and module name
	  Beliefbase beliefs = module.getBeliefbase();
		Planbase plans = module.getPlanbase();
		PCrulebase pcrules = module.getPCrulebase();
		String name = module.getLocalName();

		// As long as there are events to be handled
		while( (e = module.getEEvents().poll()) != null )
		{
  		SubstList theta = new SubstList();
  		PCrule rule = null;

  		ArrayList<String> unfreshVars = new ArrayList<String>();
  		try 
			{ rule = pcrules.selectRule(beliefs,e,unfreshVars,theta);
			}
  		catch (NoRuleException exc) {}
			
  		if (rule != null)
  	 	{ result.addProcessed( e, rule, theta );
				PlanSeq p = rule.getBody().clone();
  			p.applySubstitution(theta);
  			plans.addPlan(p);
  		}
			else
			{ result.addUnprocessed( e );
			}
		}

		return( result );
	}
	
	public String toString()
	{
		return "Procces external Events";
	}
}

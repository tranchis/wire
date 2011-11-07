package apapl.deliberation;

import apapl.plans.*;
import apapl.*;
import apapl.program.*;
import apapl.data.*;
import apapl.deliberation.DeliberationResult.InfoMessage;

import java.util.*;

/**
 * The result of the deliberation step {@link apapl.deliberation.ApplyPGrules}. Contains
 * information about which plans have been generated.
 */
public class ApplyPGrulesResult extends DeliberationResult
{
  public ArrayList<PlanSeq> generatedPlans;

  	/**
  	 * Constructs a result object.
  	 * 
  	 * @param generatedPlans the list of plans that have been generated
  	 */
  	public ApplyPGrulesResult(  ArrayList<PlanSeq> generatedPlans )
	{
	  this.generatedPlans = generatedPlans;
	}
  	
	/**
	 * The name of the deliberation step this result belongs to.
	 * 
	 * @return the name of this step
	 */
	public String stepName()
	{
	  return("Apply PG-rules");
	}
	
	/**
	 * Lists the information about this step in text format. In particular,
	 * it lists which plans have been generated.
	 * 
	 * @return a list of InfoMessage objects pertaining to the information
	 */
	public LinkedList<InfoMessage> listInfo()
	{
	  LinkedList<InfoMessage> info = new LinkedList<InfoMessage>();

		if( generatedPlans.isEmpty() )
		{  info.add(new InfoMessage("No PG-rules were applied"));
		}
		else for( PlanSeq p : generatedPlans )
		{ String s = "";
		  PGrule pgrule = p.getActivationRule();
			SubstList<Term> theta = p.getActivationSubstitution();
		  s += "Applied:\n" + pgrule.toString();
			s += "\n\nwith substitution: \n" + theta.toString();

			info.add(new InfoMessage( s ));
		}

	  return( info );
	}
	
	/**
	* Checks whether the execution of the step this result belongs to has changed
	* the state of the module.
	*
	* @return true if the module has been changed while executing this step, false
	*		otherwise
	*/
	public boolean moduleChanged()
	{
		return(!generatedPlans.isEmpty());
	}
}

package apapl.deliberation;

import apapl.*;
import apapl.program.*;
import apapl.data.*;
import apapl.deliberation.DeliberationResult.InfoMessage;
import apapl.plans.*;
import java.util.*;

/**
 * The result of the deliberation step {@link apapl.deliberation.ProcessIEvents}.
 * Contains information about for which events PR-rules were applied. Information
 * about unprocessed events is not stored, as this can be derived from the failure
 * of the execution of plans as listed in {@link apapl.deliberation.ExecutePlansResult}.
 */
public class ProcessIEventsResult extends DeliberationResult
{
  // The PR-rules that were applied with their substitutions
  private LinkedList<PRrule> applied;
	private LinkedList<SubstList<Term>> thetas;
	private LinkedList<SubstList<PlanSeq>> thetaPs;

	/**
	 * Constructs a result object.
	 */
  public ProcessIEventsResult( )
  {
    this.applied = new LinkedList();
		this.thetas = new LinkedList();
		this.thetaPs = new LinkedList();
  }

  /**
   * Adds a PR-rule that has been applied.
   * 
   * @param rule the rule that has been applied
   * @param theta the term substitutions
   * @param thetaP the plan substitutions
   */
  public void addApplied( PRrule rule, SubstList<Term> theta, SubstList<PlanSeq> thetaP )
  {
    applied.add(rule);
    thetas.add(theta);
    thetaPs.add(thetaP);
  }

	/**
	 * Lists the information about this step in text format. In particular,
	 * it lists which PR-rules have been applied.
	 * 
	 * @return a list of strings pertaining to the information
	 */
  public LinkedList<InfoMessage> listInfo()
	{
	  LinkedList<InfoMessage> info = new LinkedList<InfoMessage>();

		if( applied.isEmpty() )
    { info.add( new InfoMessage("No PR-rules were applied"));
    }
		else for( int i = 0; i < applied.size(); i++ )
    { String s = "Applied PR-rule: ";
		  PRrule rule = applied.get(i);
			s += "\n" + rule.toString();
			s += "\n with substitution \n" + thetas.get(i).toString() + " and " + thetaPs.get(i).toString();
				
			info.add( new InfoMessage(s) );
    }

	  return( info );
	}

	/**
	 * The name of the deliberation step this result belongs to.
	 * 
	 * @return the name of this step
	 */
	public String stepName()
	{
	  return("Process Internal Events");
	}
	
	public boolean moduleChanged()
	{
		return(!applied.isEmpty());
	}
}

package apapl.deliberation;

import java.util.*;

import apapl.data.*;
import apapl.deliberation.DeliberationResult.InfoMessage;
import apapl.program.*;
import apapl.*;

/**
 * The result of the deliberation step {@link apapl.deliberation.ProcessEEvents}.
 * Contains information such as which events were processed and for which events 
 * PC-rules were applied.
 */
public class ProcessEEventsResult extends DeliberationResult
{
  // list of received events
  private LinkedList<APLFunction> received;
	// list of PC-rules that were applied (index corresponds to event)
  private LinkedList<PCrule> rules;
	// list of substitutions that were applied (index corresponds to PC-rule)
  private LinkedList<SubstList> thetas;

  /**
   * Constructs a result object.
   */
  public ProcessEEventsResult( )
  {
    this.received = new LinkedList();
		this.rules = new LinkedList();
		this.thetas = new LinkedList();
  }

  /**
   * Adds an event for which no PC-rule is applied.
   * 
   * @param e the event to add
   */
  public void addUnprocessed( APLFunction e )
  {
    received.add(e);
		rules.add(null);
		thetas.add(null);
  }

  /**
   * Adds an event for which a PC-rule is applied.
   * 
   * @param e the event to add
   * @param rule the Pc-rule that was applied for this event
   * @param theta the substitution used in applying the PC-rule
   */
  public void addProcessed( APLFunction e, PCrule rule, SubstList theta )
  {
    received.add(e);
		rules.add(rule);
		thetas.add(theta);
  }

	/**
	 * Lists the information about this step in text format. In particular,
	 * it lists which PC-rules have been applied for which events and which
	 * events were discarded.
	 * 
	 * @return a list of InfoMessage objects pertaining to the information
	 */
  	public LinkedList<InfoMessage> listInfo()
	{
	  LinkedList<InfoMessage> info = new LinkedList<InfoMessage>();

    if( received.isEmpty() )
    { info.add(new InfoMessage("No external events were received"));
    }
    else for(int i = 0; i < received.size(); i++)
		{ String s = "Received:\n" + received.get(i).toString() + "\n";
		  if( rules.get(i) == null )
			{ s += "\nNo PC-rule was applied for this event";
			}
			else
			{ s += "\nApplied PC-rule:\n" + rules.get(i).toString();
				s += "\nwith substitution:\n" + thetas.get(i).toString();
			}

     	info.add(new InfoMessage(s));
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
	  return("Process External Events");
	}
	
	public boolean moduleChanged()
	{
		return(!rules.isEmpty());
	}
}

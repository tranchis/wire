package apapl.deliberation;

import java.util.*;

import apapl.data.*;
import apapl.deliberation.DeliberationResult.InfoMessage;
import apapl.program.*;
import apapl.messaging.*;
import apapl.*;

/**
 * The result of the deliberation step {@link apapl.deliberation.ProcessMessages}.
 * Contains information about which messages were received and for which PC-rules
 * were applied.
 */
public class ProcessMessagesResult extends DeliberationResult
{ 
  // Messages that were received	
  private LinkedList<APLMessage> received;
  // PC-rules that were applied (index corresponds to message)
  private LinkedList<PCrule> rules;
  // Substitution found for PC-rule (index corresponds to pc-rule)
  private LinkedList<SubstList> thetas;

  /**
   * Constructs a result object.
   */
  public ProcessMessagesResult( )
  {
    this.received = new LinkedList();
    this.rules = new LinkedList();
    this.thetas = new LinkedList();
  }

  /**
   * Adds a message for which no PC-rule is applied.
   * 
   * @param msg the message to add
   */
  public void addUnprocessed( APLMessage msg )
  {
    received.add(msg);
		rules.add(null);
		thetas.add(null);
  }

  /**
   * Adds a message for which a PC-rule is applied.
   * 
   * @param msg the message to add
   * @param rule the rule that was applied
   * @param theta the substitution used in applying the PC-rule
   */
  public void addProcessed( APLMessage msg, PCrule rule, SubstList theta )
  {
	  received.add(msg); 
    rules.add(rule);
		thetas.add(theta);
  }

	/**
	 * Lists the information about this step in text format. In particular,
	 * it lists which PC-rules have been applied for which messages and which
	 * messages were discarded.
	 * 
	 * @return a list of strings pertaining to the information
	 */
  public LinkedList<InfoMessage> listInfo()
	{
	  LinkedList<InfoMessage> info = new LinkedList<InfoMessage>();

	if( received.isEmpty() )
    { info.add(new InfoMessage("No messages were received"));
      return( info );	     
    }

    for(int i = 0; i < received.size(); i++)
		{ String s = "Received: " + received.get(i).toString() + "\n";
		  if( rules.get(i) == null )
			{ s += "\nNo PC-rule was applied for this message";
			}
			else
			{ s += "\nApplied PC-rule: \n" + rules.get(i).toString();
				s += "\nwith substitution:\n" + thetas.get(i).toString();
			}

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
	  return("Process Messages");
	}
	
	public boolean moduleChanged()
	{
		return(!rules.isEmpty());
	}
}

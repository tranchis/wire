package apapl.deliberation;

import java.util.*;

import apapl.deliberation.DeliberationResult.InfoMessage.Severity;
import apapl.plans.PlanResult;

/**
 * Each deliberation step has a specific effect on the mental state of a module.
 * Information about the effect of the performance of a deliberation step is
 * stored in a designated deliberation result object.
 */
public abstract class DeliberationResult
{
	/**
	 * Represents one message produced by a deliberation step. Each message is
	 * assigned certain severity that can be used to alter the way the
	 * information is render in the GUI.
	 */
	static public class InfoMessage
	{
		public enum Severity
		{
			INSIGNIFICANT, SIGNIFICANT, WARNING, ERROR
		}

		public Severity severity;
		public String message;

		public InfoMessage(String s)
		{
			this.severity = Severity.INSIGNIFICANT;
			this.message = s;
		}

		public InfoMessage(Severity severity, String s)
		{
			this.severity = severity;
			this.message = s;
		}

		/**
		 * Constructs an info message containing textual representation of the
		 * plan execution result. This constructor sets the appropriate severity
		 * of the info message based on the plan execution outcome.
		 * 
		 * @param r the result of plan execution
		 */
		public InfoMessage(PlanResult r)
		{
			if (r.succeeded())
			{
				String s = "Executed:\n"
						+ r.getPlan().toString()
						+ ((r.getMessage() != null) ? "\n\n" + r.getMessage()
								: "");

				this.severity = Severity.SIGNIFICANT;
				this.message = s;
			}

			if (r.failed())
			{
				String s = "Failed to Execute:\n"
						+ r.getPlan().toString()
						+ ((r.getMessage() != null) ? "\n\n" + r.getMessage()
								: "");

				this.severity = Severity.ERROR;
				this.message = s;
			}

			if (r.noEffect())
			{
				String s = "Executed:\n"
						+ r.getPlan().toString()
						+ ((r.getMessage() != null) ? "\n\n" + r.getMessage()
								: "");

				this.severity = Severity.INSIGNIFICANT;
				this.message = s;
			}
		}

		public String getMessage()
		{
			return message;
		}

		public Severity getSeverity()
		{
			return severity;
		}

	}

	/**
	 * The name of the deliberation step this result belongs to.
	 * 
	 * @return the name of this step
	 */
	public abstract String stepName();

	/**
	 * Checks whether the execution of the step this result belongs to has
	 * changed the state of the module.
	 * 
	 * @return <code>true</code> if the module has been changed while executing
	 *         this step, <code>false</code> otherwise
	 */
	public abstract boolean moduleChanged();

	/**
	 * Lists the information about this step in text format.
	 * 
	 * @return a list of strings pertaining to the information
	 */
	public abstract LinkedList<DeliberationResult.InfoMessage> listInfo();
}

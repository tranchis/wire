package apapl.program;

import apapl.PlanUnifier;
import apapl.Unifier;
import apapl.APLModule;
import apapl.Parser;
import apapl.plans.*;
import apapl.data.*;
import java.util.ArrayList;
import apapl.SubstList;
import java.util.LinkedList;
import apapl.SubstList;
import java.util.NoSuchElementException;

/**
 * The base in which the {@link apapl.program.PRrule}s are stored.
 */
public class PRrulebase extends Rulebase<PRrule> implements Cloneable
{
	/**
	 * @return  clone of the PG rulebase
	 */
	public PRrulebase clone()
	{
		PRrulebase b = new PRrulebase(); 
		b.setRules(getRules());
		return b;
	}
}

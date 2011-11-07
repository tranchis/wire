package apapl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import apapl.data.APLFunction;
import apapl.data.APLIdent;
import apapl.data.APLNum;
import apapl.data.APLList;
import apapl.data.Term;

import eis.iilang.Action;
import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import eis.iilang.Percept;
import eis.iilang.Function;

public class IILConverter {

	public static Action convertToAction(APLFunction action) {
		
		Parameter[] params = new Parameter[action.getParams().size()];
		
		int index = 0;
		for( Term t : action.getParams() ) {
			
			Parameter p = convert(t);
			
			params[index] = p;
			index ++;
			
		}
		
		return new Action(action.getName(), params);		
	}
	
	
	public static Parameter convert(Term term) {
		
		if( term instanceof APLNum ) {
			
			Numeral ret = new Numeral( ((APLNum)term).getVal() );		
			return ret;			
		}
		if( term instanceof APLIdent ) {			
			return new Identifier( ((APLIdent)term).getName() );
		
		}
		if( term instanceof APLList) {
			List<Parameter> list = new ArrayList<Parameter>();
			for(Term item : ((APLList) term).toLinkedList()) {
				list.add(convert(item));
			}
			return new ParameterList(list);			
		}
	    if( term instanceof APLFunction) {
	        Parameter params[] = new Parameter[((APLFunction) term).getParams().size()];
	        for (int i = 0; i < params.length; i++)
	            params[i] = convert(((APLFunction) term).getParams().get(i));
	        
	        return new Function(((APLFunction) term).getName(), params);
	    }
		else {
			assert false: "Unknown type " + term.getClass();			
		}
		
		return null;
		
	}
	
	public static APLFunction convert(Percept percept) {

		// terms
		ArrayList<Term> params = new ArrayList<Term>();

		for ( Parameter p : percept.getParameters() ) {
		
			params.add( IILConverter.convert(p) );
			
		}
		
		return new APLFunction(percept.getName(), params);
		
	}
	
	public static Term convert(Parameter parameter) {

		Term ret = null;
		
		if ( parameter instanceof Identifier ) {
			
			return new APLIdent(((Identifier)parameter).getValue());
			
		}
		if ( parameter instanceof Numeral ) {
			
			return new APLNum( ((Numeral)parameter).getValue().doubleValue() );
			
		}		
		if (parameter instanceof ParameterList) {
			LinkedList<Term> terms = new LinkedList<Term>();					
			
			for (Parameter item : ((ParameterList) parameter)) {
				terms.add(convert(item));
			}
			
			return new APLList(terms);
		}
		else if(parameter instanceof Function)
		{
			Function f=(Function)parameter;
			ArrayList<Term>terms=new ArrayList<Term>();
			for(Parameter p:f.getParameters())
				terms.add(convert(p));
			APLFunction aplf=new APLFunction(f.getName(),terms);
			return aplf;
		}
		else {
			assert false: "Unknown type " + parameter.getClass();
		}
		
		return ret;		
	}

/**	public static Term convert(ActionResult result) {

		// terms
		ArrayList<Term> params = new ArrayList<Term>();

		for ( Parameter p : result.getParameters() ) {
		
			params.add( IILConverter.convert(p) );
			
		}
		
		return new APLFunction(result.getName(), params);
		
	}	
	
	public static ActionResult convertToActionResult(Term term) {			
		Parameter param = convert(term);
		if (term != null) {
			return new ActionResult("actionresult", param);
		}
		else {
			return null;
		}		
	}*/	

	public static Percept convertToActionResult(Term term) {			
		Parameter param = convert(term);
		if (term != null) {
			return new Percept("actionresult", param);
		}
		else {
			return null;
		}		
	}
	
	public static Percept convertToPercept(APLFunction f)
	{
	    Parameter params[] = new Parameter[f.getParams().size()];
	    for (int i = 0; i < params.length; i++)
	        params[i] = convert(f.getParams().get(i));
	    
	    return new Percept(f.getName(), (Parameter[]) params);
	}
}

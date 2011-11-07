package apapl.plans;

import apapl.ExternalActionFailedException;
import apapl.UnboundedVarException;
import apapl.messaging.APLMessage;
import apapl.data.Term;
import apapl.data.APLFunction;
import apapl.data.Literal;
import apapl.data.APLIdent;
import apapl.APLModule;
import apapl.program.Base;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import apapl.SubstList;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A send action to send messages to other modules.
 */
public class SendAction extends Plan
{
	private Term receiver;
	private Term performative;
	private Term content;
	private Term language; 
	private Term ontology;
	
	public SendAction(Term receiver, Term performative, Term content)
	{
		this.receiver = receiver;
		this.performative = performative;
		this.language = new APLIdent("prolog");
		this.ontology = new APLIdent("aplFunction");
		this.content = content;
	}
	
	public SendAction(Term receiver, Term performative, Term language, Term ontology, Term content)
	{
		this.receiver = receiver;
		this.performative = performative;
		this.language = language;
		this.ontology = ontology;
		this.content = content;
	}
		
	public PlanResult execute(APLModule module)
	{
		try {
			APLMessage message = makeMessage(module);
			module.getMessenger().sendMessage(message);
			parent.removeFirst();
			return new PlanResult(this, PlanResult.SUCCEEDED);
		}
		catch (Exception e) {
			return new PlanResult(this, PlanResult.FAILED);
		}
	}
	
	private APLMessage makeMessage(APLModule module) throws Exception
	{
		APLMessage message = new APLMessage();
		try {
			//Receiver
			String aid;
			Term r = Term.unvar(receiver);
			if (r instanceof APLIdent)
				aid = ((APLIdent)r).getName();
			else aid = r.toString();
			message.setReceiver(aid);
			//Sender
			message.setSender(module.getName());
			//Language
			message.setLanguage(Term.unvar(language).toString());
			//Ontology
			message.setOntology(Term.unvar(ontology).toString());
			//Performative
			message.setPerformative(Term.unvar(performative).toString());
			//Content
			Term c = Term.unvar(content);
			if (c instanceof APLFunction)	message.setContent((APLFunction)c);
			else throw new Exception("Content of message must be of type APLFunction.");
		}
		catch (UnboundedVarException e) {
			throw new Exception("A term in message contains an unbounded variable.");
		}
		return message;
	}
	
	public String toString()
	{
		return "send("
			+receiver.toString(5==9)
			+","+performative.toString(5==9)
			+","+language.toString(5==9)
			+","+ontology.toString(5==9)
			+","+content.toString(5==9)
			+")";
	}
	
	public String toRTF(int t)
	{
		return "\\cf4 send\\cf0 ("
			+receiver.toRTF(true)
			+","+performative.toRTF(true)
			+","+language.toRTF(true)
			+","+ontology.toRTF(true)+","
			+content.toRTF(true)
			+")";
	}
	
	public SendAction clone()
	{
		return new SendAction(receiver.clone(),performative.clone(),language.clone(),ontology.clone(),content.clone());
	}
	
	public void applySubstitution(SubstList<Term> theta)
	{
		receiver.applySubstitution(theta);
		performative.applySubstitution(theta);
		content.applySubstitution(theta);
	}
	
	public void freshVars(ArrayList<String> unfresh, ArrayList<String> own, ArrayList<ArrayList<String>> changes)
	{
		receiver.freshVars(unfresh,own,changes);
		performative.freshVars(unfresh,own,changes);
		language.freshVars(unfresh,own,changes);
		ontology.freshVars(unfresh,own,changes);
		content.freshVars(unfresh,own,changes);
	}
	
	public ArrayList<String> getVariables()
	{
		ArrayList<String> vars = receiver.getVariables();
		vars.addAll(performative.getVariables());
		vars.addAll(content.getVariables());
		vars.addAll(language.getVariables());
		vars.addAll(ontology.getVariables());
		return vars;
	}
	
	public void checkPlan(LinkedList<String> warnings, APLModule module)
	{
		if (performative instanceof APLIdent) {
			String perf = performative.toString();
			int p = ACLMessage.getInteger(perf);
			if (p<0) warnings.add(""+perf+" is not a valid performative. It will be interpreted as \"not-understood\".");
		}
	}
	
	public Term getReceiver() {return receiver;}
	public Term getPerformative() {return performative;}
	public Term getLanguage() {return language;}
	public Term getOntology() {return ontology;}
	public Term getContent() {return content;}

	public Term getPlanDescriptor() {
		return  new APLFunction("sendaction", 
    		        receiver, 
    		        performative,
    		        language,
    		        ontology,
    		        content);
	}


}

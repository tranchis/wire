package apapl.messaging;

import apapl.data.APLFunction;
import apapl.*;
import apapl.parser.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public class MessageBehaviour extends CyclicBehaviour
{
	public MessageBehaviour()
	{
	}
	
	public void action()
	{
		MessageAgent agent = (MessageAgent)myAgent;
		ACLMessage msg = myAgent.receive();
		while (msg != null) {
			String title = msg.getContent();
			AID sender = msg.getSender();
			agent.incomingMessage(toAPLMessage(msg,agent.getLocalName()));
			msg = myAgent.receive();
		}
		block();
	}
	
	private APLMessage toAPLMessage(ACLMessage msg, String receiver)
	{
		String language = msg.getLanguage(); if (language==null) language = "null";
		String ontology = msg.getOntology(); if (ontology==null) ontology = "null";
		APLMessage message = new APLMessage();
		message.setSender(msg.getSender().getLocalName());
		message.setPerformative(ACLMessage.getPerformative(msg.getPerformative()).toLowerCase());
		message.setLanguage(language);
		message.setOntology(ontology);
		message.setContent(parseContent(msg.getContent()));
		message.setReceiver(receiver);
		return message;
	}
	
	private APLFunction parseContent(String content)
	{
		Parser p = new Parser();
		APLFunction f = null;

    try
		{ f = p.parseAPLFunction(content);
		}
		catch( ParseException e ) {}

    return(f); 
	}
}

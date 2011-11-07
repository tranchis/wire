package apapl.messaging;

import jade.core.Agent;

public class MessageAgent extends Agent
{
	private JadeMessenger jm;
	private String agentname;
	
	/**
	 * This method is called when the agent is initialized.
	 */
	protected void setup()
	{
		Object[] args = getArguments();
		jm = (JadeMessenger)(args[0]);
		agentname = (String)(args[1]);
		jm.agentCallBack(agentname,this);
		MessageBehaviour mb = new MessageBehaviour();
		addBehaviour(mb);
	}
	
	public void incomingMessage(APLMessage message)
	{
		jm.incomingMessage(agentname,message);
	}
	
	
}

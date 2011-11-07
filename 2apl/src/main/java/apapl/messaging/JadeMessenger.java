package apapl.messaging;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Messenger that implements interconnection between 2APL platform and JADE
 * middleware. Each 2APL module is represented by one shadow JADE agent whose
 * only responsibilities are sending and receiving message.
 */
public class JadeMessenger implements Messenger
{
	private HashMap<String,LinkedList<APLMessage>> messageQueues;
	private HashMap<String,MessageAgent> jadeAgents;
	
	private static ContainerController agentContainer;
	private AgentController agentController;
	private String host;// = null;
	private int port;// = -1;
	private jade.core.Runtime rt;
	private LinkedList<MessageListener> listeners;
		
	public JadeMessenger(String host, int port)
	{
		this.host = host;
		this.port = port;
		messageQueues = new HashMap<String,LinkedList<APLMessage>>();
		jadeAgents = new HashMap<String,MessageAgent>();
		listeners = new LinkedList<MessageListener>();
		startJade();
	}
	
	public void addModule(String modulename)
	{
		messageQueues.put(modulename,new LinkedList<APLMessage>());
		
		Object[] args = new Object[2];
		args[0] = this;
		args[1] = modulename;
		try {
			agentController = agentContainer.createNewAgent(modulename,"apapl.messaging.MessageAgent",args);
			agentController.start();
		}
		catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	
	public void removeModule(String modulename)
	{
		messageQueues.remove(modulename);
		
		try {
			agentController = agentContainer.getAgent(modulename);
			agentController.kill();
		}
		catch (ControllerException e) {
			e.printStackTrace();
		}
	}
	
	public void restart()
	{
		try{
			agentContainer.kill();
			rt.shutDown();
		}
		catch (Exception e) {}
		startJade();
	}
	
	public void agentCallBack(String name, MessageAgent agent)
	{
		jadeAgents.put(name,agent);
	}
	
	public void sendMessage(APLMessage message)
	{
		String sender = message.getSender();
		MessageAgent agent = jadeAgents.get(sender);
		if (agent!=null) 
			agent.send(toJadeMessage(message));
	}
	
	public void incomingMessage(String modulename, APLMessage msg)
	{
		synchronized(this) {
			LinkedList<APLMessage> 
				messageQueue = messageQueues.get(modulename);
			if (messageQueue!=null) 
				messageQueue.offer(msg);
		}
		
		for (MessageListener listener : listeners) {
			listener.messageSent(msg);
		}
	}
		
	public synchronized APLMessage receiveMessage(String receiver)
	{
		LinkedList<APLMessage> messageQueue = messageQueues.get(receiver);
		if (messageQueue!=null) 
			return messageQueue.poll();
		else 
			return null;
	}
		
	private ACLMessage toJadeMessage(APLMessage message)
	{
		int p = ACLMessage.getInteger(message.getPerformative());
		ACLMessage msg = new ACLMessage(p);
		String aid = message.getReceiver();
		boolean isguid = aid.contains("@");
		msg.addReceiver(new AID(aid,isguid));
		msg.setLanguage(message.getLanguage());
		msg.setOntology(message.getOntology());
		msg.setContent(message.getContent().toString());
		return msg;
	}
	
	private void startJade()
	{
		rt = jade.core.Runtime.instance();
		rt.setCloseVM(false);

		Profile p = null;
		if (host==null) {
			p = new ProfileImpl(null,port,null);
			p.setParameter(Profile.DETECT_MAIN, "false");
			agentContainer = rt.createMainContainer(p);
		}
		else {
			p = new ProfileImpl(host,port,null);
			agentContainer = rt.createAgentContainer(p);
		}
		if (agentContainer==null) System.out.println("Could not start "+(host==null?"Main":"Agent")+"-Container.");
	}
	
	public void addMessageListener(MessageListener listener)
	{
		listeners.add(listener);
	}
	
	public synchronized int getMessageCount(String modulename)
	{
		LinkedList<APLMessage> messageQueue = messageQueues.get(modulename);
		
		if (messageQueue != null) 
			return messageQueue.size();
		else 
			throw new RuntimeException("No message queue for module + \"" + 
					modulename + "\".");
	}
	
}
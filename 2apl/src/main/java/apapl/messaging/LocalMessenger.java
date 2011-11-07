package apapl.messaging;

import java.util.LinkedList;
import java.util.HashMap;

/**
 * Stand-alone messenger independent from Jade.
 */
public class LocalMessenger implements Messenger
{
	private HashMap<String,LinkedList<APLMessage>> messageQueues;
	private LinkedList<MessageListener> listeners;
		
	public LocalMessenger()
	{
		restart();
		listeners = new LinkedList<MessageListener>();
	}
	
	public void addModule(String modulename)
	{
		messageQueues.put(modulename,new LinkedList<APLMessage>());
	}

	public void removeModule(String modulename)
	{
		messageQueues.remove(modulename);
	}
	
	public synchronized void restart()
	{
		messageQueues = new HashMap<String,LinkedList<APLMessage>>();
	}
	
	public void sendMessage(APLMessage message)
	{
		synchronized(this) {
			String receiver = message.getReceiver();
			LinkedList<APLMessage> messageQueue = messageQueues.get(receiver);
			if (messageQueue!=null) 
				messageQueue.offer(message);
		}
		
		for (MessageListener listener : listeners) {
			listener.messageSent(message);
		}		
	}
		
	public synchronized APLMessage receiveMessage(String modulename)
	{
		LinkedList<APLMessage> messageQueue = messageQueues.get(modulename);
		
		if (messageQueue!=null) 
			return messageQueue.poll();
		else 
			return null;
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
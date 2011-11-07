package apapl.messaging;

/**
 * An interface for an specific messenger object that can be used for the
 * communication between modules.
 */
public interface Messenger
{
	public void sendMessage(APLMessage message);

	public APLMessage receiveMessage(String receiver);

	public void addModule(String modulename);

	public void removeModule(String modulename);

	public void restart();

	/**
	 * Adds the listener that is notified each time a message is sent.
	 * 
	 * @param listener the listener to set
	 */
	public void addMessageListener(MessageListener listener);
	
	/**
	 * Determines number of messages waiting in the module's message queue.
	 *  
	 * @param the module to count messages for 
	 * @return the length of message queue for given module
	 */
	public int getMessageCount(String modulename);
}
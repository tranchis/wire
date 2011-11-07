package apapl.messaging;

/**
 * Defines interface of an object that is notified (when registered) when a
 * message has been sent.
 */
public interface MessageListener
{
	public void messageSent(APLMessage message);
}
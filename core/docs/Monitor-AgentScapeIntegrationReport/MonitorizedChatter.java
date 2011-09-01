package org.iids.aos.agents.monitor;

import org.iids.aos.agent.Agent;
import org.iids.aos.systemservices.communicator.structs.AgentHandle;
import org.iids.aos.systemservices.communicator.structs.AgentScapeID;
import org.iids.aos.messagecenter.Envelope;

import org.iids.aos.exception.AgentScapeException;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.ictalive.eventbus.EventBus;
import alive.EventModel.Fact.SendAct;
import alive.EventModel.Event.Actor;
import alive.EventModel.Event.EventFactory;
import alive.EventModel.Event.Event;
import alive.EventModel.Fact.FactFactory;
import org.eclipse.emf.ecore.EObject;
import alive.EventModel.Fact.Content;
import opera.OM.Atom;
import opera.OM.OMFactory;
import alive.EventModel.Fact.Message;
import alive.EventModel.Fact.Fact;



/**
 * Monitorized Chatting Agent. Just connect two agents and send messages between them. This message interchange is reflected on monitor
 @author igomez
 */
public class MonitorizedChatter extends Agent
{
    private static final transient org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog (MonitorizedChatter.class);

    enum MessageType {
        COMMUNICATION, HELLO, BYE, UNKNOWN
    }

    private static String NAME_KEY = "chatclient.name";
    private String myName = "uninitialized";

    // Swing components
    private transient JTextArea rcvTextArea;
    private transient JTextArea sndTextArea;
    private transient JComboBox agentList;
    private transient JTextField nameTextField;
    private transient JFrame frame;

    private transient JButton sendButton;
    private transient JButton registerButton;

    private transient java.util.List<AgentHandle> sentHelloTo;
    private transient Map<String,AgentHandle> receivedHelloFrom;

    private void showMe ()
    {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        sndTextArea = new JTextArea(3, 40);
        rcvTextArea = new JTextArea(10, 40);
        nameTextField = new JTextField(40);

        sentHelloTo = new Vector<AgentHandle>();
        receivedHelloFrom = new Hashtable<String,AgentHandle>();

        //
        // action for sending a message
        //

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (sndTextArea.getText() == null) {
                    return;
                }

                Object o = agentList.getSelectedItem();
                if (o == null) {
                    return;
                }

                AgentHandle dest = resolve ((String) o);
                log.debug("sending message to: " + dest);

                /*MAMessage msg = new MAMessage (MAMessage.Type.COMMUNICATION,
                    sndTextArea.getText());
                    */
                String msg = "" + MessageType.COMMUNICATION + 
                        sndTextArea.getText();

                Envelope env = new Envelope (dest, getPrimaryHandle(), msg);

                try {
                    sendMessage (env);
                    Monitor("Sent", (String)env.getData(), env.getFromHandle().toString(),  env.getToHandle().toString());
                    sndTextArea.setText("");
                }
                catch (AgentScapeException ex) {
                    reportError ("Problem sending HELLO to " + dest, ex);
                }
            }
        });

        //
        // action for killing the agent
        //

        JButton killButton = new JButton("Kill");
        killButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        });

        //
        // registering the name (only once)
        //

        registerButton = new JButton("Connect");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startWithName (nameTextField.getText());
                registerButton.setEnabled(false);
            }
        });



        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */

        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JPanel namePane = new JPanel();
        namePane.setBorder(BorderFactory.createTitledBorder("Your name:"));
        namePane.setLayout(new FlowLayout());
        namePane.add(Box.createRigidArea(new Dimension(0, 5)));
        namePane.add(nameTextField);
        nameTextField.setText("Fill in your name and push 'connect'");
        namePane.add(registerButton);

        JPanel destIdPane = new JPanel();
        destIdPane.setBorder(
                BorderFactory.createTitledBorder("Destination ID:"));
        destIdPane.add(Box.createRigidArea(new Dimension(0, 5)));
        Vector<String> agentItems = new Vector<String>();
        agentItems.add("<empty>");
        agentList = new JComboBox(agentItems);
        destIdPane.add(agentList);

        JPanel incomingPane = new JPanel();
        incomingPane.setBorder(
                BorderFactory.createTitledBorder("Received messages:"));
        incomingPane.add(Box.createRigidArea(new Dimension(0, 5)));
        JScrollPane rcvScrollPane = new JScrollPane(rcvTextArea);
        rcvScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        rcvScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rcvScrollPane.setPreferredSize(new Dimension(500, 145));
        rcvScrollPane.setMinimumSize(new Dimension(10, 10));
        incomingPane.add(rcvScrollPane);

        JPanel msgPane = new JPanel();
        msgPane.setBorder(BorderFactory.createTitledBorder("Your message:"));
        msgPane.add(Box.createRigidArea(new Dimension(0, 5)));
        JScrollPane sndScrollPane = new JScrollPane(sndTextArea);
        sndScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sndScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sndScrollPane.setPreferredSize(new Dimension(500, 40));
        sndScrollPane.setMinimumSize(new Dimension(10, 10));
        msgPane.add(sndScrollPane);

        JPanel controlPane = new JPanel();
        controlPane.setBorder(
                BorderFactory.createTitledBorder("Control panel:"));
        controlPane.add(Box.createRigidArea(new Dimension(0, 5)));
        controlPane.add(sendButton);
        sendButton.setEnabled(false);
        controlPane.add(killButton);

        pane.add(namePane);
        pane.add(destIdPane);
        pane.add(incomingPane);
        pane.add(msgPane);
        pane.add(controlPane);


        frame = new JFrame("Monitorized Chatting Agent");
        frame.getContentPane().add(pane, BorderLayout.WEST);

        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
        frame.setTitle("Monitorized Chatting Agent: " + getPrimaryHandle());
        frame.pack();
        frame.setVisible(true);
    }

    // Send registration message (HELLO) to a certain agent handle. If a HELLO
    // message was already sent to this handle, it does nothing.

    private void sendHello (AgentHandle ah, String myName)
    {
        System.err.print (myName + ": ");
        System.err.println ("sendHello (" + ah + "," + myName + ")");

        if (!agentRunning())
            return;

        System.err.print (myName + ": ");
        System.err.println ("agent still running!");

        synchronized (sentHelloTo)
        {
            if (getPrimaryHandle().equals(ah))
                return;

            if (!sentHelloTo.contains(ah))
            {
                //MAMessage msg = new MAMessage (MAMessage.Type.HELLO, myName);
                String msg = "" + MessageType.HELLO + myName;
                Envelope env = new Envelope (ah, getPrimaryHandle(), msg);

                try {
                    sendMessage (env);
                    Monitor("Sent", (String)env.getData(), env.getFromHandle().toString(),  env.getToHandle().toString());
                    sentHelloTo.add (ah);
                }
                catch (AgentScapeException e) {
                    reportError ("Problem sending HELLO to " + ah, e);
                }
            }
        }
    }

    // process HELLO message, received from handle ah

    private void receivedHelloMessage (AgentHandle ah, String name)
    {
        if (!agentRunning())
            return;

        System.err.print (myName + ": ");
        System.err.println ("Received HELLO message from " + name);

        synchronized (receivedHelloFrom)
        {
            // see if name already registered
            if (receivedHelloFrom.containsKey (name))
            {
                System.err.print (myName + ": ");
                System.err.println ("Already received registration of " + name);
                return;
            }

            receivedHelloFrom.put (name, ah);
            swingInvoke (new RefreshAliasListRunnable ());
        }

        sendHello (ah, myName);
    }

    // process received text message from ah

    private void receivedTextMessage (AgentHandle ah, String text)
    {
        swingInvoke (new AppendTextRunnable (resolve (ah) + ": " + text));
    }

    // process BYE message, received from handle ah

    private void receivedGoodbye (AgentHandle ah)
    {
        if (!agentRunning())
            return;

        synchronized (receivedHelloFrom)
        {
            log.debug ("we (" + getPrimaryHandle() + 
                    ") received goodbye from agent " + ah);

            String name = resolve (ah);
            swingInvoke (new AppendTextRunnable(name + " signed off"));
            
            receivedHelloFrom.remove (name);
            swingInvoke (new RefreshAliasListRunnable ());
        }
    }

    // send BYE message to all known handles

    private void sendGoodbye ()
    {
        log.debug ("Agent " + getPrimaryHandle() + " sending goodbye");

        synchronized (receivedHelloFrom)
        {
            String msg = MessageType.BYE.toString();

            for (AgentHandle target : receivedHelloFrom.values())
            {
                log.debug ("Sending BYE to agent " + target);
                        
                Envelope env = new Envelope (target, getPrimaryHandle(), msg);
                try 
                {
                    sendMessage (env);
                    Monitor("Sent", (String)env.getData(), env.getFromHandle().toString(),  env.getToHandle().toString());
                }
                catch (AgentScapeException e) {
                    reportError ("Problem sending BYE to " + target, e);
                    log.error ("Problem sending BYE to " + target, e);
                }
            }

            receivedHelloFrom.clear ();
        }

        log.debug ("Agent " + getPrimaryHandle() + " sent goodbyes");
    }

    // send a text message (text) to handle ah

    private void sendTextMessage (String text, AgentHandle ah)
    {
        String msg = "" + MessageType.COMMUNICATION + text;
        Envelope env = new Envelope (ah, getPrimaryHandle(), msg);

        try {
            sendMessage (env);
            Monitor("Sent", (String)env.getData(), env.getFromHandle().toString(),  env.getToHandle().toString());
        }
        catch (AgentScapeException e) {
            reportError ("Problem sending HELLO to " + ah, e);
        }
    }

    // convert string to handle

    private AgentHandle resolve (String name)
    {
        return receivedHelloFrom.get (name);
    }

    // convert handle to string

    private String resolve (AgentHandle ah)
    {
        if (ah == null)
            return "";

        for (Map.Entry<String,AgentHandle> me : receivedHelloFrom.entrySet())
            if (me.getValue().equals(ah))
                return me.getKey();

        return "unknown";
    }

    // enable chat by sending HELLO to all others

    private void startWithName (String myName)
    {
        this.myName = myName;

        System.err.println ("startWithName (" + myName + ", agent " + 
                getPrimaryHandle());

        // register self!
        try {
            register (getPrimaryHandle(), NAME_KEY);
        }
        catch (AgentScapeException e) {
            reportError ("Problem registering with chat", e);
            return;
        }

        // send hello to all others!
        try {
            Map<AgentHandle,AgentScapeID> m = lookup (NAME_KEY);
            for (AgentHandle ah : m.keySet())
                sendHello (ah, myName);

            sendButton.setEnabled (true);
        }
        catch (AgentScapeException e) {
            reportError ("Problem starting", e);
        }
    }

    public void run() 
    {
    		
        showMe ();

        while (agentRunning()) 
        {
            // while running do a blocking receive

            try {
                Envelope env = receiveMessage (true);
                String data = (String) (env.getData());

                System.err.print (myName + ": ");
                System.err.println ("Received message [" + data + "]");

                // convert to enum value if possible
                MessageType type = MessageType.UNKNOWN;
                for (MessageType t : MessageType.values())
                {
                    String typeString = t.toString();

                    System.err.print (myName + ": ");
                    System.err.println ("Checking type string " + 
                            typeString + " of length " + typeString.length());

                    if (data.startsWith(typeString))
                    {
                        System.err.print (myName + ": ");
                        System.err.println ("Type " + t + " matches");
                        type = t;
                        System.err.print (myName + ": ");
                        System.err.println ("Get substring, start " +
                                typeString.length());

                        data = data.substring (typeString.length());
                        break;
                    }
                }

                System.err.print (myName + ": ");
                System.err.println ("Received MAMessage of type " + type +
                        ", data in message is " + data);

                switch (type)
                {
                    case COMMUNICATION:
                        receivedTextMessage (env.getFromHandle(), data);
                        break;

                    case HELLO:
                        receivedHelloMessage (env.getFromHandle(), data);
                        break;

                    case BYE:
                        receivedGoodbye (env.getFromHandle());
                        break;

                    case UNKNOWN:
                    default:
                        System.err.print (myName + ": ");
                        System.err.println ("MonitorizedChatter: message type unknown");
                        System.err.print (myName + ": ");
                        System.err.println ("Message was: " + data);
                }
            }
            catch (AgentScapeException e) {
                if (agentRunning())
                    reportError ("Problem receiving message", e);
            }
        }
    }

    // Shutdown agent, first tell other agents goodbye, then ask AgentScape
    // to kill our agent. If the kill succeeds, this calls cleanUp

    private void shutdown ()
    {
        // invoke in a non-awt thread in case of errors..
        
        nonSwingInvoke (new Runnable () {
            public void run () {
                try {
                    sendGoodbye ();
                    kill ();
                }
                catch (AgentScapeException ex) 
                {
                    log.error ("Problem killing self", ex);
                    reportError ("Problem killing self", ex);
                }
            }
        });
    }

    // cleanUp is called by our agent server, we do the disposing of the
    // frame here, so the window will only disappear if kill worked.

    public void cleanUp ()
    {
        if (frame != null)
        {
            frame.setVisible (false);
            frame.dispose();
            frame = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //
    // Runnable classes, to allow GUI updates to be executed from the
    // event dispatcher thread. Though our app is not really multithreaded,
    // it is always good practice to access swing components this way.
    //
    ////////////////////////////////////////////////////////////////////////

    // append text to our conversation window

    private class AppendTextRunnable implements Runnable
    {
        private String message;

        AppendTextRunnable (String message)
        {
            this.message = message;
        }

        public void run ()
        {
            rcvTextArea.append (message + "\n");
        }
    }

    // append chat client name to the agent list

    private class RefreshAliasListRunnable implements Runnable
    {
        public void run ()
        {
            agentList.removeAllItems();
            for (String name : receivedHelloFrom.keySet())
                agentList.addItem (name);

            if (agentList.getItemCount() == 0)
                agentList.addItem ("<empty>");
        }
    }

    // report an error on the screen

    private void reportError (String msg, Throwable t)
    {
        StringBuffer sb = new StringBuffer();
        sb.append (msg);
        sb.append ("\n");

        if (t != null)
            t.printStackTrace();

        while (t != null)
        {
            if (t instanceof AgentScapeException)
            {
                sb.append ("AgentScapeException: ");
                sb.append (t.getMessage());
                sb.append ("\n");
            }
            else
            {
                sb.append ("Caused by: ");
                sb.append (t.getClass().getName());
                sb.append ("\n");
            }

            t = t.getCause();
        }

        System.err.print (myName + ": ");
        System.err.println (sb.toString());
        swingInvoke (new AppendTextRunnable (sb.toString()));
    }

    // the swingInvoke method executes a runnable from the event dispatcher
    // thread if possible. If the calling thread already is the correct thread
    // then the runnable is executed right away, otherwise calls invoke &
    // waits for the runnable to complete (SwingUtilities.invokeAndWait)

    private void nonSwingInvoke (Runnable r)
    {
        // if we're not the event dispatcher thread, then run immediately
        if (!java.awt.EventQueue.isDispatchThread())
        {
            r.run();
            return;
        }

        // otherwise send Runnable to a new thread
        // don't wait (would just block event dispatcher thread..)
        new Thread(r).start();
    }

    private void swingInvoke (Runnable r)
    {
        // if we're the event dispatcher thread, then run immediately
        if (java.awt.EventQueue.isDispatchThread())
        {
            r.run();
            return;
        }

        // otherwise send Runnable to event dispatcher thread
        while (true)
        {
            try 
            {
                SwingUtilities.invokeAndWait (r);
                break;
            }
            catch (InterruptedException e)
            {
                // thread interrupted, try again
                continue;
            }
            catch (java.lang.reflect.InvocationTargetException e)
            {
                // Runnable caused exception
                r.run();
                break;
            }
        }
    }
    
    private void Monitor(String cause, String concept, String from, String to)
		{			
								//Remote EventBus instance
								//EventBus eb = new EventBus("147.83.200.102");
								//Local EventBus
								EventBus eb = new EventBus();
								Event	dummyEvent;
								SendAct dummyFact;								
								Actor	senderAgent;
								Actor	reciverAgent;
								Content dummyContent;
								Message dummyMess; 
								Atom dummyAtom;																
								
								//Start of the Russian Dolls game
								//Event to be sent
								dummyEvent = EventFactory.eINSTANCE.createEvent();
								//Fact inside the Content
								dummyFact = FactFactory.eINSTANCE.createSendAct();	
								//Content inside the Event
								dummyContent= FactFactory.eINSTANCE.createContent();
								//Message inside the Fact
								dummyMess = FactFactory.eINSTANCE.createMessage();
								//Atom inside the message
								dummyAtom = OMFactory.eINSTANCE.createAtom();
								
								//Set cause and concept (typically 'Sent' and the message sent) into the atom
								dummyAtom.setProposition(cause + " : " + concept);
								dummyMess.getObject().add(dummyAtom);
								//Put the message inside the fact
								dummyFact.setSendMessage(dummyMess);	
								//Initialize sender and receiver using Agent's handlers
								senderAgent = EventFactory.eINSTANCE.createActor();
								senderAgent.setName(from);
								senderAgent.setUrl("localhost");
								reciverAgent = EventFactory.eINSTANCE.createActor();
								reciverAgent.setName(to);
								reciverAgent.setUrl("localhost");
								//Set sender and receiver in the fact
								dummyFact.setSender(senderAgent);
								dummyFact.setReceiver(reciverAgent);
								//Put the fact into the content
								dummyContent.setFact(dummyFact);
								//Put the content into the event
								dummyEvent.setContent(dummyContent);
								
								//Set aserter of the event
								dummyEvent.setAsserter(senderAgent);
								
								//Publish the event
								try
								{
									eb.publish(dummyEvent);
									try
									{
										Event resultEvent = eb.take();
										System.err.println(resultEvent.toString());									
									}
									catch (java.lang.InterruptedException EE)
									{
										System.err.println("InterruptedException:'" + EE.getMessage() + "'");
									}									
								}
								catch(java.io.IOException E)
								{
									System.err.println("IOException:'" + E.getMessage() + "'"); 								
								}
								
		}
    
}

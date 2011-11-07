package msagent;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import msagent.characters.Animation;
import msagent.characters.Character;
import msagent.characters.Peedy;

/***
 * This class represents a Microsoft agent and can be used to create and control an Microsoft agent character.
 * An MSAgent instance needs a character to be identified with:
 * <xmp>
 * MSAgent myAgent = new MSAgent(Merlin.getInstance());
 * </xmp>
 *
 * @see MSAgentListener
 * @see MSAgentActionEvent
 * @see MSAgentException
 * @author Ramon Hagenaars
 */
public class MSAgent
implements MSAgentListener
{
	private static boolean initialized;
	private static HashSet<String> charactersInUse;
	private static HashSet<MSAgent> msAgents;	
	
	private Character character;
	private boolean detached;
	private boolean isVisible;
	
	/***
	 * Initializes the class. This method is executed only once.
	 * @throws MSAgentException thrown when testing the dll location fails
	 */
	private synchronized static void init() 
	throws MSAgentException
	{
		if (initialized)
			return;
		
		try
		{
			String path = (MSAgent.class.getResource("").getPath().split("[\\w]+\\.jar\\!")[0].split("file:/")[1] + "dll/MSAgentAvatar.dll").replaceAll("%20", " ");
			if (!new File(path).exists())
				throw new MSAgentException("Could not load native library in '" + path + "'");
		}
		catch (ArrayIndexOutOfBoundsException aioobe) { throw new MSAgentException("Please rename the jar-file to 'msagentenvironment.jar'"); }
		
		charactersInUse = new HashSet<String>();
		msAgents = new HashSet<MSAgent>();
		
		initialized = true;
		
		Env.getInstance().start();
	}
	
	/***
	 * Constructor; instantiates a new MSAgent object.
	 * @param character the character of the MSAgent
	 * @throws MSAgentException thrown when tempted to create a duplicate character or when the running OS is not a Windows
	 */
	public MSAgent(Character character) 
	throws MSAgentException
	{
		init();

		if (charactersInUse.contains(character.getName()))
			throw new MSAgentException("The given character (" + character.getName() + ") is already in use and may be used only once");
		
		if (!System.getProperty("os.name").toLowerCase().contains("windows"))
			throw new MSAgentException("The MSAgent functionality is only provided for a Microsoft Windows operating system");
		
		this.character = character;
		detached = false;
		isVisible = false;
		
		addMSAgentListener(this);
		
		Env.getInstance().doCreateAvatar(this, character.getName() + ".acs");
		charactersInUse.add(character.getName());
		msAgents.add(this);	
	}
	
	/***
	 * Returns whether this MSAgent is visible; when the <code>show</code> method was called.
	 * @return true when this MSAgent is visible
	 */
	public boolean isVisible()
	{
		return isVisible;
	}
	
	/***
	 * Returns the Character which was bound to this MSAgent instance during initialization.
	 * @return a Character object
	 */
	public synchronized Character getCharacter()
	{
		return character;
	}
	
	/***
	 * Returns a String with the name of the character of this MSAgent.
	 */
	@Override
	public synchronized String toString() 
	{
		return "MSAgent " + character.getName();
	}
	
	/***
	 * Detaches all MSAgents.
	 */
	public static synchronized void detach()
	{
		charactersInUse.clear();
		
		for (MSAgent msAgent : msAgents.toArray(new MSAgent[0]))
			msAgent.detached = true;
		
		Env.getInstance().doDetach(null);
		
		initialized = false;
	}
	
	/***
	 * Makes the character appear.
	 * @param animated indicates whether the character appears animated (true) or fast (false)
	 * @throws MSAgentException 
	 */
	public synchronized void show(boolean animated) 
	throws MSAgentException
	{
		if (detached)
			throw new MSAgentException("This agent has been detached thus can no longer perform actions");
		Env.getInstance().doShow(this, !animated);
		isVisible = true;
		
		try { wait(); }
		catch (InterruptedException ie) { ie.printStackTrace(); }
	}

	/***
	 * Hides the character.
	 * @param animated indicates whether the character hides animated (true) or fast (false)
	 * @throws MSAgentException 
	 */
	public synchronized void hide(boolean animated) 
	throws MSAgentException
	{
		if (detached)
			throw new MSAgentException("This agent has been detached thus can no longer perform actions");
		Env.getInstance().doHide(this, !animated);
		isVisible = false;
		
		try { wait(); }
		catch (InterruptedException ie) { ie.printStackTrace(); }
	}
	
	/***
	 * Speaks a given text; shows it in a text balloon and plays the text as audio.
	 * Note that the Microsoft speech drivers need to be installed.
	 * @param text the text which is to be spoken
	 * @throws MSAgentException 
	 */
	public synchronized void speak(String text) 
	throws MSAgentException
	{
		if (detached)
			throw new MSAgentException("This agent has been detached thus can no longer perform actions");
		Env.getInstance().doSpeakAudio(this, text);
		
		try { wait(); }
		catch (InterruptedException ie) { ie.printStackTrace(); }
	}
	
	/***
	 * Shows a text balloon which indicates the thoughts of the character.
	 * @param thoughts the text which should appear in the balloon
	 * @throws MSAgentException 
	 */
	public synchronized void think(String thoughts) 
	throws MSAgentException
	{
		if (detached)
			throw new MSAgentException("This agent has been detached thus can no longer perform actions");
		Env.getInstance().doThink(this, thoughts);
		
		try { wait(); }
		catch (InterruptedException ie) { ie.printStackTrace(); }
	}
	
	/***
	 * Moves the character to a specific location on the screen in a specified time.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param delay the delay in milliseconds
	 * @throws MSAgentException 
	 */
	public synchronized void moveTo(int x, int y, int delay) 
	throws MSAgentException
	{
		if (detached)
			throw new MSAgentException("This agent has been detached thus can no longer perform actions");
		Env.getInstance().doMoveTo(this, x, y, delay);
		
		try { wait(); }
		catch (InterruptedException ie) { ie.printStackTrace(); }
	}
	
	/***
	 * Gestures at a specific location on the screen.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @throws MSAgentException 
	 */
	public synchronized void gestureAt(int x, int y) 
	throws MSAgentException
	{
		if (detached)
			throw new MSAgentException("This agent has been detached thus can no longer perform actions");
		Env.getInstance().doGestureAt(this, x, y);
		
		try { wait(); }
		catch (InterruptedException ie) { ie.printStackTrace(); }
	}
	
	/***
	 * Performs a specific Animation.
	 * @param animation an Animation object which holds the requested animation that should be performed
	 * @throws MSAgentException thrown when the character of this MSAgent does not match with the given Animation's character
	 */
	public synchronized void perform(Animation animation) 
	throws MSAgentException
	{
		if (!animation.getCharacter().getName().equals(character.getName()))
			throw new MSAgentException("The animation " + animation.getCharacter().getName() + "." + animation.getName().toUpperCase() + " should be used for character " + animation.getCharacter().getName() + " only");
		Env.getInstance().doPlayAnimation(this, animation.getName());
		
		try { wait(); }
		catch (InterruptedException ie) { ie.printStackTrace(); }
	}
	
	/***
	 * Adds an MSAgentListener to this MSAgent.
	 * @see MSAgentListener
	 * @see MSAgentActionEvent
	 * @param listener the MSAgentListener
	 */
	public synchronized void addMSAgentListener(MSAgentListener listener)
	{
		Env.getInstance().addMSAgentListener(this, listener);
	}
	
	/***
	 * Adds an MSAgentListener to all MSAgents.
	 * @see MSAgentListener
	 * @see MSAgentActionEvent
	 * @param listener the MSAgentListener
	 */	
	public static synchronized void addMSAgentListenerToAll(MSAgentListener listener)
	{
		for (MSAgent msAgent : msAgents)
			Env.getInstance().addMSAgentListener(msAgent, listener);
	}
	
	/***
	 * Removes an MSAgentListener from this MSAgent.
	 * @param listener the MSAgentListener
	 */	
	public synchronized void removeMSAgentListener(MSAgentListener listener)
	{
		Env.getInstance().removeMSAgentListener(this, listener);
	}
	
	/***
	 * Removes an MSAgentListener from all MSAgents.
	 * @param listener the MSAgentListener
	 */
	public static synchronized void removeMSAgentListenerFromAll(MSAgentListener listener)
	{
		for (MSAgent msAgent : msAgents)
			Env.getInstance().removeMSAgentListener(msAgent, listener);
	}

	@Override
	public synchronized void actionPerformed(MSAgentActionEvent msAgentActionEvent) 
	{
		//System.out.println("MSAgent.notify()");
		notify();
	}
}

/***
 * Contains the native methods from the dll.
 * Note that this class was made a Thread, because [i]there can only be one thread controlling the actual Microsoft Agents[/i].
 * Therefore this class was made singleton.
 */
class Env extends Thread
{
	private static Env instance;
	private static volatile HashMap<MSAgent, Integer> msAgentIDs;
	private static volatile HashMap<MSAgent, HashSet<MSAgentListener>> listeners;

	private boolean running;
	private LinkedList<State> states;
	
	/***
	 * This subclass is used to determine in which state the Env class is.
	 * 
	 * @author Ramon Hagenaars
	 */
	private class State
	{
		MSAgent performer;
		int number;
		Object[] arguments;
		
		/***
		 * Constructor of the subclass.
		 * @param performer the MSAgent object which executed the action
		 * @param number the number of the state, for example Env.STATE_SHOW
		 * @param arguments an array of possible arguments of which the length differs per action
		 */
		State(MSAgent performer, int number, Object... arguments)
		{
			this.performer = performer;
			this.number = number;
			this.arguments = arguments;
		}
	}
	
	/***
	 * Constructor; initializes the one and only Env object.
	 */
	private Env()
	{
		running = false;
		
		msAgentIDs = new HashMap<MSAgent, Integer>();
		states = new LinkedList<State>();
		listeners = new HashMap<MSAgent, HashSet<MSAgentListener>>();

		states.push(new State(null, MSAgentActionEvent.INITIAL));
	}
	
	/***
	 * Returns the one and only instance of this class. Initializes it when it hasn't been initialized yet.
	 * @return an Env object
	 */
	public static Env getInstance()
	{
		if (instance == null)
			instance = new Env();
		
		return instance;
	}
	
	@Override
	public synchronized void start()
	{
		if (!running)
			super.start();
	}
	
	/***
	 * This method was overridden from Thread and holds the actual code for controlling the Microsoft Agents.
	 */
	@Override
	public void run()
	{
		Object result = null;
		State state;
		running = true;
		
		while (running)
		{
			state = states.pollLast();
			
			switch (state.number)
			{
				case MSAgentActionEvent.INITIAL:
					//System.out.println("STATE_INITIAL");

					System.load((MSAgent.class.getResource("").getPath().split("[\\w]+\\.jar\\!")[0].split("file:/")[1] + "dll/MSAgentAvatar.dll").replaceAll("%20", " "));
					result = true;
					break;
				case MSAgentActionEvent.CREATEAVATAR:
					//System.out.println("STATE_CREATEAVATAR");
					int buffer = CreateAvatar((String)state.arguments[0]);
					msAgentIDs.put(state.performer, buffer);
					result = buffer;
					break;
				case MSAgentActionEvent.SHOW:
					//System.out.println("STATE_SHOW");
					result = Show(msAgentIDs.get(state.performer), (Boolean)state.arguments[0]);
					break;
				case MSAgentActionEvent.HIDE:
					//System.out.println("STATE_HIDE");
					result = Hide(msAgentIDs.get(state.performer), (Boolean)state.arguments[0]);
					break;
				case MSAgentActionEvent.SPEAKAUDIO:
					//System.out.println("STATE_SPEAKAUDIO");
					result = SpeakAudio(msAgentIDs.get(state.performer), (String)state.arguments[0]);
					break;
				case MSAgentActionEvent.SPEAKTEXT:
					//System.out.println("STATE_SPEAKAUDIO");
					result = SpeakText(msAgentIDs.get(state.performer), (String)state.arguments[0]);
					break;					
				case MSAgentActionEvent.THINK:
					//System.out.println("STATE_THINK");
					result = Think(msAgentIDs.get(state.performer), (String)state.arguments[0]);
					break;
				case MSAgentActionEvent.MOVETO:
					//System.out.println("STATE_MOVETO");
					result = MoveTo(msAgentIDs.get(state.performer), (Integer)state.arguments[0], (Integer)state.arguments[1], (Integer)state.arguments[2]);
					break;
				case MSAgentActionEvent.GESTUREAT:
					//System.out.println("STATE_GESTUREAT");
					result = GestureAt(msAgentIDs.get(state.performer), (Integer)state.arguments[0], (Integer)state.arguments[1]);
					break;
				case MSAgentActionEvent.PLAYANIMATION:
					//System.out.println("STATE_PLAYANIMATION");
					result = PlayAnimation(msAgentIDs.get(state.performer), (String)state.arguments[0]);
					break;
				case MSAgentActionEvent.SETLANGUAGEID:
					//System.out.println("STATE_SETLANGUAGEID");
					SetLanguageID((Long)state.arguments[0]);
					result = true;
					break;
				case MSAgentActionEvent.DETACH:
					//System.out.println("STATE_DETACH");
					Detach();
					result = true;
					msAgentIDs.clear();
					break;
			}

			if (state.performer == null)
			{
				synchronized(this)
				{
					for (HashSet<MSAgentListener> listeners : Env.listeners.values())
						for (MSAgentListener listener : listeners)
							listener.actionPerformed(new MSAgentActionEvent(state.number, state.performer, result));
				}
			}
			else
			{
				synchronized(this)
				{
					for (MSAgentListener listener : listeners.get(state.performer).toArray(new MSAgentListener[0]))
						listener.actionPerformed(new MSAgentActionEvent(state.number, state.performer, result));
				}
			}
			
			if (states.size() == 0)
			{
				synchronized(this)
				{
					try 
					{ 
						this.wait();
					} 
					catch (InterruptedException ie) { ie.printStackTrace(); }
				}
			}
		}
	}
	
	public synchronized void addMSAgentListener(MSAgent msAgent, MSAgentListener listener)
	{
		if (listeners.get(msAgent) == null)
			listeners.put(msAgent, new HashSet<MSAgentListener>());
			
		listeners.get(msAgent).add(listener);
	}
	
	public synchronized void removeMSAgentListener(MSAgent msAgent, MSAgentListener listener)
	{
		if (listeners.get(msAgent) == null)
			return;
		
		listeners.get(msAgent).remove(listener);
	}
	
	public synchronized void doCreateAvatar(MSAgent msAgent, String sName)
	{//System.out.println("doCreateAvatar " + sName);
		states.push(new State(msAgent, MSAgentActionEvent.CREATEAVATAR, sName));

		notify();
	}
	
	public synchronized void doMoveTo(MSAgent msAgent, int zX, int zY, int zSpeed)
	{//System.out.println("doMoveTo");
		states.push(new State(msAgent, MSAgentActionEvent.MOVETO, zX, zY, zSpeed));

		notify();
	}
	
	public synchronized void doShow(MSAgent msAgent, boolean bFast)
	{//System.out.println("doShow");
		states.push(new State(msAgent, MSAgentActionEvent.SHOW, bFast));

		notify();
	}
	
	public synchronized void doHide(MSAgent msAgent, boolean bFast)
	{//System.out.println("doHide");
		states.push(new State(msAgent, MSAgentActionEvent.HIDE, bFast));

		notify();
	}
	
	public synchronized void doGestureAt(MSAgent msAgent, int zX, int zY)
	{//System.out.println("doGestureAt");
		states.push(new State(msAgent, MSAgentActionEvent.GESTUREAT, zX, zY));

		notify();
	}
	
	public synchronized void doSpeakAudio(MSAgent msAgent, String sText)
	{//System.out.println("doSpeakAudio");
		states.push(new State(msAgent, MSAgentActionEvent.SPEAKAUDIO, sText));

		notify();
	}
	
	public synchronized void doSpeakText(MSAgent msAgent, String sText)
	{//System.out.println("doSpeakText");
		states.push(new State(msAgent, MSAgentActionEvent.SPEAKTEXT, sText));

		notify();
	}	
	
	public synchronized void doThink(MSAgent msAgent, String sThink)
	{//System.out.println("doThink");
		states.push(new State(msAgent, MSAgentActionEvent.THINK, sThink));

		notify();
	}
	
	public synchronized void doPlayAnimation(MSAgent msAgent, String sAnimation)
	{//System.out.println("doPlayAnimation");
		states.push(new State(msAgent, MSAgentActionEvent.PLAYANIMATION, sAnimation));
		
		notify();
	}
	
	public synchronized void doSetLanguageID(MSAgent msAgent, long zLanguageID)
	{//System.out.println("doSetLanguageID");
		states.push(new State(msAgent, MSAgentActionEvent.PLAYANIMATION, zLanguageID));
		
		notify();		
	}	
	
	public synchronized void doDetach(MSAgent msAgent)
	{//System.out.println("doDetach");
		states.clear();
		states.push(new State(msAgent, MSAgentActionEvent.DETACH));

		notify();
	}	
	
	private static native void Detach();	
	private static native void SetLanguageID(long zLanguageID);
	private static native int CreateAvatar(String sName);
	private static native long MoveTo(int zAvatarID, int zX, int zY, int zSpeed);
	private static native long Show(int zAvatarID, boolean bFast);
	private static native long Hide(int zAvatarID, boolean bFast);
	private static native long GestureAt(int zAvatarID, int zX, int zY);
	private static native long SpeakText(int zAvatarID, String sText);
	private static native long SpeakAudio(int zAvatarID, String sText);
	private static native long Think(int zAvatarID, String sThink);
	private static native long PlayAnimation(int zAvatarID, String sAnimation);
}	
package msagent.characters;

/***
 * A singleton class which represents the MS Agent character Robby.
 * 
 * @see msagent.MSAgent
 * @see Character
 * @see Animation
 * @author Ramon Hagenaars
 */
public class Robby extends Character
{
	public static final Animation ACKNOWLEDGE = new Animation("Acknowledge", Robby.getInstance());
	public static final Animation ALERT = new Animation("Alert", Robby.getInstance());
	public static final Animation ANNOUNCE = new Animation("Announce", Robby.getInstance());
	public static final Animation BLINK = new Animation("Blink", Robby.getInstance());
	public static final Animation CONFUSED = new Animation("Confused", Robby.getInstance());
	public static final Animation CONGRATULATE = new Animation("Congratulate", Robby.getInstance());
	public static final Animation DECLINE = new Animation("Decline", Robby.getInstance());
	public static final Animation DOMAGIC1 = new Animation("DoMagic1", Robby.getInstance());
	public static final Animation DOMAGIC2 = new Animation("DoMagic2", Robby.getInstance());
	public static final Animation DONTRECOGNIZE = new Animation("DontRecognize", Robby.getInstance());
	public static final Animation EXPLAIN = new Animation("Explain", Robby.getInstance());
	public static final Animation GESTUREDOWN = new Animation("GestureDown", Robby.getInstance());
	public static final Animation GESTURELEFT = new Animation("GestureLeft", Robby.getInstance());
	public static final Animation GESTURERIGHT = new Animation("GestureRight", Robby.getInstance());
	public static final Animation GESTUREUP = new Animation("GestureUp", Robby.getInstance());
	public static final Animation GETATTENTION = new Animation("GetAttention", Robby.getInstance());
	public static final Animation GETATTENTIONCONTINUED = new Animation("GetAttentionContinued", Robby.getInstance());
	public static final Animation GETATTENTIONRETURN = new Animation("GetAttentionReturn", Robby.getInstance());
	public static final Animation GREET = new Animation("Greet", Robby.getInstance());
	public static final Animation HEARING_1 = new Animation("Hearing_1", Robby.getInstance());
	public static final Animation HEARING_2 = new Animation("Hearing_2", Robby.getInstance());
	public static final Animation HEARING_3 = new Animation("Hearing_3", Robby.getInstance());
	public static final Animation HEARING_4 = new Animation("Hearing_4", Robby.getInstance());
	public static final Animation HIDE = new Animation("Hide", Robby.getInstance());
	public static final Animation IDLE1_1 = new Animation("Idle1_1", Robby.getInstance());
	public static final Animation IDLE1_2 = new Animation("Idle1_2", Robby.getInstance());
	public static final Animation IDLE1_3 = new Animation("Idle1_3", Robby.getInstance());
	public static final Animation IDLE1_4 = new Animation("Idle1_4", Robby.getInstance());
	public static final Animation IDLE2_1 = new Animation("Idle2_1", Robby.getInstance());
	public static final Animation IDLE2_2 = new Animation("Idle2_2", Robby.getInstance());
	public static final Animation IDLE3_1 = new Animation("Idle3_1", Robby.getInstance());
	public static final Animation IDLE3_2 = new Animation("Idle3_2", Robby.getInstance());
	public static final Animation LOOKDOWN = new Animation("LookDown", Robby.getInstance());
	public static final Animation LOOKDOWNRETURN = new Animation("LookDownReturn", Robby.getInstance());
	public static final Animation LOOKLEFT = new Animation("LookLeft", Robby.getInstance());
	public static final Animation LOOKLEFTRETURN = new Animation("LookLeftReturn", Robby.getInstance());
	public static final Animation LOOKRIGHT = new Animation("LookRight", Robby.getInstance());
	public static final Animation LOOKRIGHTRETURN = new Animation("LookRightReturn", Robby.getInstance());
	public static final Animation LOOKUP = new Animation("LookUp", Robby.getInstance());
	public static final Animation LOOKUPRETURN = new Animation("LookUpReturn", Robby.getInstance());
	public static final Animation MOVEDOWN = new Animation("MoveDown", Robby.getInstance());
	public static final Animation MOVELEFT = new Animation("MoveLeft", Robby.getInstance());
	public static final Animation MOVERIGHT = new Animation("MoveRight", Robby.getInstance());
	public static final Animation MOVEUP = new Animation("MoveUp", Robby.getInstance());
	public static final Animation PLEASED = new Animation("Pleased", Robby.getInstance());
	public static final Animation PROCESS = new Animation("Process", Robby.getInstance());
	public static final Animation PROCESSING = new Animation("Processing", Robby.getInstance());
	public static final Animation READ = new Animation("Read", Robby.getInstance());
	public static final Animation READCONTINUED = new Animation("ReadContinued", Robby.getInstance());
	public static final Animation READRETURN = new Animation("ReadReturn", Robby.getInstance());
	public static final Animation READING = new Animation("Reading", Robby.getInstance());
	public static final Animation RESTPOSE = new Animation("RestPose", Robby.getInstance());
	public static final Animation SAD = new Animation("Sad", Robby.getInstance());
	public static final Animation SEARCH = new Animation("Search", Robby.getInstance());
	public static final Animation SEARCHING = new Animation("Searching", Robby.getInstance());
	public static final Animation SHOW = new Animation("Show", Robby.getInstance());
	public static final Animation STARTLISTENING = new Animation("StartListening", Robby.getInstance());
	public static final Animation STOPLISTENING = new Animation("StopListening", Robby.getInstance());
	public static final Animation SUGGEST = new Animation("Suggest", Robby.getInstance());
	public static final Animation SURPRISED = new Animation("Surprised", Robby.getInstance());
	public static final Animation THINK = new Animation("Think", Robby.getInstance());
	public static final Animation THINKING = new Animation("Thinking", Robby.getInstance());
	public static final Animation UNCERTAIN = new Animation("Uncertain", Robby.getInstance());
	public static final Animation WAVE = new Animation("Wave", Robby.getInstance());
	public static final Animation WRITE = new Animation("Write", Robby.getInstance());
	public static final Animation WRITECONTINUED = new Animation("WriteContinued", Robby.getInstance());
	public static final Animation WRITERETURN = new Animation("WriteReturn", Robby.getInstance());
	public static final Animation WRITING = new Animation("Writing", Robby.getInstance());
	
	/***
	 * The one and only instance of Robby.
	 */
	protected static Robby instance;
	
	/***
	 * Constructor; instantiates a new Merlin.
	 */
	protected Robby()
	{
		super("Robby");
	}

	/***
	 * Returns the one and only instance of Robby.
	 * @return the Robby object
	 */
	public static Robby getInstance()
	{
		if (instance == null)
			instance = new Robby();
		
		return instance;
	}
}

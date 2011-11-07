package msagent.characters;


/***
 * A singleton class which represents the MS Agent character Merlin.
 * 
 * @see msagent.MSAgent
 * @see Character
 * @see Animation
 * @author Ramon Hagenaars
 */
public class Merlin extends Character
{
	public static final Animation ACKNOWLEDGE = new Animation("Acknowledge", Merlin.getInstance());
	public static final Animation ALERT = new Animation("Alert", Merlin.getInstance());
	public static final Animation ANNOUNCE = new Animation("Announce", Merlin.getInstance());
	public static final Animation BLINK = new Animation("Blink", Merlin.getInstance());
	public static final Animation CONFUSED = new Animation("Confused", Merlin.getInstance());
	public static final Animation CONGRATULATE = new Animation("Congratulate", Merlin.getInstance());
	public static final Animation CONGRATULATE_2 = new Animation("Congratulate_2", Merlin.getInstance());
	public static final Animation DECLINE = new Animation("Decline", Merlin.getInstance());
	public static final Animation DOMAGIC1 = new Animation("DoMagic1", Merlin.getInstance());
	public static final Animation DOMAGIC2 = new Animation("DoMagic2", Merlin.getInstance());
	public static final Animation DONTRECOGNIZE = new Animation("DontRecognize", Merlin.getInstance());
	public static final Animation EXPLAIN = new Animation("Explain", Merlin.getInstance());
	public static final Animation GESTUREDOWN = new Animation("GestureDown", Merlin.getInstance());
	public static final Animation GESTURELEFT = new Animation("GestureLeft", Merlin.getInstance());
	public static final Animation GESTURERIGHT = new Animation("GestureRight", Merlin.getInstance());
	public static final Animation GESTUREUP = new Animation("GestureUp", Merlin.getInstance());
	public static final Animation GETATTENTION = new Animation("GetAttention", Merlin.getInstance());
	public static final Animation GETATTENTIONCONTINUED = new Animation("GetAttentionContinued", Merlin.getInstance());
	public static final Animation GETATTENTIONRETURN = new Animation("GetAttentionReturn", Merlin.getInstance());
	public static final Animation GREET = new Animation("Greet", Merlin.getInstance());
	public static final Animation HEARING_1 = new Animation("Hearing_1", Merlin.getInstance());
	public static final Animation HEARING_2 = new Animation("Hearing_2", Merlin.getInstance());
	public static final Animation HEARING_3 = new Animation("Hearing_3", Merlin.getInstance());
	public static final Animation HEARING_4 = new Animation("Hearing_4", Merlin.getInstance());
	public static final Animation HIDE = new Animation("Hide", Merlin.getInstance());
	public static final Animation IDLE1_1 = new Animation("Idle1_1", Merlin.getInstance());
	public static final Animation IDLE1_2 = new Animation("Idle1_2", Merlin.getInstance());
	public static final Animation IDLE1_3 = new Animation("Idle1_3", Merlin.getInstance());
	public static final Animation IDLE1_4 = new Animation("Idle1_4", Merlin.getInstance());
	public static final Animation IDLE2_1 = new Animation("Idle2_1", Merlin.getInstance());
	public static final Animation IDLE2_2 = new Animation("Idle2_2", Merlin.getInstance());
	public static final Animation IDLE3_1 = new Animation("Idle3_1", Merlin.getInstance());
	public static final Animation IDLE3_2 = new Animation("Idle3_2", Merlin.getInstance());
	public static final Animation LOOKDOWN = new Animation("LookDown", Merlin.getInstance());
	public static final Animation LOOKDOWNBLINK = new Animation("LookDownBlink", Merlin.getInstance());
	public static final Animation LOOKDOWNRETURN = new Animation("LookDownReturn", Merlin.getInstance());
	public static final Animation LOOKLEFT = new Animation("LookLeft", Merlin.getInstance());
	public static final Animation LOOKLEFTBLINK = new Animation("LookLeftBlink", Merlin.getInstance());
	public static final Animation LOOKLEFTRETURN = new Animation("LookLeftReturn", Merlin.getInstance());
	public static final Animation LOOKRIGHT = new Animation("LookRight", Merlin.getInstance());
	public static final Animation LOOKRIGHTBLINK = new Animation("LookRightBlink", Merlin.getInstance());
	public static final Animation LOOKRIGHTRETURN = new Animation("LookRightReturn", Merlin.getInstance());
	public static final Animation LOOKUP = new Animation("LookUp", Merlin.getInstance());
	public static final Animation LOOKUPBLINK = new Animation("LookUpBlink", Merlin.getInstance());
	public static final Animation LOOKUPRETURN = new Animation("LookUpReturn", Merlin.getInstance());
	public static final Animation MOVEDOWN = new Animation("MoveDown", Merlin.getInstance());
	public static final Animation MOVELEFT = new Animation("MoveLeft", Merlin.getInstance());
	public static final Animation MOVERIGHT = new Animation("MoveRight", Merlin.getInstance());
	public static final Animation MOVEUP = new Animation("MoveUp", Merlin.getInstance());
	public static final Animation PLEASED = new Animation("Pleased", Merlin.getInstance());
	public static final Animation PROCESS = new Animation("Process", Merlin.getInstance());
	public static final Animation PROCESSING = new Animation("Processing", Merlin.getInstance());
	public static final Animation READ = new Animation("Read", Merlin.getInstance());
	public static final Animation READCONTINUED = new Animation("ReadContinued", Merlin.getInstance());
	public static final Animation READRETURN = new Animation("ReadReturn", Merlin.getInstance());
	public static final Animation READING = new Animation("Reading", Merlin.getInstance());
	public static final Animation RESTPOSE = new Animation("RestPose", Merlin.getInstance());
	public static final Animation SAD = new Animation("Sad", Merlin.getInstance());
	public static final Animation SEARCH = new Animation("Search", Merlin.getInstance());
	public static final Animation SEARCHING = new Animation("Searching", Merlin.getInstance());
	public static final Animation SHOW = new Animation("Show", Merlin.getInstance());
	public static final Animation STARTLISTENING = new Animation("StartListening", Merlin.getInstance());
	public static final Animation STOPLISTENING = new Animation("StopListening", Merlin.getInstance());
	public static final Animation SUGGEST = new Animation("Suggest", Merlin.getInstance());
	public static final Animation SURPRISED = new Animation("Surprised", Merlin.getInstance());
	public static final Animation THINK = new Animation("Think", Merlin.getInstance());
	public static final Animation THINKING = new Animation("Thinking", Merlin.getInstance());
	public static final Animation UNCERTAIN = new Animation("Uncertain", Merlin.getInstance());
	public static final Animation WAVE = new Animation("Wave", Merlin.getInstance());
	public static final Animation WRITE = new Animation("Write", Merlin.getInstance());
	public static final Animation WRITECONTINUED = new Animation("WriteContinued", Merlin.getInstance());
	public static final Animation WRITERETURN = new Animation("WriteReturn", Merlin.getInstance());
	public static final Animation WRITING = new Animation("Writing", Merlin.getInstance());
	
	/***
	 * The one and only instance of Merlin.
	 */
	protected static Merlin instance;
	
	/***
	 * Constructor; instantiates a new Merlin.
	 */
	protected Merlin()
	{
		super("Merlin");
	}

	/***
	 * Returns the one and only instance of Merlin.
	 * @return the Merlin object
	 */
	public static Merlin getInstance()
	{
		if (instance == null)
			instance = new Merlin();
		
		return instance;
	}
}

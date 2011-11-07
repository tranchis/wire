package msagent.characters;

/***
 * A singleton class which represents the MS Agent character Genie.
 *
 * @see msagent.MSAgent
 * @see Character
 * @see Animation
 * @author Ramon Hagenaars
 */
public class Genie extends Character
{
	public static final Animation ACKNOWLEDGE = new Animation("Acknowledge", Genie.getInstance());
	public static final Animation ALERT = new Animation("Alert", Genie.getInstance());
	public static final Animation ANNOUNCE = new Animation("Announce", Genie.getInstance());
	public static final Animation BLINK = new Animation("Blink", Genie.getInstance());
	public static final Animation CONFUSED = new Animation("Confused", Genie.getInstance());
	public static final Animation CONGRATULATE = new Animation("Congratulate", Genie.getInstance());
	public static final Animation CONGRATULATE_2 = new Animation("Congratulate_2", Genie.getInstance());
	public static final Animation DECLINE = new Animation("Decline", Genie.getInstance());
	public static final Animation DOMAGIC1 = new Animation("DoMagic1", Genie.getInstance());
	public static final Animation DOMAGIC2 = new Animation("DoMagic2", Genie.getInstance());
	public static final Animation DONTRECOGNIZE = new Animation("DontRecognize", Genie.getInstance());
	public static final Animation EXPLAIN = new Animation("Explain", Genie.getInstance());
	public static final Animation GESTUREDOWN = new Animation("GestureDown", Genie.getInstance());
	public static final Animation GESTURELEFT = new Animation("GestureLeft", Genie.getInstance());
	public static final Animation GESTURERIGHT = new Animation("GestureRight", Genie.getInstance());
	public static final Animation GESTUREUP = new Animation("GestureUp", Genie.getInstance());
	public static final Animation GETATTENTION = new Animation("GetAttention", Genie.getInstance());
	public static final Animation GETATTENTIONCONTINUED = new Animation("GetAttentionContinued", Genie.getInstance());
	public static final Animation GETATTENTIONRETURN = new Animation("GetAttentionReturn", Genie.getInstance());
	public static final Animation GREET = new Animation("Greet", Genie.getInstance());
	public static final Animation HEARING_1 = new Animation("Hearing_1", Genie.getInstance());
	public static final Animation HEARING_2 = new Animation("Hearing_2", Genie.getInstance());
	public static final Animation HEARING_3 = new Animation("Hearing_3", Genie.getInstance());
	public static final Animation HEARING_4 = new Animation("Hearing_4", Genie.getInstance());
	public static final Animation HIDE = new Animation("Hide", Genie.getInstance());
	public static final Animation IDLE1_1 = new Animation("Idle1_1", Genie.getInstance());
	public static final Animation IDLE1_2 = new Animation("Idle1_2", Genie.getInstance());
	public static final Animation IDLE1_3 = new Animation("Idle1_3", Genie.getInstance());
	public static final Animation IDLE1_4 = new Animation("Idle1_4", Genie.getInstance());
	public static final Animation IDLE1_5 = new Animation("Idle1_5", Genie.getInstance());
	public static final Animation IDLE1_6 = new Animation("Idle1_6", Genie.getInstance());
	public static final Animation IDLE2_1 = new Animation("Idle2_1", Genie.getInstance());
	public static final Animation IDLE2_2 = new Animation("Idle2_2", Genie.getInstance());
	public static final Animation IDLE2_3 = new Animation("Idle2_3", Genie.getInstance());
	public static final Animation IDLE3_1 = new Animation("Idle3_1", Genie.getInstance());
	public static final Animation IDLE3_2 = new Animation("Idle3_2", Genie.getInstance());
	public static final Animation LOOKDOWN = new Animation("LookDown", Genie.getInstance());
	public static final Animation LOOKDOWNBLINK = new Animation("LookDownBlink", Genie.getInstance());
	public static final Animation LOOKDOWNRETURN = new Animation("LookDownReturn", Genie.getInstance());
	public static final Animation LOOKLEFT = new Animation("LookLeft", Genie.getInstance());
	public static final Animation LOOKLEFTBLINK = new Animation("LookLeftBlink", Genie.getInstance());
	public static final Animation LOOKLEFTRETURN = new Animation("LookLeftReturn", Genie.getInstance());
	public static final Animation LOOKRIGHT = new Animation("LookRight", Genie.getInstance());
	public static final Animation LOOKRIGHTBLINK = new Animation("LookRightBlink", Genie.getInstance());
	public static final Animation LOOKRIGHTRETURN = new Animation("LookRightReturn", Genie.getInstance());
	public static final Animation LOOKUP = new Animation("LookUp", Genie.getInstance());
	public static final Animation LOOKUPBLINK = new Animation("LookUpBlink", Genie.getInstance());
	public static final Animation LOOKUPRETURN = new Animation("LookUpReturn", Genie.getInstance());
	public static final Animation MOVEDOWN = new Animation("MoveDown", Genie.getInstance());
	public static final Animation MOVELEFT = new Animation("MoveLeft", Genie.getInstance());
	public static final Animation MOVERIGHT = new Animation("MoveRight", Genie.getInstance());
	public static final Animation MOVEUP = new Animation("MoveUp", Genie.getInstance());
	public static final Animation PLEASED = new Animation("Pleased", Genie.getInstance());
	public static final Animation PROCESS = new Animation("Process", Genie.getInstance());
	public static final Animation PROCESSING = new Animation("Processing", Genie.getInstance());
	public static final Animation READ = new Animation("Read", Genie.getInstance());
	public static final Animation READCONTINUED = new Animation("ReadContinued", Genie.getInstance());
	public static final Animation READRETURN = new Animation("ReadReturn", Genie.getInstance());
	public static final Animation READING = new Animation("Reading", Genie.getInstance());
	public static final Animation RESTPOSE = new Animation("RestPose", Genie.getInstance());
	public static final Animation SAD = new Animation("Sad", Genie.getInstance());
	public static final Animation SEARCH = new Animation("Search", Genie.getInstance());
	public static final Animation SEARCHING = new Animation("Searching", Genie.getInstance());
	public static final Animation SHOW = new Animation("Show", Genie.getInstance());
	public static final Animation STARTLISTENING = new Animation("StartListening", Genie.getInstance());
	public static final Animation STOPLISTENING = new Animation("StopListening", Genie.getInstance());
	public static final Animation SUGGEST = new Animation("Suggest", Genie.getInstance());
	public static final Animation SURPRISED = new Animation("Surprised", Genie.getInstance());
	public static final Animation THINK = new Animation("Think", Genie.getInstance());
	public static final Animation THINKING = new Animation("Thinking", Genie.getInstance());
	public static final Animation UNCERTAIN = new Animation("Uncertain", Genie.getInstance());
	public static final Animation WAVE = new Animation("Wave", Genie.getInstance());
	public static final Animation WRITE = new Animation("Write", Genie.getInstance());
	public static final Animation WRITECONTINUED = new Animation("WriteContinued", Genie.getInstance());
	public static final Animation WRITERETURN = new Animation("WriteReturn", Genie.getInstance());
	public static final Animation WRITING = new Animation("Writing", Genie.getInstance());

	/***
	 * The one and only instance of Genie.
	 */	
	protected static Genie instance;
	
	/***
	 * Constructor; instantiates a new Genie.
	 */	
	protected Genie()
	{
		super("Genie");
	}
	
	/***
	 * Returns the one and only instance of Genie.
	 * @return the Genie object
	 */
	public static Genie getInstance()
	{
		if (instance == null)
			instance = new Genie();
		
		return instance;
	}
}

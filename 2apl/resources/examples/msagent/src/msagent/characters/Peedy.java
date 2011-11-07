package msagent.characters;

/***
 * A singleton class which represents the MS Agent character Peedy.
 * 
 * @see msagent.MSAgent
 * @see Character
 * @see Animation
 * @author Ramon Hagenaars
 */
public class Peedy extends Character
{
	public static final Animation ACKNOWLEDGE = new Animation("Acknowledge", Peedy.getInstance());
	public static final Animation ALERT = new Animation("Alert", Peedy.getInstance());
	public static final Animation ANNOUNCE = new Animation("Announce", Peedy.getInstance());
	public static final Animation BLINK = new Animation("Blink", Peedy.getInstance());
	public static final Animation CONFUSED = new Animation("Confused", Peedy.getInstance());
	public static final Animation CONGRATULATE = new Animation("Congratulate", Peedy.getInstance());
	public static final Animation DECLINE = new Animation("Decline", Peedy.getInstance());
	public static final Animation DOMAGIC1 = new Animation("DoMagic1", Peedy.getInstance());
	public static final Animation DOMAGIC2 = new Animation("DoMagic2", Peedy.getInstance());
	public static final Animation DONTRECOGNIZE = new Animation("DontRecognize", Peedy.getInstance());
	public static final Animation EXPLAIN = new Animation("Explain", Peedy.getInstance());
	public static final Animation GESTUREDOWN = new Animation("GestureDown", Peedy.getInstance());
	public static final Animation GESTURELEFT = new Animation("GestureLeft", Peedy.getInstance());
	public static final Animation GESTURERIGHT = new Animation("GestureRight", Peedy.getInstance());
	public static final Animation GESTUREUP = new Animation("GestureUp", Peedy.getInstance());
	public static final Animation GETATTENTION = new Animation("GetAttention", Peedy.getInstance());
	public static final Animation GETATTENTIONCONTINUED = new Animation("GetAttentionContinued", Peedy.getInstance());
	public static final Animation GETATTENTIONRETURN = new Animation("GetAttentionReturn", Peedy.getInstance());
	public static final Animation GREET = new Animation("Greet", Peedy.getInstance());
	public static final Animation HEARING_1 = new Animation("Hearing_1", Peedy.getInstance());
	public static final Animation HEARING_2 = new Animation("Hearing_2", Peedy.getInstance());
	public static final Animation HEARING_3 = new Animation("Hearing_3", Peedy.getInstance());
	public static final Animation HIDE = new Animation("Hide", Peedy.getInstance());
	public static final Animation IDLE1_1 = new Animation("Idle1_1", Peedy.getInstance());
	public static final Animation IDLE1_2 = new Animation("Idle1_2", Peedy.getInstance());
	public static final Animation IDLE1_3 = new Animation("Idle1_3", Peedy.getInstance());
	public static final Animation IDLE1_4 = new Animation("Idle1_4", Peedy.getInstance());
	public static final Animation IDLE1_5 = new Animation("Idle1_5", Peedy.getInstance());
	public static final Animation IDLE2_1 = new Animation("Idle2_1", Peedy.getInstance());
	public static final Animation IDLE2_2 = new Animation("Idle2_2", Peedy.getInstance());
	public static final Animation IDLE3_1 = new Animation("Idle3_1", Peedy.getInstance());
	public static final Animation IDLE3_2 = new Animation("Idle3_2", Peedy.getInstance());
	public static final Animation IDLE3_3 = new Animation("Idle3_3", Peedy.getInstance());
	public static final Animation LOOKDOWN = new Animation("LookDown", Peedy.getInstance());
	public static final Animation LOOKDOWNBLINK = new Animation("LookDownBlink", Peedy.getInstance());
	public static final Animation LOOKDOWNRETURN = new Animation("LookDownReturn", Peedy.getInstance());
	public static final Animation LOOKDOWNLEFT = new Animation("LookDownLeft", Peedy.getInstance());
	public static final Animation LOOKDOWNLEFTBLINK = new Animation("LookDownLeftBlink", Peedy.getInstance());
	public static final Animation LOOKDOWNLEFTRETURN = new Animation("LookDownLeftReturn", Peedy.getInstance());
	public static final Animation LOOKDOWNRIGHT = new Animation("LookDownRight", Peedy.getInstance());
	public static final Animation LOOKDOWNRIGHTBLINK = new Animation("LookDownRightBlink", Peedy.getInstance());
	public static final Animation LOOKDOWNRIGHTRETURN = new Animation("LookDownRightReturn", Peedy.getInstance());
	public static final Animation LOOKLEFT = new Animation("LookLeft", Peedy.getInstance());
	public static final Animation LOOKLEFTBLINK = new Animation("LookLeftBlink", Peedy.getInstance());
	public static final Animation LOOKLEFTRETURN = new Animation("LookLeftReturn", Peedy.getInstance());
	public static final Animation LOOKRIGHT = new Animation("LookRight", Peedy.getInstance());
	public static final Animation LOOKRIGHTBLINK = new Animation("LookRightBlink", Peedy.getInstance());
	public static final Animation LOOKRIGHTRETURN = new Animation("LookRightReturn", Peedy.getInstance());
	public static final Animation LOOKUP = new Animation("LookUp", Peedy.getInstance());
	public static final Animation LOOKUPBLINK = new Animation("LookUpBlink", Peedy.getInstance());
	public static final Animation LOOKUPRETURN = new Animation("LookUpReturn", Peedy.getInstance());
	public static final Animation LOOKUPLEFT = new Animation("LookUpLeft", Peedy.getInstance());
	public static final Animation LOOKUPLEFTBLINK = new Animation("LookUpLeftBlink", Peedy.getInstance());
	public static final Animation LOOKUPLEFTRETURN = new Animation("LookUpLeftReturn", Peedy.getInstance());
	public static final Animation LOOKUPRIGHT = new Animation("LookUpRight", Peedy.getInstance());
	public static final Animation LOOKUPRIGHTBLINK = new Animation("LookUpRightBlink", Peedy.getInstance());
	public static final Animation LOOKUPRIGHTRETURN = new Animation("LookUpRightReturn", Peedy.getInstance());
	public static final Animation MOVEDOWN = new Animation("MoveDown", Peedy.getInstance());
	public static final Animation MOVELEFT = new Animation("MoveLeft", Peedy.getInstance());
	public static final Animation MOVERIGHT = new Animation("MoveRight", Peedy.getInstance());
	public static final Animation MOVEUP = new Animation("MoveUp", Peedy.getInstance());
	public static final Animation PLEASED = new Animation("Pleased", Peedy.getInstance());
	public static final Animation PROCESS = new Animation("Process", Peedy.getInstance());
	public static final Animation PROCESSING = new Animation("Processing", Peedy.getInstance());
	public static final Animation READ = new Animation("Read", Peedy.getInstance());
	public static final Animation READCONTINUED = new Animation("ReadContinued", Peedy.getInstance());
	public static final Animation READRETURN = new Animation("ReadReturn", Peedy.getInstance());
	public static final Animation READING = new Animation("Reading", Peedy.getInstance());
	public static final Animation RESTPOSE = new Animation("RestPose", Peedy.getInstance());
	public static final Animation SAD = new Animation("Sad", Peedy.getInstance());
	public static final Animation SEARCH = new Animation("Search", Peedy.getInstance());
	public static final Animation SEARCHING = new Animation("Searching", Peedy.getInstance());
	public static final Animation SHOW = new Animation("Show", Peedy.getInstance());
	public static final Animation STARTLISTENING = new Animation("StartListening", Peedy.getInstance());
	public static final Animation STOPLISTENING = new Animation("StopListening", Peedy.getInstance());
	public static final Animation SUGGEST = new Animation("Suggest", Peedy.getInstance());
	public static final Animation SURPRISED = new Animation("Surprised", Peedy.getInstance());
	public static final Animation THINK = new Animation("Think", Peedy.getInstance());
	public static final Animation THINKING = new Animation("Thinking", Peedy.getInstance());
	public static final Animation UNCERTAIN = new Animation("Uncertain", Peedy.getInstance());
	public static final Animation WAVE = new Animation("Wave", Peedy.getInstance());
	public static final Animation WRITE = new Animation("Write", Peedy.getInstance());
	public static final Animation WRITECONTINUED = new Animation("WriteContinued", Peedy.getInstance());
	public static final Animation WRITERETURN = new Animation("WriteReturn", Peedy.getInstance());
	public static final Animation WRITING = new Animation("Writing", Peedy.getInstance());

	/***
	 * The one and only instance of Peedy.
	 */
	protected static Peedy instance;
	
	/***
	 * Constructor; instantiates a new Peedy.
	 */
	protected Peedy()
	{
		super("Peedy");
	}

	/***
	 * Returns the one and only instance of Peedy.
	 * @return the Peedy object
	 */
	public static Peedy getInstance()
	{
		if (instance == null)
			instance = new Peedy();
		
		return instance;
	}
}

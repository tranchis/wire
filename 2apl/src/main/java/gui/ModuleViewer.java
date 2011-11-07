package gui;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import apapl.APLModule;
import apapl.deliberation.DeliberationResult;
import apapl.program.Rule;
import apapl.program.Rulebase;

public class ModuleViewer extends Viewer
{
	public final static	Color color1 = new Color(255,255,255);
	public final static	Color color2 =  new Color(230,230,230);
	
	private static String selectedTab = null;
	
	private JSplitPane belief_goal, overview;
	private JTabbedPane tabPane = this;
	private boolean rtf = false;
	private RTFFrame belief, goal, plans;
	private APLModule module;
	
	private final int dividerSize = 2;
	private static double[] divs = {0.5,0.5,0.5};
	private StateTracer tracer;
		
	private LogWindow log = new LogWindow();
	
	private StateHistory history;
	private int statenr = 0;
	
	private boolean tracerEnabled = true;
	private boolean logEnabled = true;
	
	
	public ModuleViewer(APLModule module, GUI gui, boolean tracerEnabled, boolean logEnabled)
	{
		super();
		this.module = module;
		
		belief = new RTFFrame("Beliefbase",rtf);
		goal  = new RTFFrame("Goalbase",rtf);
		plans = new RTFFrame("Planbase",rtf);
		
		history = new StateHistory();
		tracer = new StateTracer(history);
	
		SwingUtilities.updateComponentTreeUI(this);
		
		belief_goal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,belief,goal);
		belief_goal.setDividerLocation(divs[0]);
		belief_goal.setResizeWeight(divs[0]);
				
		overview = new JSplitPane(JSplitPane.VERTICAL_SPLIT,belief_goal,plans);
		overview.setDividerSize(dividerSize);
		overview.setDividerLocation(divs[2]);
		overview.setResizeWeight(divs[2]);
		useSH(gui.useSH());
		
		LinkedList<String> warnings = module.check();
		tabPane.add("Overview", overview);
		logState(null);
		update();
			
		if (showBases)
		{	if (!module.getBeliefUpdates().isEmpty()) addTab("Belief updates",makeRuleViewer(module.getBeliefUpdates()));
			if (!module.getPGrulebase().isEmpty()) addTab("PG rules",makeRuleViewer(module.getPGrulebase()));
			if (!module.getPCrulebase().isEmpty()) addTab("PC rules",makeRuleViewer(module.getPCrulebase()));
			if (!module.getPRrulebase().isEmpty()) addTab("PR rules",makeRuleViewer(module.getPRrulebase()));
		}
		if (warnings.size()!=0) showWarnings();
		
		addTab("State Tracer", tracer);
		tracer.update(false);	
		
    	addTab("Log", new JScrollPane(log)); 
    	
		setTracerEnabled(tracerEnabled);
		setLogEnabled(logEnabled);
	}
	
	private void showWarnings()
	{
		LinkedList<String> warnigs = module.check();
		RTFFrame r = new RTFFrame(true);
		r.update(warningsToRTF(warnigs));
		add("Warnings",r);
		select(r);
	}
	
	public void useSH(boolean useSH)
	{
		rtf = useSH;
		belief.setRTF(useSH);
		goal.setRTF(useSH);
		plans.setRTF(useSH);
	}
	
	private JComponent makeRuleViewer(Rulebase<? extends Rule> base)
	{
		int i = 0;
		String[] s = new String[base.getRules().size()];
		for (Rule rule : base.getRules()) {
			s[i] = rule.toRTF();
			i++;
		}
					
		JList list = new JList(s);
		list.setBackground(color1);
		list.setCellRenderer(new BaseListCellRenderer(base.getRules().size()%2==0?color2:color1,base.getRules().size()%2==0?color1:color2,false,true));
		return new JScrollPane(list);
	}

	public void logState(DeliberationResult result)
	{
		if (tracerEnabled) {
			synchronized(module)
			{	
				history.addPart(StateHistory.BELIEFS, statenr, module.getBeliefbase().toString());
				history.addPart(StateHistory.GOALS, statenr, module.getGoalbase().toString());
				history.addPart(StateHistory.PLANS, statenr, module.getPlanbase().toString());
			}
			
			if (result == null){
				history.addPart(StateHistory.LOGS, statenr, "Initial State");
			}		
			else
			{
				history.addPart(StateHistory.LOGS, statenr, result.stepName());
			}
			statenr++;
		}
		
		if (logEnabled) {
			if (result != null)
			{
				log.showDeliberationResult(result);
			} 
		}
	}
	

	public void update()
	{
		if (getSelectedComponent() == tracer)
		{			
			tracer.update(true);
		}
        
		if (getSelectedComponent() == overview || getSelectedIndex() == 0)
		{
			// Avoid modifications of the module while displaying the module's state
			synchronized (module)
			{
				if (rtf)
				{
					belief.update(module.getBeliefbase().toRTF());
					goal.update(module.getGoalbase().toRTF());
					plans.update(module.getPlanbase().toRTF());
				} else
				{
					belief.update(module.getBeliefbase().toString());
					goal.update(module.getGoalbase().toString());
					plans.update(module.getPlanbase().toString());
				}
			}
		}
	}
	
	private String warningsToRTF(LinkedList<String> warnings)
	{
		String r = "";
			for (String s : warnings) {
				r = r + "\\b -\\b0  " + s + "\\par\n";
			}
		return r;
	}
	
	/**
	 * Converts the the list of errors to an RTF string containing sytax highlighting colors.
	 * @param The list of errors.
	 * @return A String containing the errors in RTF format.
	 */
	private String errorsToRTF(LinkedList<String> errors)
	{
		String r = "";
		boolean quote = false;
		int bla=1;
		for (String s : errors) {
			int d = s.indexOf(':');
			if (d>=0) {
				String head = s.substring(0,d);
				s = s.substring(d+1).trim();
				if (head.equals("apapl.parser.ParseException")) head = "Parse error";
				else if (head.equals("apapl.parser.TokenMgrError")) head = "Parse error";
				else if (head.equals("com.ugos.JIProlog.engine.JIPSyntaxErrorException")) head = "Parse error in beliefs";
				r = r + "\\cf7 "+head+":\\cf0 \\par\n";
			}
			else r = r + "\\cf7 Error:\\cf0 \\par\n";
			
			int a = 0;
			for (int i=0; i<s.length(); i++) {
				bla++;
				char c = s.charAt(i);
				
				if (c=='\n') {
					r = r + s.substring(a,i) + "\\par\n";
					a = i+1;
				}
				else if (c=='<'&&!quote) {
					r = r + s.substring(a,i) + "\\cf5 <";
					a = i+1;
				}
				else if (c=='>'&&!quote) {
					r = r + s.substring(a,i) + ">\\cf0 ";
					a = i+1;
				}
				else if (c=='\"') {
					if (i>a) r = r + s.substring(a,i);
					r = r + (quote?"\"\\cf0 ":"\\cf6 \"");
					a = i+1;
					quote = !quote;
				}
				else if (c=='{') {
					if (i>a) r = r + s.substring(a,i);
					r = r + "\\{";
					a = i+1;
				}
				else if (c=='}') {
					if (i>a) r = r + s.substring(a,i);
					r = r + "\\}";
					a = i+1;
				}
				else if (!isLetterOrDigit(c)) {
					if (i>a) {
						r = r + s.substring(a,i);
						a = i;
					}
				}
			}
			r = r + s.substring(a,s.length())+"\n\n";
		}
		return r;
	}
	
	private boolean isLetterOrDigit(char c)
	{
		return	c>='a' && c<='z'
		||		c>='A' && c<='Z'
		||		c>='0' && c<='9';
	}
	
	public void setSelectedIndex(int index)
	{
		super.setSelectedIndex(index);
		
		// A little hack:
		// JTabbedPane component calls this method to select the first tab added to the tabbed pane.
		// However, the selectedTab field is defined as static and therefore common for all the module viewers.
		// This initial call would cause all viewers to switch to the first tab whenever a new module is created.
		//
		// Thus the selection is only registered if the viewer is fully loaded i.e.  if it contains more than 3 tabs.
		
		if (this.getTabCount() >= 3 || selectedTab == null)
			selectedTab = getTitleAt(index);
	
		if (selectedTab.equals("State Tracer")) {
			tracer.isSelected();
			tracer.update(false);
		}
		else if (selectedTab.equals("Overview")) {
			update();
		}
	}
	
	public void doLayout()
	{
		super.doLayout();
		
		if (selectedTab != null) {
			int t = indexOfTab(selectedTab);
			if (t >= 0 && t < getTabCount()) 
				setSelectedIndex(t);
		}
	}

	/**
	 * Returns the module that the viewer represents
	 * @return the module
	 */
	public APLModule getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	public void setModule(APLModule module) {
		this.module = module;
	}
	
	/**
	 * Sets background of the overview editors to gray to indicate that their
	 * contents are not being updated.
	 */
	public void setNotUpToDate() {
		Color disabled = new Color(240,240,240);
		belief.setBackground(disabled);
		goal.setBackground(disabled);
		plans.setBackground(disabled);
	}
	
	/**
	 * Set background of the overview editors to white to indicate that their
	 * contents is up-to-date.
	 */
	public void setUpToDate() {
		belief.setBackground(Color.WHITE);
		goal.setBackground(Color.WHITE);
		plans.setBackground(Color.WHITE);
	}
	
	/**
	 * Informs viewer whether it should save state tracer information.
	 * 
	 * @param enabled
	 *            true if tracing information should be saved, false otherwise
	 */
	public void setTracerEnabled(boolean enabled) {
		tracerEnabled = enabled;
		
		if (enabled == false) {
			history.clear();
			tracer.update(true);
		}
		
		int i = indexOfTab("State Tracer");
		if (i != -1) {
			tabPane.setEnabledAt(i, enabled);
		}
	}
	
	/**
	 * Informs viewer whether it should save deliberation logs.
	 * 
	 * @param enabled
	 *            true if deliberation logs should be saved, false otherwise
	 */
	public void setLogEnabled(boolean enabled) {
		logEnabled = enabled;
		
		if (enabled == false) {
			log.clear();
		}
		
		int i = indexOfTab("Log");
		if (i != -1) {
			tabPane.setEnabledAt(i, enabled);
		}
	}	
}

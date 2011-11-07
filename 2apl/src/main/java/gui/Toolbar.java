package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import apapl.APLMAS;
import apapl.APLModule;
import apapl.messaging.Messenger;

public class Toolbar implements ActionListener, ItemListener
{
	private APLMAS mas;
	private GUI gui;
	private JMenuBar menubar;
	private JToolBar toolbar;
	private Messenger msgr;
	private MessageFrame f;

	/**
	 * Indicates whether buttons have effect for the entire MAS or just one
	 * selected module.
	 */
	private boolean forAll = true;
	
	/**
	 * Indicates whether overview should be updated after each deliberation step.
	 */
	private boolean autoUpdateOverviewEnabled = true;
	
	public boolean isAutoUpdateOverviewEnabled() {
		return autoUpdateOverviewEnabled;
	}

	/**
	 * Indicates whether tracing information will be saved after each step.
	 */
	private boolean tracerEnabled = true;
	
	public boolean isTracerEnabled() {
		return tracerEnabled;
	}

	/**
	 * Indicates whether log information will be saved after each step.
	 */
	private boolean logEnabled = true; 

	public boolean isLogEnabled() {
		return logEnabled;
	}

	/** Number of steps per one deliberation cycle. */
	private final int STEPS_PER_CYCLE = 6;

	/**
	 * Indicates which modules are currently being executed. To manipulate this
	 * list, use methods {@link Toolbar#modulesStartExecuting} and
	 * {@link Toolbar#modulesStoppedExecuting}.
	 */
	private Set<APLModule> runningModules = new HashSet<APLModule>();

	private Dimension sep = new Dimension(16, 16);

	private HashMap<String, AbstractButton> buttons = new HashMap<String, AbstractButton>();
	private HashMap<String, AbstractButton> menuItems = new HashMap<String, AbstractButton>();
	private ArrayList<JToggleButton> toolbarChecks = new ArrayList<JToggleButton>();
	private ArrayList<JCheckBoxMenuItem> menuChecks = new ArrayList<JCheckBoxMenuItem>();

	/**
	 * List of actions that will be disabled during the execution of the
	 * multi-agent system.
	 */
	final List<String> disableDuringExecutionCmds = Arrays.asList(
			"recompile.png", "close.png", "forall.png", "load.png", "Auto-update Overview", "Allow State Tracer", "Allow Log");

	/**
	 * List of actions that can cause start of MAS execution and are therefore
	 * affected by the state of forAll button
	 */
	final List<String> startExecutionCmds = Arrays.asList("play.png",
			"playcycle.png", "playstep.png");

	public Toolbar(GUI gui)
	{
		this.gui = gui;
		toolbar = new JToolBar();
		menubar = new JMenuBar();
		toolbar.setFloatable(false);

		// Toolbar
		addButton("load.png", "Load MAS");
		// addButton("new.png","New mas");
		addButton("close.png", "Close MAS");
		addButton("recompile.png", "Reload MAS");
		toolbar.addSeparator(sep);
		addButton("play.png", "Start deliberation");
		addButton("playcycle.png", "Perform one deliberation cycle");
		addButton("playstep.png", "Perform one deliberation step");
		addButton("pause.png", "Pause deliberation");
		addToggleButton("forall.png", "Apply to all", forAll);
		toolbar.addSeparator(sep);
		addButton("message.png", "Start message agent");
		toolbar.addSeparator(sep);
		// addToggleButton("fgdc.png","Toggle FGDC",Settings.showFGDC());

		toolbar.addSeparator(sep);

		// Menu
		JMenu file = new JMenu("File");
		addMenuItem(file, "Open...", "Open MAS file.", "load.png", KeyEvent.VK_O);
		// addMenuItem(file,"New","New mas file.","new.png",KeyEvent.VK_N);
		addMenuItem(file, "Close", "Close MAS file.", "close.png",
				KeyEvent.VK_C);
		addMenuItem(file, "Reload", "Reload MAS file.", "recompile.png",
				KeyEvent.VK_F9);
		addMenuItem(file, "Exit", "Exit the platform.", null,
				KeyEvent.VK_Q);
		menubar.add(file);

		JMenu run = new JMenu("Run");
		addMenuItem(run, "Play", "Start deliberation", "play.png",
				KeyEvent.VK_F5);
		addMenuItem(run, "Play Cycle", "Perform one cycle in deliberation.",
				"playcycle.png", KeyEvent.VK_F7);
		addMenuItem(run, "Play One", "Perform one step in deliberation.",
				"playstep.png", KeyEvent.VK_F6);
		addMenuItem(run, "Pause", "Pause Deliberation.", "pause.png",
				KeyEvent.VK_F8);
		addCheckBoxMenuItem(run, "Apply to All", "Apply to all agents.",
				"forall.png", forAll, KeyEvent.VK_A);
		menubar.add(run);

		JMenu debug = new JMenu("Debug");
		addCheckBoxMenuItem(debug, "Auto-update Overview", "If checked, the overview will be updated after each step.",
				null, autoUpdateOverviewEnabled, KeyEvent.VK_U);
		addCheckBoxMenuItem(debug, "Allow State Tracer", "Allows storing of the tracing information.",
				null, tracerEnabled, KeyEvent.VK_T);
		addCheckBoxMenuItem(debug, "Allow Log", "Allows storing of the logging information.",
				null, logEnabled, KeyEvent.VK_L);
		
		debug.addSeparator();
		
		addMenuItem(debug, "Message Agent", "Launch Message agent",
				"message.png", -1);
		menubar.add(debug);
		

		JMenu help = new JMenu("Help");
		addMenuItem(help, "About", "About 2APL", null, KeyEvent.VK_F1);
		// addMenuItem(options,"Settings","Change Settings",null,KeyEvent.VK_F2);
		menubar.add(help);
		masLoaded(false);
		setMessenger(null);
	}

	public void setMAS(APLMAS mas)
	{
		this.mas = mas;
		masLoaded(mas != null);
	}

	public void setMessenger(Messenger msgr)
	{
		this.msgr = msgr;
		buttons.get("message.png").setEnabled(msgr != null);
	}

	private void addMenuItem(JMenu m, String name, String description,
			String icon, int key)
	{
		JMenuItem menuItem;
		if (icon == null)
			menuItem = new JMenuItem(name);
		else
			menuItem = new JMenuItem(name, makeIcon(icon));
		menuItem.setActionCommand(icon == null ? name : icon);
		menuItem.addActionListener(this);
		if (key >= KeyEvent.VK_F1 && key <= KeyEvent.VK_F9)
			menuItem.setAccelerator(KeyStroke.getKeyStroke(key, 0));
		else if (key > 0)
			menuItem.setAccelerator(KeyStroke.getKeyStroke(key,
					ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		m.add(menuItem);
		menuItems.put(icon == null ? name : icon, menuItem);
	}

	private void addCheckBoxMenuItem(JMenu m, String name, String description,
			String icon, boolean checked, int key)
	{
		JCheckBoxMenuItem menuItem;
		if (icon == null) 
			menuItem = new JCheckBoxMenuItem(name, checked);
		else
			menuItem = new JCheckBoxMenuItem(name, makeIcon(icon), checked);
		
		menuItem.setActionCommand(icon == null ? name : icon);
		menuItem.addActionListener(this);
		
		if (key >= KeyEvent.VK_F1 && key <= KeyEvent.VK_F9)
			menuItem.setAccelerator(KeyStroke.getKeyStroke(key, 0));
		else if (key > 0)
			menuItem.setAccelerator(KeyStroke.getKeyStroke(key,
					ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		menuItem.addItemListener(this);
		menuChecks.add(menuItem);
		m.add(menuItem);
		menuItems.put(icon == null ? name : icon, menuItem);
	}

	public void addButton(String s, String tooltip)
	{
		JButton button = new JButton(makeIcon(s));
		button.setActionCommand(s);
		button.addActionListener(this);
		button.setToolTipText(tooltip);
		toolbar.add(button);
		buttons.put(s, button);
	}

	public void addToggleButton(String s, String tooltip, boolean checked)
	{
		JToggleButton button = new JToggleButton(makeIcon(s), checked);
		button.setActionCommand(s);
		button.addActionListener(this);
		button.setToolTipText(tooltip);
		button.addItemListener(this);
		toolbarChecks.add(button);
		toolbar.add(button);
		buttons.put(s, button);
	}

	public void addTextButton(String s, String tooltip)
	{
		JButton button = new JButton(s);
		button.setActionCommand(s);
		button.addActionListener(this);
		button.setToolTipText(tooltip);
		toolbar.add(button);
	}

	public void actionPerformed(ActionEvent e)
	{
		processAction(e.getActionCommand());
	}

	private void processAction(String action)
	{
		// List of actions that will get executed outside the event-dispatch
		// thread
		final List<String> asyncCommands = Arrays.asList("play.png",
				"playstep.png", "playcycle.png", "pause.png");

		if (mas != null)
		{
			if ("new.png".equals(action))
				gui.newMas();
			// Commands that execute MAS are dispatched in a separate thread
			else if (asyncCommands.contains(action))
				this.dispatchMASAction(action, forAll, gui.getSelectedModule());
			else if ("recompile.png".equals(action))
				gui.reloadMas();
			else if ("close.png".equals(action))
				gui.closeMas();
			else if ("message.png".equals(action))
				showMessageAgent();
		}
		if ("load.png".equals(action))
			gui.openFile();
		if ("About".equals(action))
			showAboutWindow();
		if ("Settings".equals(action))
			showSettingsWindow();
		if ("Exit".equals(action))
			gui.exit();
	}

	/**
	 * Dispatches command for the multi-agent system. Commands are executed in
	 * an asynchronous fashion, i.e. method does not wait for deliberation step
	 * to finish and returns immediately.
	 * 
	 * @param action action to be performed on the multi-agent system.
	 * @param forAll flag specifying if the action is perfomed with respect to
	 *        the individual module or whole multi-agent system.
	 * @param module module to which is the action related if forAll is set to
	 *        <code>false</code>
	 */

	private void dispatchMASAction(String action, boolean forAll,
			APLModule module)
	{
		if (mas != null)
		{
			if ("new.png".equals(action))
				gui.newMas();
			else if (forAll && "play.png".equals(action))
				mas.start();
			else if (forAll && "playstep.png".equals(action))
				mas.step(1);
			else if (forAll && "playcycle.png".equals(action))
				mas.step(STEPS_PER_CYCLE);
			else if (forAll && "pause.png".equals(action))
				mas.stop();

			else if (!forAll && "play.png".equals(action))
				mas.start(module);
			else if (!forAll && "playstep.png".equals(action))
				mas.step(module, 1);
			else if (!forAll && "playcycle.png".equals(action))
				mas.step(module, STEPS_PER_CYCLE);
			else if (!forAll && "pause.png".equals(action))
				mas.stop(module);
		}
	}

	private void showMessageAgent()
	{
		if (msgr != null)
		{
			if (f == null)
				f = new MessageFrame(msgr);
			f.setVisible(true);
		}
	}

	private void showSettingsWindow()
	{
		new SettingsFrame(gui);
	}

	public void itemStateChanged(ItemEvent e)
	{
		String action = "";
		if (e.getItem() instanceof JToggleButton)
			action = ((JToggleButton) e.getItem()).getActionCommand();
		if (e.getItem() instanceof JCheckBoxMenuItem)
			action = ((JCheckBoxMenuItem) e.getItem()).getActionCommand();

		if ("fgdc.png".equals(action))
			Settings.showFGDC(e.getStateChange() == ItemEvent.SELECTED);
		else if ("tracer.png".equals(action))
			Settings.showTracer(e.getStateChange() == ItemEvent.SELECTED);
		// else if ("sh.png".equals(action)) Settings.useSH(e.getStateChange()
		// == ItemEvent.SELECTED);
		else if ("forall.png".equals(action))
		{
			Boolean selected = e.getStateChange() == ItemEvent.SELECTED;
			forAll = selected.booleanValue();
			if (e.getItem() instanceof JToggleButton)
				((JToggleButton) (e.getItem()))
						.setIcon(selected ? makeIcon("forall.png")
								: makeIcon("forall2.png"));
			if (e.getItem() instanceof JCheckBoxMenuItem)
				((JCheckBoxMenuItem) (e.getItem()))
						.setIcon(selected ? makeIcon("forall.png")
								: makeIcon("forall2.png"));

			updateRunButtonsEnabled();
		} else if ("Auto-update Overview".equals(action)) {
			autoUpdateOverviewEnabled = (e.getStateChange() == ItemEvent.SELECTED);
		} else if ("Allow State Tracer".equals(action)) {
			tracerEnabled = (e.getStateChange() == ItemEvent.SELECTED);
			gui.setTracerEnabled(tracerEnabled);
		} else if ("Allow Log".equals(action)) {
			logEnabled = (e.getStateChange() == ItemEvent.SELECTED);
			gui.setLogEnabled(logEnabled);
		}

		// Synchronization of Check buttons in the Menu and in the Toolbar.
		// Make sure the checks are added in the same order to the menu as to
		// the toolbar.
		int i = 0;
		for (JCheckBoxMenuItem c : menuChecks)
		{
			if (c == e.getItem() && i < toolbarChecks.size())
				toolbarChecks.get(i).setSelected(c.isSelected());
			i++;
		}
		i = 0;
		for (JToggleButton c : toolbarChecks)
		{
			if (c == e.getItem() && i < menuChecks.size())
				menuChecks.get(i).setSelected(c.isSelected());
			i++;
		}
	}

	private ImageIcon makeIcon(String icon)
	{
		// String imagemap = "icons"+File.separator;
		// When we using a jar file, the File.seperator strategy does not
		// work, it returns a \ instead and cannot locate the image anymore.
		String imagemap = "icons/";
		URL loc = GUI.class.getResource(imagemap + icon);
		return new ImageIcon(loc);
	}

	public JToolBar getJToolBar()
	{
		return toolbar;
	}

	public JMenuBar getMenuBar()
	{
		return menubar;
	}

	public void showAboutWindow()
	{
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(4, 1, 0, 0));
		JLabel line1 = new JLabel(
				"This platform facilitates the development of multi-agent systems");
		JLabel line2 = new JLabel(
				"based on 2APL: A Practical Agent Programming Language.");
		JLabel line3 = new JLabel(
				"It is developed by the Intelligent Systems Group of Utrecht University.");
		JLabel line4 = new JLabel(
				"More information about 2APL can be found on http://www.cs.uu.nl/2apl/");
		Font f = new Font(line1.getFont().getName(), Font.PLAIN, line1
				.getFont().getSize());
		line1.setFont(f);
		line2.setFont(f);
		line3.setFont(f);
		line4.setFont(f);
		p.add(line1);
		p.add(line2);
		p.add(line3);
		p.add(line4);
		JOptionPane.showMessageDialog(gui, p, "About 2APL",
				JOptionPane.INFORMATION_MESSAGE, makeIcon("logo.png"));
	}

	public void masLoaded(boolean masLoaded)
	{
		try
		{
			// buttons.get("new.png").setEnabled(!masLoaded);
			buttons.get("load.png").setEnabled(!masLoaded);
			buttons.get("close.png").setEnabled(masLoaded);
			buttons.get("recompile.png").setEnabled(masLoaded);
			buttons.get("play.png").setEnabled(masLoaded);
			buttons.get("forall.png").setEnabled(masLoaded);
			buttons.get("message.png").setEnabled(masLoaded);
			buttons.get("playstep.png").setEnabled(masLoaded);
			buttons.get("playcycle.png").setEnabled(masLoaded);
			buttons.get("pause.png").setEnabled(masLoaded);

			// menuItems.get("new.png").setEnabled(!masLoaded);
			menuItems.get("load.png").setEnabled(!masLoaded);
			menuItems.get("close.png").setEnabled(masLoaded);
			menuItems.get("recompile.png").setEnabled(masLoaded);
			menuItems.get("play.png").setEnabled(masLoaded);
			menuItems.get("forall.png").setEnabled(masLoaded);
			menuItems.get("message.png").setEnabled(masLoaded);
			menuItems.get("playstep.png").setEnabled(masLoaded);
			menuItems.get("playcycle.png").setEnabled(masLoaded);
			menuItems.get("pause.png").setEnabled(masLoaded);
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Updates the enabled/disabled state of the run buttons. The buttons are
	 * enabled only if the current module is active or "run all" option is
	 * selected.
	 */
	public void updateRunButtonsEnabled()
	{
		APLModule module = gui.getSelectedModule();

		boolean runButtonsEnabled = false;
		boolean pauseButtonEnabled = false;

		if (module != null)
		{
			// A particular module is selected
			runButtonsEnabled = (!forAll && module.isActive() && !runningModules
					.contains(module))
					|| (forAll && runningModules.isEmpty());

			pauseButtonEnabled = (!forAll && runningModules.contains(module))
					|| (forAll && !runningModules.isEmpty());
		} else
		{
			// No particular module is selected
			runButtonsEnabled = (forAll && runningModules.isEmpty());

			pauseButtonEnabled = (forAll && !runningModules.isEmpty());
		}

		for (String command : startExecutionCmds)
		{
			buttons.get(command).setEnabled(runButtonsEnabled);
			menuItems.get(command).setEnabled(runButtonsEnabled);
		}

		buttons.get("pause.png").setEnabled(pauseButtonEnabled);
		menuItems.get("pause.png").setEnabled(pauseButtonEnabled);
	}

	/**
	 * Callback handling the GUI updates needed before the MAS starts executing.
	 * 
	 * @param module the module that will start executing
	 */
	public void moduleWillStart(APLModule module)
	{
		runningModules.add(module);
		for (String command : disableDuringExecutionCmds)
		{
			if (buttons.get(command) != null)
				buttons.get(command).setEnabled(false);
			
			if (menuItems.get(command) != null)
				menuItems.get(command).setEnabled(false);
		}
		updateRunButtonsEnabled();
	}

	/**
	 * Callback handling the GUI updates needed after the module finishes its
	 * execution.
	 * 
	 * @param module the module that will start executing
	 */
	public void moduleStopped(APLModule module)
	{
		runningModules.remove(module);
		if (runningModules.isEmpty())
		{
			for (String command : disableDuringExecutionCmds)
			{
				if (buttons.get(command) != null)
					buttons.get(command).setEnabled(true);
				
				if (menuItems.get(command) != null)
					menuItems.get(command).setEnabled(true);
			}
		}
		updateRunButtonsEnabled();
	}
}

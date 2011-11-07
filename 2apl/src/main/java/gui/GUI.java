package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import apapl.APAPLBuilder;
import apapl.APLMAS;
import apapl.APLModule;
import apapl.LoadEnvironmentException;
import apapl.MASChangeListener;
import apapl.MASExecutionListener;
import apapl.ModuleChangeListener;
import apapl.MultiThreadedExecutor;
import apapl.deliberation.DeliberationResult;
import apapl.messaging.Messenger;
import apapl.parser.ParseMASException;
import apapl.parser.ParseModuleException;
import apapl.parser.ParsePrologException;

class MASFilenameFilter implements FilenameFilter {

	public boolean accept(File dir, String name) {

		if( name.endsWith(".mas") )
			return true;
		
		return false;
	}
}

/**
 * The class constructs graphic user interface (GUI) of the 2APL platform. The
 * GUI runs in the event dispatching thread, independently from the 2APL
 * interpreter, which runs in a number of other threads.
 * <p>
 * The communication between GUI and 2APL interpreter is asynchronous. It
 * listens for interpreter callbacks to maintain its own local representation of
 * the current state of the multi-agent system.
 */
public class GUI extends JFrame implements WindowListener,
		ModuleChangeListener, MASChangeListener, MASExecutionListener,
		TreeSelectionListener
{
	private int x = 200;
	private int y = 100;
	private int width = 800;
	private int height = 600;

	private boolean useSH = false;

	private JPanel viewerPanel;
	private HashMap<APLModule, ModuleViewer> viewers = new HashMap<APLModule, ModuleViewer>();

	/**
	 * Tree displaying the hierarchy of modules stored in the {@link treeModel}
	 * data structure.
	 */
	private JTree tree;

	/**
	 * Module tree model mirrors the current hierarchy of modules in the
	 * multi-agent system. It maintains information about the state of their
	 * activity (active or inactive) and about their current execution state
	 * (running, stopped or sleeping).
	 * <p>
	 * The {@code treeModel} data structure is initially populated after the MAS
	 * is loaded and updated after each callback from MASChangeListener or
	 * MASExecutionListener.
	 */
	private ModuleTreeModel treeModel;

	MasTab mastab;

	private APLMAS mas;
	private Toolbar toolbar;
	private File masfile;
	private Messenger msgr;

	public static LookAndFeel laf = UIManager.getLookAndFeel();
	public static Object oldUIClassLoader = UIManager.get("ClassLoader");

	public GUI(Messenger msgr, File masfile)
	{
		// WindowUtilities.setNativeLookAndFeel();
		mastab = null;
		this.msgr = msgr;

		// Setup the main window
		addWindowListener(this);
		setBounds(x, y, width, height);
		setLayout(new BorderLayout());
		setTitle("2APL platform");
		setVisible(true);

		// Add toolbar
		toolbar = new Toolbar(this);
		setJMenuBar(toolbar.getMenuBar());
		add(toolbar.getJToolBar(), BorderLayout.NORTH);

		// No MAS system loaded initially
		treeModel = null;

		// Add tree
		tree = new JTree();
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(this);
		tree.expandRow(tree.getRowCount());
		tree.setModel(new DefaultTreeModel(null));
		// tree.setExpandsSelectedPaths(true);

		// Disable icons
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree
				.getCellRenderer();
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		renderer.setLeafIcon(null);

		JScrollPane treeScrollPane = new JScrollPane(tree);

		// Add viewer panel
		viewerPanel = new JPanel(new BorderLayout());
		viewerPanel.setBackground(Color.GRAY);

		// Add the tree and the viewer to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeScrollPane);
		splitPane.setRightComponent(viewerPanel);

		treeScrollPane.setMinimumSize(new Dimension(100, 400));
		viewerPanel.setMinimumSize(new Dimension(400, 400));
		splitPane.setDividerLocation(200);

		// Disables F6 and F8 keystrokes that are normally handled by the
		// splitPane.
		splitPane.getActionMap().getParent().remove("startResize");
		splitPane.getActionMap().getParent().remove("toggleFocus");

		add(splitPane, BorderLayout.CENTER);

		validateTree();
		
		if (masfile != null) {
		    loadMas(masfile);
		}
	}

	public void loadMas(File masfile)
	{
		// If the previously open MAS has not been closed, close it.
		if (mas != null)
			closeMas();
		
		viewerPanel.removeAll();
		APAPLBuilder builder = new APAPLBuilder();

		// Standard error message (only used in case of error)
		String errormsg = "An error occurred while loading MAS file " + masfile
				+ "\n";

		try
		{
			mas = builder.buildMas(masfile, msgr, new MultiThreadedExecutor());

			// Add multi-agent system root node
			treeModel = new ModuleTreeModel();
			tree.setModel(treeModel);
			tree.expandRow(0);

			// GUI will be informed about the module being
			// created/released/activated/deactivated
			mas.addMASChangeListener(this);
			// GUI will be informed each time a module is started/stopped/put to
			// sleep/waken up
			mas.addMASExecutionListener(this);

			this.masfile = masfile;

			MessageTab msgTable = new MessageTab();
			mastab = new MasTab(msgTable);
			msgr.addMessageListener(msgTable);
			toolbar.setMessenger(msgr);

			// Add edit MAS file to MAS tab
			//File[] files = { masfile };
			//mastab.addTab("File", new FileList(new JextEdit(), files, mastab));

			List<APLModule> modules = mas.getModules();

			for (APLModule a : modules)
			{
				addModule(a);
			}

			toolbar.setMAS(mas);

			// The multi-agent system node is selected initially
			tree.setSelectionRow(0);
		} catch (ParseMASException e)
		{
			showError(errormsg + "Error parsing mas file " + e.getFile()
					+ ":\n\n" + e.getMessage());
		} catch (ParseModuleException e)
		{
			showError(errormsg + "Error parsing module specification file "
					+ e.getFile() + ":\n\n" + e.getMessage());
		} catch (ParsePrologException e)
		{
			showError(errormsg + "Error parsing prolog file " + e.getFile()
					+ ":\n\n" + e.getMessage());
		} catch (LoadEnvironmentException e)
		{
			showError(errormsg + "Error loading environment " + e.getName()
					+ ":\n\n" + e.getMessage());
			e.printStackTrace();
		}
	}

	public void reloadMas()
	{
		// first close the mas
		closeMas();

		// then load it again
		if (masfile != null)
		{
			loadMas(masfile);
		}
	}

	public void closeMas()
	{
		mas.takeDown();
		mas = null;
		toolbar.setMAS(null);

		msgr.restart();

		// then remove module tabs from the UI
		viewers = new HashMap<APLModule, ModuleViewer>();

		// No viewer
		updateViewer(null);

		tree.setModel(new DefaultTreeModel(null));
	}

	/**
	 * Adds node representing the module to the module tree and creates the
	 * module viewer.
	 * 
	 * @param module the module to be added
	 */
	public void addModule(APLModule module)
	{
		ModuleViewer viewer = new ModuleViewer(module, this, toolbar.isTracerEnabled(), toolbar.isLogEnabled());

		// Add File tab to ModuleViewer
		//LinkedList<File> files = mas.getFiles(module);
		//viewer.addTab("Files", new FileList(new JextEdit(), files
		//		.toArray(new File[0]), viewer));

		viewers.put(module, viewer);

		// Add module to the tree view
		treeModel.addNode(module.getParent(), module);
		// Make the newly added node visible
		tree.expandPath(treeModel.getPathTo(treeModel.getNode(module
				.getParent())));

		// Add listeners
		module.addModuleChangeListener(this);

		viewer.update();
	}

	/**
	 * Removes the module's node from the module tree and removes the module
	 * viewer.
	 * 
	 * @param module the module to be removed
	 */
	public void removeModule(APLModule module)
	{
		// Hide the module viewer of the removed module, if currently displayed
		TreePath selectionPath = tree.getSelectionPath();
		if (selectionPath != null)
		{
			// Is the module to-be-removed selected?
			if (selectionPath.getLastPathComponent() == treeModel
					.getNode(module))
			{
				// Set the focus to the parent of the removed module
				tree.setSelectionPath(treeModel.getPathTo(treeModel
						.getNode(module.getParent())));
			}
		}
		// Remove from the Tree view
		treeModel.removeNode(module.getParent(), module);
		// Destroy the viewer
		viewers.remove(module);
	}

	/**
	 * Displays given container in the viewer area on the right side of the
	 * split pane.
	 * 
	 * @param c the container to display
	 */
	private void updateViewer(final Container c)
	{
		// this causes the addTab method to be called from within the swing
		// event dispatch thread.
		Runnable updateViewer = new Runnable()
		{
			public void run()
			{
				viewerPanel.removeAll();
				if (c != null)
					viewerPanel.add(c);
				viewerPanel.revalidate();
				viewerPanel.repaint();
			}
		};

		SwingUtilities.invokeLater(updateViewer);
	}

	/**
	 * Returns the module that is currently selected.
	 * 
	 * @return the selected module, {@code null} if nothing or root node is selected
	 */
	public APLModule getSelectedModule()
	{
		ModuleTreeNode node = (ModuleTreeNode) tree
				.getLastSelectedPathComponent();
		// Nothing is selected.
		if (node == null)
			return null;

		if (node.isModule())
			return node.getModule(); // module viewer is selected
		else
			return null; // MAS is selected
	}

	public void newMas()
	{
		ImageIcon icon = new ImageIcon(gui.GUI.class
				.getResource("icons/new.png"));
		String filename = (String) JOptionPane.showInputDialog(this,
				"Specify name for mas file:", "Create new APLPlatform",
				JOptionPane.PLAIN_MESSAGE, icon, null, "new.mas");

		if ((filename != null) && (filename.length() > 0))
		{
			if (filename.indexOf(".") < 0)
				filename = filename + ".mas";
			if (filename.endsWith(".mas"))
			{
				File file = new File(filename);
				try
				{
					file.createNewFile();
				} catch (IOException e)
				{
				}
				loadMas(file.getAbsoluteFile());
			} else
				JOptionPane.showMessageDialog(this, filename
						+ " is not a valid mas filename.");
		}
	}

	public void useSH(boolean useSH)
	{
		this.useSH = useSH;
		for (ModuleViewer viewer : viewers.values())
			viewer.useSH(useSH);
	}

	public boolean useSH()
	{
		return useSH;
	}

	private void showError(String errorMessage)
	{
		RTFFrame r = new RTFFrame(false);
		r.update(errorMessage);

		updateViewer(r);
	}

	public void openFile()
	{
		FileDialog o = new FileDialog(this, "Open File", FileDialog.LOAD);
		MASFilenameFilter filter = new MASFilenameFilter();
		o.setFilenameFilter(filter);
		o.setVisible(true);
		String filename = o.getFile();
		String path = o.getDirectory();
		if (filename == null)
			return;
		if (filename.endsWith(".mas") || filename.endsWith(".xml"))
		{
			loadMas(new File(path + File.separator + filename));
		}
	}

	public void windowClosing(WindowEvent e)
	{
		System.exit(0);
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}

	/**
	 * Method listening for the node being selected in the tree component.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		// Returns the last path element of the selection.
		ModuleTreeNode node = (ModuleTreeNode) tree
				.getLastSelectedPathComponent();
		// Nothing is selected.
		if (node == null)
			return;

		if (node.isModule())
		{
			APLModule module = node.getModule();
			// Show the module viewer
			ModuleViewer viewer = viewers.get(module);
			updateViewer(viewer);
		} else
		{
			if (mastab != null)
				updateViewer(mastab);
		}

		// Disable run & step actions if the module is not active
		toolbar.updateRunButtonsEnabled();
	}

	/*
	 *
	 * Callbacks from the 2APL interpreter.
	 *  
	 */

	/**
	 * Implements the GUI feedback after module performs a deliberation step.
	 */
	public void moduleChanged(final APLModule module,
			final DeliberationResult result)
	{

		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				ModuleViewer viewer = viewers.get(module);
				if (viewer != null)
				{
					viewer.logState(result);
					
					if (toolbar.isAutoUpdateOverviewEnabled())
						viewer.update();
				}
			}
		}, true);

	}

	/**
	 * Implements the GUI feedback after module has been activated.
	 */
	public void moduleActivated(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				treeModel.activateNode(module);
				toolbar.updateRunButtonsEnabled();
			}
		}, false);
	}

	/**
	 * Implements the GUI feedback after a new module has been created.
	 */
	public void moduleCreated(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				addModule(module);
			}
		}, false);
	}

	/**
	 * Implements the GUI feedback after a module has been deactivated.
	 */
	public void moduleDeactivated(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				treeModel.deactivateNode(module);
				toolbar.updateRunButtonsEnabled();
			}
		}, false);
	}

	/**
	 * Implements the GUI feedback after a module has been released.
	 */
	public void moduleReleased(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				removeModule(module);
			}
		}, false);
	}

	/**
	 * Callback handling the GUI updates needed before the MAS starts executing
	 * one of its modules.
	 */
	public void moduleWillStart(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				toolbar.moduleWillStart(module);
				treeModel.setStartedNode(module);
				if (!toolbar.isAutoUpdateOverviewEnabled()) {
					ModuleViewer viewer = viewers.get(module);
					if (viewer != null) {
						viewer.setNotUpToDate();
					}
				}
			}
		}, true);

	}

	/**
	 * Callback handling the GUI updates needed after the MAS finishes execution
	 * of one of the modules.
	 */
	public void moduleStopped(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				toolbar.moduleStopped(module);
				treeModel.setStoppedNode(module);
				
				if (!toolbar.isAutoUpdateOverviewEnabled()) {
					ModuleViewer viewer = viewers.get(module);
					if (viewer != null) {
						viewer.update();
						viewer.setUpToDate();
					}
				}
			}
		}, true);
	}

	/**
	 * Callback handling the GUI updates needed after one of the module is put
	 * to sleep.
	 */
	public void modulePutToSleep(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				treeModel.setNodePutToSleep(module);
			}
		}, true);
	}

	/**
	 * Callback handling the GUI updates needed before one of the module will
	 * wake up from the sleep mode.
	 */
	public void moduleWillWakeUp(final APLModule module)
	{
		dispatchSwingUpdate(new Runnable()
		{
			public void run()
			{
				treeModel.setNodeWakenUp(module);
			}
		}, true);
	}

	/**
	 * Performs given code in the Swing event dispatching thread.
	 * 
	 * @param update the code to execute
	 * @param wait if set to {@code true}, the thread will wait until the event is
	 *        processed
	 */
	private void dispatchSwingUpdate(final Runnable update, boolean wait)
	{
		if (SwingUtilities.isEventDispatchThread())
		{
			update.run();
		} else
		{
			if (!wait)
			{
				SwingUtilities.invokeLater(update);
			} else
			{
				try
				{
					SwingUtilities.invokeAndWait(update);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				} catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Informs module viewers that user has enabled/disabled the state tracer.
	 * 
	 * @param state true if state tracer is now enabled, false if it is now disabled.
	 */
	public void setTracerEnabled(boolean state) {
		for (ModuleViewer viewer : viewers.values()) {
			viewer.setTracerEnabled(state);
		}
	}
	
	/**
	 * Informs module viewers that user has enabled/disabled the log.
	 * 
	 * @param state true if the log is now enabled, false if it is now disabled.
	 */
	public void setLogEnabled(boolean state) {
		for (ModuleViewer viewer : viewers.values()) {
			viewer.setLogEnabled(state);
		}
	}
	
	/**
	 * Exit the 2APL Platform: Takes down the MAS and closes the GUI window.
	 */
	public void exit() {
		if (mas != null)
			closeMas();
		setVisible(false);
		dispose();
		System.exit(0);
	}

}

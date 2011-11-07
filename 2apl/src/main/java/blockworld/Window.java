package blockworld;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.util.ArrayList;

import blockworld.lib.Signal;

/// statistics view
class Statistics extends AbstractTableModel implements Observer {

	private static final long serialVersionUID = 5372407086612717903L;

	protected EnvView _view = null;

	protected Vector _signals = new Vector();
	

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return _signals.size();
	}

	public Object getValueAt( int row, int col ) {
		// get requested row
		final Signal signal = (Signal) _signals.get( row );

		// name column requested
		if( col == 0 )
			return signal.getName();

		// count column requested
		return new Integer( (int) signal.getEmitCount() );
	}

	public String getColumnName( int i ) {
		if( i == 0 )
			return "Signal Description";
		return "Total Emitted Events";
	}

	public Statistics( EnvView view ) {
		setEnvView( view );
	}

	// \todo throw illegal argument exception is view == null
	public void setEnvView( EnvView view ) {
		// remove old listener
		if( _view != null )
			_view.signalSelectionChanged.deleteObserver( this );

		_view = view;
		_view.signalSelectionChanged.addObserver( this );
		
		
		// Changed SA: On a screen refresh of the environment update yourself (call the update( Observable o, Object arg ))
	//	_view.signalRefresh.addObserver(this);
		// agent/env changed
		update();
	}

	public void update( Observable o, Object arg ) {
		// agent/env changed
		update();
	}

	// \todo het is netter als ipv tableStructureChanged, rowsInserted
	// en rowsDeleted wordt gesignaled
	public void update() {
		// empty list
		_signals.clear();

		final Env env = _view.getEnv();
		final Agent agent = _view.getSelectedAgent();

		// add events
		_signals.add( env.signalBombTrapped );
		if( agent != null ) {
			_signals.add( agent.signalDropBomb );
			_signals.add( agent.signalDropBombSucces );
			_signals.add( agent.signalMove );
			_signals.add( agent.signalMoveSucces );
			_signals.add( agent.signalPickupBomb );
			_signals.add( agent.signalPickupBombSucces );
		}

		// notify observers
		//	fireTableRowsUpdated(0, _signals.size() - 1);
	
		// Changed SA: This is better, this will not change the layout of the complete table!
		fireTableDataChanged();
	}

}

// / Window with EnvView and buttons for loading/saving
// / \todo implement step blocking function and gray done button
// / when step is not possible
public class Window extends JFrame{

	private static final long serialVersionUID = -462965463955994316L;

	protected boolean _done = false;

	protected boolean _init = false;

	protected File _lastFile = null;

	private JToolBar					m_tbToolbar		= null;
	private ArrayList<JToggleButton>	m_aEditOptions	= new ArrayList<JToggleButton>();


	public Window( final Env env ){
		super( "Blockworld" );

		// create clear menuitem + action
		JMenuItem clear = new JMenuItem( "Clear environment" );
		clear.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				// popup confirmation dialog
				final int rv = JOptionPane.showConfirmDialog( Window.this,
						"Are you sure you want to clear the environment",
						"Confirm clear", JOptionPane.YES_NO_OPTION );

				// abort unless YES is pressed
				if( rv != JOptionPane.YES_OPTION )
					return;

				env.clear();
			}
		} );

		// create revert menuitem + action
		JMenuItem revert = new JMenuItem( "Revert to saved" );
		revert.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				// see if we can revert at all
				if( _lastFile == null ) {
					JOptionPane
							.showMessageDialog(
									Window.this,
									"You did not load or save this environment yet.\nPlease load or save first",
									"Nothing to revert to",
									JOptionPane.ERROR_MESSAGE );
					return;
				}

				// popup confirmation dialog
				final int rv = JOptionPane.showConfirmDialog( Window.this,
						"Are you sure you want revert to "
								+ _lastFile.getPath(),
						"Confirm revert to saved", JOptionPane.YES_NO_OPTION );

				// abort unless YES is pressed
				if( rv != JOptionPane.YES_OPTION )
					return;

				try {
					env.clear();
					final FileInputStream stream = new FileInputStream(
							_lastFile );
					env.load( stream );
				}
				catch( Exception ex ) {
					System.out.println( "Loading failed! " + ex );
				}
			}
		} );

		// create 'load' button + action
		JMenuItem load = new JMenuItem( "Load from File" );
		load.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				try {
					final File cwd = new File( "." );
					final JFileChooser fc = new JFileChooser( cwd );
					final int rv = fc.showOpenDialog( Window.this );

					// cancel pressed
					if( rv != JFileChooser.APPROVE_OPTION )
						return;

					final File file = fc.getSelectedFile();
					final FileInputStream stream = new FileInputStream( file );
					env.load( stream );
					_lastFile = file;
				}
				catch( Exception ex ) {
					System.out.println( "Loading failed! " + ex );
				}
			}
		} );

		JMenuItem save = new JMenuItem( "Save to File" );
		save.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				try {
					final File cwd = new File( "." );
					final JFileChooser fc = new JFileChooser( cwd );
					final int rv = fc.showSaveDialog( Window.this );

					// cancel pressed
					if( rv != JFileChooser.APPROVE_OPTION )
						return;

					final File file = fc.getSelectedFile();
					final FileOutputStream stream = new FileOutputStream( file );
					env.save( stream );
					_lastFile = file;
				}
				catch( Exception ex ) {
					System.out.println( "Saving failed! " + ex );
				}
			}
		} );

		JMenuItem resize = new JMenuItem( "Environment Size" );
		resize.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				// show dialog
				String size = (String) JOptionPane.showInputDialog(
						Window.this,
						"Resize environment (X * Y) (X, Y) (X x Y) (X Y)",
						"Resize environment", JOptionPane.PLAIN_MESSAGE, null, null, env
								.getWidth()
								+ " * " + env.getHeight() );

				// if a string was returned, parse and set size
				if( (size != null) && (size.length() > 0) ) {
					StringTokenizer st = new StringTokenizer( size, "*x, " );

					if( !st.hasMoreTokens() )
						return;
					String x = st.nextToken();

					if( !st.hasMoreTokens() )
						return;
					String y = st.nextToken();

					try {
						Dimension d = new Dimension( Integer.parseInt( x ),
								Integer.parseInt( y ) );

						env.setSize( d );
					}
					catch( NumberFormatException e1 ) {
						JOptionPane
								.showMessageDialog(
										Window.this,
										"Invalid number format, it should be one of (X * Y) (X, Y) (X x Y) (X Y)",
										"Error parsing size",
										JOptionPane.ERROR_MESSAGE );
					}
				}
			}
		} );

		JMenuItem setid = new JMenuItem( "Default APLIdentifier" );
		setid.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				// show dialog
				String objType = ((String) JOptionPane.showInputDialog(
						Window.this,
						"Enter default identifier for new bombs/traps (all char, first char in lowercase)",
						"Set Default Object APLIdentifier", JOptionPane.PLAIN_MESSAGE, null, null, env.getObjType() ));
				// -- validate objType --
				if(objType == null) return;
				boolean invalid = false;
				for(int i = 0; i < objType.length(); i++) {
					char ichar = objType.charAt(i);
					if(!Character.isLetter(ichar) || (i ==0 && !Character.isLowerCase(ichar))) {
						invalid = true;
						break;
					}
				}
				
				if(objType.length() == 0 || invalid) {
					JOptionPane.showMessageDialog(Window.this,
								      "The object type identifier must be an all-character string with the first character in lowercase",
								      "Invalid object type",
								      JOptionPane.ERROR_MESSAGE );
					return;
				}
				
				env.setObjType(objType);
			}
		} );

		JMenuItem senserange = new JMenuItem( "Sensor Range" );
		senserange.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				String range = (String) JOptionPane.showInputDialog(
						Window.this, "Set agent sensor range in cells",
						"Set Sensor Range in Cells", JOptionPane.PLAIN_MESSAGE, null, null,
						Integer.toString( env.getSenseRange() ) );

				// If a string was returned, parse and set range
				if( (range != null) && (range.length() > 0) )
					env.setSenseRange( Integer.parseInt( range ) );
			}
		} );

		JMenuItem about = new JMenuItem( "About BlockWorld" );
		about.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				// show dialog
				JOptionPane.showMessageDialog(Window.this,
					"BlockWorld for 2APL\n\n"+
					"http://www.cs.uu.nl/2apl/\n\n"+
					"Developed by\n"+
				    "  The 2APL development group\n"+
					"  Utrecht University, the Netherlands",
					"About BlockWorld",
					JOptionPane.INFORMATION_MESSAGE );
			}
		} );

		JMenu world = new JMenu( "World" );
		world.add( load );
		world.add( save );
		world.add( revert );
		world.add( clear );

		JMenu properties = new JMenu( "Properties" );
		properties.add( resize );
		properties.add( senserange );
		properties.add( setid );

		JMenu help = new JMenu( "Help" );
		help.add( about );

		JMenuBar menubar = new JMenuBar();
		menubar.add( world );
		menubar.add( properties );
		menubar.add( help );

		// create window
		getContentPane().setLayout( new BorderLayout() );

		final EnvView envView = new EnvView( env );
		final Statistics stats = new Statistics( envView );
		final JComponent statsView = new JScrollPane( new JTable( stats ) );

		final JSplitPane sp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
				envView, statsView );
		sp.setOneTouchExpandable( true );
		sp.setDividerLocation( 230 );
		sp.setResizeWeight(1);
		getContentPane().add( sp, BorderLayout.CENTER );
		setJMenuBar( menubar );

		// The toolbar
		m_tbToolbar	 = new JToolBar();
		m_tbToolbar.setFloatable(false);
		addButton("info.gif", "Select agent", envView.tool.STATE_SELECT, envView.tool).setSelected(true);
		addButton("bomb.gif", "Place bombs", envView.tool.STATE_ADDBOMB, envView.tool);
		addButton("stone.gif", "Place walls", envView.tool.STATE_ADDWALL, envView.tool);
		addButton("trap.gif", "Place traps", envView.tool.STATE_ADDTRAP, envView.tool);
		addButton("eraser.gif", "Erase objects", envView.tool.STATE_REMOVE, envView.tool);
		getContentPane().add(m_tbToolbar, BorderLayout.NORTH);


		// pack();
		setSize( 400, 250 );
		setVisible( true );
	}

	// / the first call to this method will display the
	// / edit window and block until user has finished editing
	public synchronized void init() {
		if( _init )
			return;

		while( !_done ) {
			try {
				wait();
			} catch( InterruptedException e ) {
			}
		}

		_init = true;
	}

	// / call this method to end the editing session. This function
	// / is called from a JButton.
	protected synchronized void done() {
		_done = true;
		notifyAll();
	}
	
	
	public JToggleButton addButton(String sImage, String tooltip, final int nState, final EnvView.MouseTool tool)
	{
		final JToggleButton button = new JToggleButton(makeIcon(sImage));
		button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tool._state	= nState;
				
				for (JToggleButton cmdButton : m_aEditOptions)
				{
					if (button != cmdButton)
					{
						cmdButton.setSelected(false);
					}
				}
			}
		});
		button.setToolTipText(tooltip);
		m_tbToolbar.add(button);
		m_aEditOptions.add(button);
		
		return button;
	}

	private ImageIcon makeIcon(String sImage)
	{
		sImage	= "images/toolbar/"+sImage;
		return new ImageIcon(this.getClass().getResource(sImage));
	}
}

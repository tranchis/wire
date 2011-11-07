package blockworld;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;


import javax.swing.JPanel;

import blockworld.lib.ObsVectListener;
import blockworld.lib.Signal;

/// Grid view of agent enviroment ('world')
class EnvView extends JPanel implements ObsVectListener, Observer {

	Map imgMap = new HashMap();
	
	// Changed SA:
//	String urlPath = "file:/";
	
	Image imgStone = null;
//	Image imgAgent = null;
//	Image imgHolding = null;
	Image imgBomb = null;
	Image imgTrap = null;
	
	// Changed SA:
	Image[] imgAgents = new Image[10];	
	Image[] imgAgentsHolding = new Image[10];	
	
	private static final long serialVersionUID = -6981513623481400558L;

	class MouseTool implements MouseListener, MouseMotionListener {
		Env _env;

		final int STATE_SELECT = 0;

		final int STATE_REMOVE = 1;

		final int STATE_ADDBOMB = 2;

		final int STATE_ADDWALL = 3;

		final int STATE_ADDTRAP = 4;

		int _state = STATE_SELECT;

		public MouseTool( Env env ) {
			_env = env;
		}

		protected Point toEnv( MouseEvent e ) {
			final double cw = getWidth() / _env.getWidth();
			final double ch = getHeight() / _env.getHeight();
			final int cx = (int) ((double) e.getX() / cw);
			final int cy = (int) ((double) e.getY() / ch);
			return new Point( cx, cy );
		}

		public void drag( MouseEvent e )
		{
			final Point p = toEnv( e );

			// Changed SA: Java is the best option to keep things slow, 
			// to slow things down a bit more we accessively use exceptions.
			// But we don't want to see them all the time!
			try
			{
				// don't draw on agents
				if( (_env.isAgent( p ) != null))
					return;
	
				switch( _state ) {
				// add bombs, removing if needed
				case STATE_ADDBOMB:
					// Changed SA:
					if( _env.isStone( p ) != null )
						_env.removeStone( p );
	
					if( _env.isTrap( p ) != null )
						_env.removeTrap( p );
	
					_env.addBomb( p );
					break;
	
				// add walls, removing if needed
				case STATE_ADDWALL:
					if( _env.isBomb( p ) != null )
						_env.removeBomb( p );
	
					if( _env.isTrap( p ) != null )
						_env.removeTrap( p );
	
					_env.addStone( p );
					break;
	
				// add traps, removing if needed
				case STATE_ADDTRAP:
					if( _env.isBomb( p ) != null )
						_env.removeBomb( p );
	
					// Changed SA:
					if( _env.isStone( p ) != null )
						_env.removeStone( p );
	
					_env.addTrap( p );
					break;
	
				// remove all
				case STATE_REMOVE:
					// Changed SA:
					if( _env.isStone( p ) != null)
						_env.removeStone( p );
	
					if( _env.isBomb( p ) != null )
						_env.removeBomb( p );
	
					if( _env.isTrap( p ) != null )
						_env.removeTrap( p );
					break;
				}
			}
			catch (Exception error){ /* Ignore this*/}
		}

		// Changed SA:
		public void start( MouseEvent e )
		{
			final Point p = toEnv( e );

			try
			{
				if (_state == STATE_SELECT)
				{	// Try to select an agent
					// if it's an agent, change selected agent
					final Agent	a	= _env.isAgent(p);
					if (a != null)
					{
						_selectedAgent = (a != _selectedAgent) ? a : null;
						signalSelectionChanged.emit();
						repaint();
//						_state = STATE_SELECT;
					}
				}
				else
				{	// Place the selected object (or erase when chosen)
					drag(e);
				}


/*
				// if it's a stone, make it a bomb
				if (_env.senseStone( p ) != null)
				{
					_env.removeStone( p );
					_env.addBomb( p );
					_state = STATE_ADDBOMB;
					return;
				}

				// if it's a bomb, make it a trap
				if( _env.senseBomb( p ) != null )
				{
					_env.removeBomb( p );
					_env.addTrap( p );
					_state = STATE_ADDTRAP;
					return;
				}

				// if it's a trap, remove it
				if( _env.senseTrap( p ) != null )
				{
					_env.removeTrap( p );
					_state = STATE_REMOVE;
					return;
				}

				// it's a empty spot, so add a stone
				_env.addStone( p );
				_state = STATE_ADDWALL;
*/
			}
			catch( Exception error )
			{
				// ignore error
			}
		}

		public void mouseClicked( MouseEvent arg0 ) {
		}

		public void mouseEntered( MouseEvent arg0 ) {
		}

		public void mouseExited( MouseEvent arg0 ) {
		}

		public void mousePressed( MouseEvent arg0 ) {
			start( arg0 );
		}

		public void mouseReleased( MouseEvent arg0 ) {
		}

		public void mouseDragged( MouseEvent arg0 ) {
			drag( arg0 );
		}

		public void mouseMoved( MouseEvent arg0 ) {
		}
	};

	protected Env _env;

	double _cw = 10;

	double _ch = 10;

	public Signal signalSelectionChanged = new Signal( "Selected agent changed" );
	
	// Changed SA: We need something to indicate the window (static and env) that the agent has moved.
	// We cannot use an agent for this because they are not created in the beginnning
//	public Signal signalRefresh = new Signal( "Refresh of screen" );

	protected Agent _selectedAgent = null;
	
	public MouseTool tool;

	EnvView( Env env ) {
		_env = env;
		
		// Changed SA:
		/*
		try {
			String rawUserDir = System.getProperty("user.dir");
			String procUserDir = rawUserDir.replaceAll("\\\\","/");
			int pathLen = procUserDir.length();
			urlPath = urlPath + procUserDir + "/plugins/BlockWorld/";
			System.out.println("Pictogramomslag: "+urlPath);
		} catch(Exception xEx) {
			xEx.printStackTrace();
		}
		*/



		try
		{
			// Changed SA:
			imgStone = createImage((ImageProducer)(this.getClass().getResource("images/stone.gif")).getContent());
		} catch(Exception xEx) {
			xEx.printStackTrace();
		}

		
		
		try 
		{
			// Changed SA:
			imgAgents[0] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_army.gif")).getContent());
			imgAgents[1] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_blue.gif")).getContent());
			imgAgents[2] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_gray.gif")).getContent());
			imgAgents[3] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_green.gif")).getContent());
			imgAgents[4] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_orange.gif")).getContent());
			imgAgents[5] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_pink.gif")).getContent());
			imgAgents[6] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_purple.gif")).getContent());
			imgAgents[7] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_red.gif")).getContent());
			imgAgents[8] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_teal.gif")).getContent());
			imgAgents[9] = createImage((ImageProducer)(this.getClass().getResource("images/agents/agent_yellow.gif")).getContent());
		} 
		catch(Exception xEx) 
		{
			xEx.printStackTrace();
		}
		
		
		try
		{
			// Changed SA:
			imgAgentsHolding[0] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_army.gif")).getContent());
			imgAgentsHolding[1] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_blue.gif")).getContent());
			imgAgentsHolding[2] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_gray.gif")).getContent());
			imgAgentsHolding[3] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_green.gif")).getContent());
			imgAgentsHolding[4] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_orange.gif")).getContent());
			imgAgentsHolding[5] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_pink.gif")).getContent());
			imgAgentsHolding[6] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_purple.gif")).getContent());
			imgAgentsHolding[7] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_red.gif")).getContent());
			imgAgentsHolding[8] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_teal.gif")).getContent());
			imgAgentsHolding[9] = createImage((ImageProducer)(this.getClass().getResource("images/agents/holding_yellow.gif")).getContent());
		} catch(Exception xEx) {
			xEx.printStackTrace();
		}
				
		try
		{
			// Changed SA:
			imgBomb = createImage((ImageProducer)(this.getClass().getResource("images/bomb.gif")).getContent());
		} catch(Exception xEx) {
			xEx.printStackTrace();
		}

		try
		{
			// Changed SA:
			imgTrap = createImage((ImageProducer)(this.getClass().getResource("images/trap.gif")).getContent());
		} catch(Exception xEx) {
			xEx.printStackTrace();
		}

		_env.addAgentListener( new ObsVectListener() 
		{
			public void onAdd( int i, Object o ) 
			{
				final Agent a = (Agent) o;
				a.signalDropBomb.addObserver( EnvView.this );
				a.signalPickupBomb.addObserver( EnvView.this );
				a.signalMove.addObserver( EnvView.this );
				repaint();
			}

			public void onRemove( int i, Object o ) {
				final Agent a = (Agent) o;
				a.signalDropBomb.deleteObserver( EnvView.this );
				a.signalPickupBomb.deleteObserver( EnvView.this );
				a.signalMove.deleteObserver( EnvView.this );
				repaint();
			}
		} );
		
		// repaint on changes in stones, size,
		_env.addStonesListener( this );
		_env.addBombsListener( this );
		_env.addTrapsListener( this );
		_env.signalSenseRangeChanged.addObserver( this );
		_env.signalSizeChanged.addObserver( this );
		_env.signalTrapChanged.addObserver( this );

		// track mouse events
		tool = new MouseTool( _env );
		addMouseListener( tool );
		addMouseMotionListener( tool );

	}
	

	public Dimension getPreferredSize() {
		return new Dimension( _env.getWidth() * (int) _cw, _env.getHeight()
				* (int) _ch );
	}

	// / \todo fix size stuff, it's now done incorrectly (use 100 * 100 grid to
	// / see result). this is because the way casting is done (doubles converted
	// / to int before use, instead of afterwards)
	protected void paintComponent( Graphics g ) {
		super.paintComponent( g );

		final double cw = getWidth() / _env.getWidth();
		final double ch = getHeight() / _env.getHeight();

		// clear
		g.setColor( Color.white );
		g.fillRect( 0, 0, getWidth(), getHeight() );
				
		// draw grid contents + lines
		for( double x = 0; x < _env.getWidth(); x++ )
			for( double y = 0; y < _env.getHeight(); y++ ) {
				final Point p = new Point( (int) x, (int) y );
				
				// draw stone (blue square)
				// Changed SA
				if( _env.isStone( p ) != null ) {
					if(imgStone == null) {
						g.setColor( Color.blue );
						g.fillRect( (int) (x * cw), (int) (y * ch), (int) cw,
							(int) ch );
					} else {
						g.drawImage(imgStone, (int) (x * cw + 1), (int) (y * ch + 1), (int) cw - 1,
							(int) ch - 1, this);
					}
				}

				// draw bomb (red oval)
				TypeObject bomb = null;
				if( (bomb = _env.isBomb( p )) != null )
				{
					// Changed SA: there's only one type of bomb!
					/*
					if(!imgMap.containsKey("bomb_"+bomb.getType())) {
						try {
							imgMap.put(("bomb_"+bomb.getType()),createImage((ImageProducer)(
								new URL(urlPath+"bomb_"+bomb.getType()+".gif")).getContent()));
							System.out.println("Type ["+bomb.getType()+"] bomb icon found");
						} catch(Exception xEx) {
							xEx.printStackTrace();
							imgMap.put(("bomb_"+bomb.getType()),null);
						}
					}
					if(imgMap.get("bomb_"+bomb.getType()) != null) {
						g.drawImage((Image) imgMap.get("bomb_"+bomb.getType()),
							(int) (x * cw + 1), (int) (y * ch + 1), (int) cw - 1,
							(int) ch - 1, this);
					} else*/
					if(imgBomb != null)
					{
						g.drawImage(imgBomb, (int)(x*cw + 1), (int)(y*ch + 1), (int)(cw-1), (int)(ch-1), this);
					}
					else
					{
						g.setColor(Color.red);
						g.fillOval((int)(x*cw), (int)(y*ch), (int)cw, (int)ch);
					}
				}

				// draw trap (orange square)
				TypeObject trap = null;
				if( (trap = _env.isTrap( p )) != null )
				{
					// Changed SA: there's only one type of trap!
					/*
					if(!imgMap.containsKey("trap_"+trap.getType())) {
						try {
							imgMap.put(("trap_"+trap.getType()),createImage((ImageProducer)(
								new URL(urlPath+"trap_"+trap.getType()+".gif")).getContent()));
							System.out.println("Type ["+trap.getType()+"] trap icon found");
						} catch(Exception xEx) {
							xEx.printStackTrace();
							imgMap.put(("trap_"+trap.getType()),null);
						}
					}
					if(imgMap.get("trap_"+trap.getType()) != null) {
						g.drawImage((Image) imgMap.get("trap_"+trap.getType()),
							(int) (x * cw + 1), (int) (y * ch + 1), (int) cw - 1,
							(int) ch - 1, this);
					} else*/
					if(imgTrap != null)
					{
						g.drawImage(imgTrap, (int)(x*cw + 1), (int)(y*ch + 1), (int)(cw-1),(int)(ch-1), this);
					}
					else
					{
						g.setColor(Color.orange);
						g.fillRect((int)(x*cw), (int)(y*ch), (int)cw, (int)ch);
					}
				}
				// draw gridline
				g.setColor( Color.gray );
				g.drawRect( (int) (x * cw), (int) (y * ch), (int) cw, (int) ch );
			}

		Iterator a = _env.getBlockWorldAgents().iterator();
		while( a.hasNext() ) {
			final Agent agent = (Agent) a.next();

			// skip agents not entered in the world
			if( !agent.isEntered() )
				continue;

			// Changed SA: don't know what this is for, do know it's giving a lot of exceptions
			// Seems like you can use a custom picture
			/*if(!imgMap.containsKey("agent_"+agent.getName())) {
			try {
				imgMap.put(("agent_"+agent.getName()),createImage((ImageProducer)(
					new URL(urlPath+"agent_"+agent.getName()+".gif")).getContent()));
					System.out.println("Unloaded agent ["+agent.getName()+"] icon found");
				} catch(Exception xEx) {
					xEx.printStackTrace();
					imgMap.put(("agent_"+agent.getName()),null);
				}
			}

			if(!imgMap.containsKey("holding_"+agent.getName())) {
			try {
				imgMap.put(("holding_"+agent.getName()),createImage((ImageProducer)(
					new URL(urlPath+"holding_"+agent.getName()+".gif")).getContent()));
					System.out.println("Loaded agent ["+agent.getName()+"] icon found");
				} catch(Exception xEx) {
					xEx.printStackTrace();
					imgMap.put(("holding_"+agent.getName()),null);
				}
			}*/

			// draw agent position
			final Point p = agent.getPosition();
			final int x = (int) ((double) p.x * cw);
			final int y = (int) ((double) p.y * ch);
			if (agent.senseBomb() != null)
			{
				// Changed SA: there's only one type of agent holding a bomb!
				/*
				if(imgMap.get("holding_"+agent.getName()) != null) {
					g.drawImage((Image) imgMap.get("holding_"+agent.getName()),
						x + 1, y + 1, (int) cw - 1,(int) ch - 1, this);
				} else*/
				if(imgAgentsHolding[agent._colorID] != null)
				{
					g.drawImage(imgAgentsHolding[agent._colorID], x+1, y+1, (int)(cw-1), (int)(ch-1), this);
				}
				else
				{
					g.setColor((agent != _selectedAgent) ? Color.green : Color.green.darker());
					g.fillRect(x+1, y+1, (int)(cw-1), (int)(ch-1));
					g.setColor(Color.black );
					g.drawRect(x+2, y+2, 3, 3);
				}
			}
			else
			{
				// Changed SA: there are 10 agents to choose from!
				/*
				if(imgMap.get("agent_"+agent.getName()) != null) {
					g.drawImage((Image) imgMap.get("agent_"+agent.getName()),
						x + 1, y + 1, (int) cw - 1,(int) ch - 1, this);
				} 
				else*/
				if(imgAgents[agent._colorID] != null)
				{
					g.drawImage(imgAgents[agent._colorID], x+1, y+1, (int)(cw-1), (int)(ch-1), this);
				}
				else
				{
					g.setColor((agent != _selectedAgent) ? Color.green : Color.green.darker());
					g.fillRect(x+1, y+1, (int)(cw-1), (int)(ch-1));
				}
			}

			// draw sensing range
			final int rw = (int) ((double) _env.getSenseRange() * cw);
			final int rh = (int) ((double) _env.getSenseRange() * ch);
			g.setColor( Color.blue );
			g.drawOval( x - (rw), y - (rh), rw * 2, rh * 2 );
		}
	}

	public void onRemove( int i, Object o ) {
		repaint();
	}

	public void onAdd( int i, Object o ) {
		repaint();
	}
	
	public void update( Observable o, Object arg ) {
		repaint();
		// Changed SA: we need some sort of event to indicate to our window we want
		// it to update its screen
	//	signalRefresh.emit();
	}

	public Agent getSelectedAgent() {
		return _selectedAgent;
	}

	public Env getEnv() {
		return _env;
	}

}

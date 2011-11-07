package blockworld;

/*
 3APL Blockworld program by Jelle Herold, copyright 2003.
 Written for the Intelligent Systems group at Utrecht University.
 This is LGPL software.

 $Id: Agent.java,v 1.6 2004/09/14 10:58:06 cvs-3apl Exp $
 */

import java.awt.Point;

import blockworld.lib.Signal;

// / Agent representation in Env. This doubles as a plugin instance.
public class Agent 
{
	protected String _name;

	/**
	 * _position is null means agent did not enter the world. This method is
	 * only for the blockworld package to prevent setting the position by other
	 * means that through the north/south/west/east methods of the environment.
	 * \todo is protection "package" correct?
	 */
	protected Point _position = null;

	protected TypeObject _bomb = null;
	
	int _colorID = 0;

	public Agent( String name ) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	/**
	 * Get current agent position. \returns This will return null if the agent
	 * is not entered in the world.
	 */
	public Point getPosition() {
		return _position;
	}

	// / Sense if agent is carrying a bomb.
	// / \return True if agent is carrying a bomb, false otherwise.
	public TypeObject senseBomb() {
		return _bomb;
	}
	public TypeObject senseBomb(String type) {
		return (_bomb != null && _bomb.getType().equals(type))?_bomb:null;
	}

	public boolean atCapacity() {
		return _bomb != null;
	}

	public void pickBomb(TypeObject bomb) {
		_bomb = bomb;
	}

	public void dropBomb() {
		_bomb = null;
	}

	/**
	 * Check if agent is "entered" in the environment. That is, it has a
	 * position in the world. \returns true if agent is entered in the
	 * environment
	 */
	public boolean isEntered() {
		return (_position != null);
	}

	/**
	 * Called by the interpreter when the agent this instance refers to is
	 * reset. \todo signalMove show become special signal enter/exit
	 */
	public void reset() {
		_position = null;
		_bomb = null; // Fixed bug pointed out by Bas of UU - Sohan
		signalMove.emit();
	}

	/**
	 * returns the unique name of the agent this instance refers to.
	 */
	public String toString() {
		return getName();
	}

	// / removes all listeners
	public void deleteObservers() {
		signalMove.deleteObservers();
		signalPickupBomb.deleteObservers();
		signalDropBomb.deleteObservers();

		signalMoveSucces.deleteObservers();
		signalPickupBombSucces.deleteObservers();
		signalDropBombSucces.deleteObservers();
	}

	// / emitted if agent attemps movement (succesful or not)
	public transient Signal signalMove = new Signal( "agent attempts move" );

	// / emitted if agent attemps to pickup a bomb (succesful or not)
	public transient Signal signalPickupBomb = new Signal(
			"agent attempts pickup" );

	// / emitted if agent attemps to drop a bomb (succesful or not)
	public transient Signal signalDropBomb = new Signal( "agent attempts drop" );

	// / emitted if agent succesfully moves
	public transient Signal signalMoveSucces = new Signal(
			"agent succesful move" );

	// / emitted if agent (succesfully) picks up a bomb
	public transient Signal signalPickupBombSucces = new Signal(
			"agent succesful pickup" );

	// / emitted if agent (succesfully) drops a bomb
	public transient Signal signalDropBombSucces = new Signal(
			"agent sucessful drop" );
}

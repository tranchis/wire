package blockworld.lib;

/*
 This file is part of the Bushfire project.

 Copyright 2003, M. Albers, H Boros, R Burema, N. Goh, J. Herold, B.
 Maassen, R. Peek, J. Priem, Bas Steunebrink, P. van de Werken.

 The Bushfire project is free software; you can redistribute it
 and/or modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2 of
 the License, or (at your option) any later version.

 The Bushfire project is distributed in the hope that it will be
 useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the Bushfire project; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA

 $Id: ObsVect.java,v 1.4 2004/09/14 10:58:06 cvs-3apl Exp $
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Any methods that have not been overwritten do not need to be, because those
 * methods only call overwritten methods anyway.
 */
public class ObsVect extends Vector {

	private static final long serialVersionUID = 1140345853942753525L;

	protected static final int ADD = 0, REMOVE = 1;

	transient protected MyObservable _observable = new MyObservable();

	public ObsVect() {
		super();
	}

	public ObsVect( Collection c ) {
		super( c );
	}

	public ObsVect( int initialCapacity ) {
		super( initialCapacity );
	}

	public ObsVect( int initialCapacity, int capacityIncrement ) {
		super( initialCapacity, capacityIncrement );
	}

	public ObsVect( ObsVectListener l, int initialCapacity ) {
		super( initialCapacity );
		addListener( l );
	}

	public ObsVect( ObsVectListener l, int initialCapacity,
			int capacityIncrement ) {
		super( initialCapacity, capacityIncrement );
		addListener( l );
	}

	public ObsVect( ObsVectListener l ) {
		super();
		addListener( l );
	}

	public ObsVect( ObsVectListener l, Collection c ) {
		super( c );
		addListener( l );
	}

	public boolean add( Object obj ) {
		addElement( obj );
		return true;
	}

	public void addElement( Object obj ) {
		synchronized(this) {
		    super.addElement( obj );
		}
		Object[] data = new Object[ 3 ];
		data[ 0 ] = new Integer( ADD );
		data[ 1 ] = new Integer( size() - 1 );
		data[ 2 ] = obj;
		_observable.setChanged();
		_observable.notifyObservers( data );
	}

	public boolean addAll( Collection c ) {
		return addAll( size(), c );
	}

	public boolean addAll( int index, Collection c ) {
		boolean result;
		synchronized(this) {
		    result = super.addAll( index, c );
		}
	    if( result ) {
			Object[] data = new Object[ 3 ];
			data[ 0 ] = new Integer( ADD );
			Iterator iter = c.iterator();
			int i = 0;
			while( iter.hasNext() ) {
				data[ 1 ] = new Integer( ++i );
				data[ 2 ] = iter.next();
				_observable.setChanged();
				_observable.notifyObservers( data );
			}
			return true;
		}
		return false;
	}

	public void insertElementAt( Object obj, int index ) {
		synchronized(this) {
		    super.insertElementAt( obj, index );
		}		
		Object[] data = new Object[ 3 ];
		data[ 0 ] = new Integer( ADD );
		data[ 1 ] = new Integer( index );
		data[ 2 ] = obj;
		_observable.setChanged();
		_observable.notifyObservers( data );
	}

	public synchronized Object remove( int index ) {		
	    Object obj = get( index );
		removeElementAt( index );
		return obj;
	}

	public synchronized void removeAllElements() {	    
		Iterator iter = iterator();
		while( iter.hasNext() ) {
			iter.next();
			iter.remove();
		}
	}

	public void removeElementAt( int index ) {
	    Object obj;
	    synchronized (this) {
		    obj = get( index );
		    super.removeElementAt( index );
		}
		
		Object[] data = new Object[ 3 ];
		data[ 0 ] = new Integer( REMOVE );
		data[ 1 ] = new Integer( index );
		data[ 2 ] = obj;
		_observable.setChanged();
		_observable.notifyObservers( data );
	}

	public Object set( int index, Object element ) {		
	    Object removed;
	    synchronized(this) {
    	    removed = get( index );
    		super.set( index, element );
	    }
		Object[] data = new Object[ 3 ];
		data[ 0 ] = new Integer( REMOVE );
		data[ 1 ] = new Integer( index );
		data[ 2 ] = removed;
		_observable.setChanged();
		_observable.notifyObservers( data );
		data[ 0 ] = new Integer( ADD );
		data[ 1 ] = new Integer( index );
		data[ 2 ] = element;
		_observable.setChanged();
		_observable.notifyObservers( data );
		return removed;
	}

	public void setElementAt( Object obj, int index ) {
	    Object removed;
	    synchronized(this) {
    		removed = get( index );
    		super.setElementAt( obj, index );
	    }
		Object[] data = new Object[ 3 ];
		data[ 0 ] = new Integer( REMOVE );
		data[ 1 ] = new Integer( index );
		data[ 2 ] = removed;
		_observable.setChanged();
		_observable.notifyObservers( data );
		data[ 0 ] = new Integer( ADD );
		data[ 1 ] = new Integer( index );
		data[ 2 ] = obj;
		_observable.setChanged();
		_observable.notifyObservers( data );
	}

	public synchronized void  setSize( int newSize ) {
		if( newSize < size() ) {
			Iterator iter = listIterator( newSize );
			while( iter.hasNext() ) {
				iter.next();
				iter.remove();
			}
		}
		else
			super.setSize( newSize );
	}

	public void addListener( ObsVectListener listener ) {
		_observable.addObserver( new ObsVectAdapter( listener ) );
	}

	public void removeListener( ObsVectListener listener ) {
		_observable.deleteObserver( new ObsVectAdapter( listener ) );
	}

	private class MyObservable extends Observable {
		/**
		 * we must create a public setChanged because that method is protected
		 * in Observable
		 */
		public void setChanged() {
			super.setChanged();
		}
	}

	protected class ObsVectAdapter implements Observer {
		protected ObsVectListener _listener;

		public ObsVectAdapter( ObsVectListener listener ) {
			_listener = listener;
		}

		public void update( Observable o, Object arg ) {
			Object[] _arg = (Object[]) arg;
			switch( ((Integer) _arg[ 0 ]).intValue() ) {
			case ADD:
				_listener.onAdd( ((Integer) _arg[ 1 ]).intValue(), _arg[ 2 ] );
				break;

			case REMOVE:
				_listener
						.onRemove( ((Integer) _arg[ 1 ]).intValue(), _arg[ 2 ] );
				break;
			}
		}

		public boolean equals( Object o ) {
			return o instanceof ObsVectAdapter
					&& _listener.equals( ((ObsVectAdapter) o)._listener );
		}
	}
}

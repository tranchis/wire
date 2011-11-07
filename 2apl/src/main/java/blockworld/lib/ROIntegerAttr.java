package blockworld.lib;

/*

 This file is part of the Bushfire project.

 Copyright 2003, M. Albers, H Boros, R Burema, N. Goh, J. Herold, B.
 Maassen, R. Peek, J. Priem, Bas Steunebrink, P. van der Werken.

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

 $Id: ROIntegerAttr.java,v 1.3 2004/09/14 10:58:06 cvs-3apl Exp $

 */

import java.util.Observable;

public class ROIntegerAttr extends Observable implements Cloneable {
	String _name;

	Integer _value;

	public ROIntegerAttr( String name, Integer value ) {
		_name = name;
		_value = value;
	}

	public void addAttributeListener( IntegerAttrListener listener ) {
		addObserver( new IntegerAttrAdapter( listener ) );
	}

	public void removeAttributeListener( IntegerAttrListener listener ) {
		deleteObserver( new IntegerAttrAdapter( listener ) );
	}

	public int getValue() {
		return _value.intValue();
	}

	/**
	 * Similar to getValue, except that this method returns an Integer instead
	 * of an int
	 */
	public Integer getIntegerValue() {
		return _value;
	}

	public String getName() {
		return _name;
	}

	/**
	 * Returns a copy of this object. The clone has no observers.
	 * 
	 * @return A copy of this object.
	 */
	public Object clone() throws CloneNotSupportedException {
		ROIntegerAttr a = (ROIntegerAttr) super.clone();
		a._name = new String( _name );
		a._value = new Integer( _value.intValue() );
		a.deleteObservers();
		return a;
	}
}

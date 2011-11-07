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

 $Id: ObsVectListener.java,v 1.1 2004/02/17 19:24:46 cvs-3apl Exp $
 */

/** the Observable Vector listener interface */
public interface ObsVectListener {
	void onAdd( int index, Object element );

	void onRemove( int index, Object element );
}

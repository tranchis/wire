package blockworld.lib;

import java.util.Observable;
import java.util.Observer;

/*! The IntegerAttrAdapter wraps a IntegerAttrListener into a Observer object.
 * Calling update on this class will convert the arg parameter into a Integer
 * and call FloatAttrListener.onValueChange().
 * \todo this class can go away, implement Observable in IntegerAttr
 */
public class IntegerAttrAdapter implements Observer {
	protected IntegerAttrListener _listener;

	public IntegerAttrAdapter( IntegerAttrListener listener ) {
		_listener = listener;
	}

	public void update( Observable o, Object arg ) {
		_listener.onValueChange( (Integer) arg );
	}

	public boolean equals( Object o ) {
		return o instanceof IntegerAttrAdapter
				&& ((IntegerAttrAdapter) o)._listener.equals( _listener );
	}
}

package blockworld.lib;

public class IntegerAttr extends ROIntegerAttr {
	public IntegerAttr( String name ) {
		this( name, new Integer( 0 ) );
	}

	public IntegerAttr( String name, Integer value ) {
		super( name, value );
	}

	public IntegerAttr( String name, int value ) {
		super( name, new Integer( value ) );
	}

	/**
	 * Set the value, this notifies the IntegerAttrListener of changes.
	 * 
	 * @param value
	 *            value to be set
	 */
	public void setValue( Integer value ) {
		_value = value;
		setChanged();
		notifyObservers( value );
	}

	/** Convenience method. Calls setValue() with integer argument */
	public void setValue( int value ) {
		setValue( new Integer( value ) );
	}

	public void setName( String name ) {
		_name = name;
	}
}

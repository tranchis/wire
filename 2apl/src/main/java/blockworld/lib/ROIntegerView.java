package blockworld.lib;

import javax.swing.JLabel;

public class ROIntegerView extends JLabel implements IntegerAttrListener {

	private static final long serialVersionUID = -4767694535801030958L;

	protected ROIntegerAttr _attr;

	public ROIntegerView( ROIntegerAttr attr ) {
		_attr = attr;
		_attr.addAttributeListener( this );

		// update text
		onValueChange( new Integer( attr.getValue() ) );
	}

	public void onValueChange( Integer value ) {
		setText( value.toString() );
	}
}

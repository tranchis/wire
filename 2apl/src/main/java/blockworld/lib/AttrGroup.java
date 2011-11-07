package blockworld.lib;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Groups a set of attribute views together into a Panel. The group has a title
 * that is shown in a border.
 */
public class AttrGroup extends JPanel {

	private static final long serialVersionUID = -1420253091328660372L;

	protected GridBagLayout gridbag = new GridBagLayout();

	protected GridBagConstraints constraints = new GridBagConstraints();

	/** Construct a AttrGroup with the title "Attributes". */
	public AttrGroup() {
		this( "Attributes" );
	}

	/**
	 * Construct a group with the specified title.
	 * 
	 * @param title
	 *            The title of this group, this shown in a border.
	 */
	public AttrGroup( String title ) {
		Border etche = BorderFactory.createEtchedBorder();

		setBorder( BorderFactory.createTitledBorder( etche, title ) );
		// setLayout(new GridLayout(0, 2, 5, 0));

		// / set initial layout settings
		setLayout( gridbag );
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets( 1, 5, 2, 5 );
	}

	protected void addRow( String descr, JComponent view ) {
		JLabel label = new JLabel( descr );

		label.setLabelFor( view );

		constraints.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
		constraints.fill = GridBagConstraints.NONE; // reset to default
		constraints.weightx = 0.0; // reset to default

		gridbag.setConstraints( label, constraints );
		add( label );

		constraints.gridwidth = GridBagConstraints.REMAINDER; // end row
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		gridbag.setConstraints( view, constraints );
		add( view );
	}

	/**
	 * Add a read only int attribute to this attribute group. This constructs
	 * the correct view and adds a label showing the attribute name.
	 */
	public void add( ROIntegerAttr attr ) {
		addRow( attr.getName(), new ROIntegerView( attr ) );
	}
}

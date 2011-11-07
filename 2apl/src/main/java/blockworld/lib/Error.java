package blockworld.lib;

public class Error extends Exception {

	private static final long serialVersionUID = 1502566209757475201L;

	public Error( String s ) {
		super( s );
	}

	public Error( Exception e ) {
		super( e.getMessage() );
	}

	public Error( String s, Exception e ) {
		super( s + ": " + e.getMessage() );
	}
}

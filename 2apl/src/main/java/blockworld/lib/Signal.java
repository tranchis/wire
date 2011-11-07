package blockworld.lib;

/// works like an observable, allows emitting signals
import java.util.Observable;

public class Signal extends Observable {
	protected String _name;

	protected long _counter = 0;

	public Signal( String name ) {
		_name = name;
	}

	public void emit() {
		emit( null );
	}

	public void emit( Object o ) {
		setChanged();
		notifyObservers( o );
		_counter++;
	}

	public void setChanged() {
		super.setChanged();
	}

	public void clearChanged() {
		super.clearChanged();
	}

	public String toString() {
		return _name;
	}

	public String getName() {
		return _name;
	}

	public long getEmitCount() {
		return _counter;
	}

}

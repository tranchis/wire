package blockworld;

import java.awt.Point;

// APLIdentifiable Object representation in 3APL BlockWorld
public class TypeObject implements java.io.Serializable {
	private String _type;
	private Point _position;

	public TypeObject( String type, Point position ) {
		_type = type;
		_position = position;
	}

	public String toString() {
		return _type+"("+_position.x+","+_position.y+")";
	}

	public Point getPosition() {
		return _position;
	}

	public void setPosition(Point position) {
		_position = position;
	}

	public String getType() {
		return _type;
	}

	public void setType(String type) {
		_type = type;
	}
}

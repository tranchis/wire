package sim.portrayal.inspector;
import sim.util.*;
import sim.field.*;
import sim.field.grid.*;
import sim.field.continuous.*;

/**
   StableDouble2D is a StableLocation for Double2D.  See StableLocation for more information.
*/

public class StableDouble2D implements StableLocation
    {
    public double x = 0;
    public double y = 0;
    public boolean exists;
    public Continuous2D field;
    public Object object;
        
    public String toString()
        {
		update();
        if (!exists) return "Gone";
        else return "(" + x + ", " + y + ")"; 
        }
    
    public StableDouble2D(Continuous2D field, Object object)
        {
        this.field = field;
        this.object = object;
        }
	
	void update()
        {
        Double2D pos = null;
        if (field != null) pos = field.getObjectLocation(object);
        if (pos == null) { exists = false; }  // purposely don't update x and y so they stay the same
        else { x = pos.x; y = pos.y; exists = true; }
        }

    public double getX() { update(); return x; }
    public double getY() { update(); return y; }
    public boolean getExists() { update(); return exists; }  // what an ugly name
            
    public void setX(double val)
        {
        if (field!=null) field.setObjectLocation(object, new Double2D(val,getY()));
		x = val;
		exists = true;
        }

    public void setY(double val)
        {
        if (field!=null) field.setObjectLocation(object, new Double2D(getX(),val));
		y = val;
		exists = true;
        }

// playing with too much fire
/* 
   public void setExists(boolean val)
   {
   exists = val;
   if (exists)
   { if (field!=null) field.setObjectLocation(object, new Double2D(x,y)); }
   else
   { if (field!=null) field.remove(object); } // too powerful?
   }
*/
    }

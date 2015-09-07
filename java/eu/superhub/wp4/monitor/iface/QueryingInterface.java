package eu.superhub.wp4.monitor.iface;

import java.io.OutputStream;
import java.util.Collection;

import net.sf.ictalive.runtime.event.Event;
import eu.superhub.wp4.monitor.core.IMonitor;
import eu.superhub.wp4.monitor.core.domain.EventTemplate;

public interface QueryingInterface {
	public OutputStream getStream(IMonitor m, EventTemplate et);

	public Collection<Event> query(IMonitor m, EventTemplate et);
}

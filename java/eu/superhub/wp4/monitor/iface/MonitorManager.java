package eu.superhub.wp4.monitor.iface;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import eu.superhub.wp4.models.mobilitypolicy.Metric;
import eu.superhub.wp4.monitor.core.IMonitor;
import eu.superhub.wp4.monitor.wp3servicedata.ISituationalDataListener;

public interface MonitorManager extends ISituationalDataListener {
	public IMonitor createInstance(String host, int port);

	public Collection<IMonitor> listInstances();

	public void updateConfiguration(IMonitor m, Configuration c);

	public void start(IMonitor m);

	public void pause(IMonitor m);

	public void restart(IMonitor m);

	public void stop(IMonitor m);

	public BlockingQueue<Metric> getMetricInputStream(IMonitor m);

	public BlockingQueue<Object> getSituationInputStream(IMonitor m); // TODO:
																		// To be
																		// updated
																		// when
																		// there
																		// is a
																		// model
																		// for
																		// Situation

	public int getCount(IMonitor im);
}

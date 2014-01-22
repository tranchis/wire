package eu.superhub.wp4.monitor.iface.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import clojure.lang.RT;
import clojure.lang.Var;
import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp4.models.mobilitypolicy.Metric;
import eu.superhub.wp4.monitor.core.IMonitor;
import eu.superhub.wp4.monitor.iface.Configuration;
import eu.superhub.wp4.monitor.iface.MonitorManager;

public class MonitorManagerImpl implements MonitorManager {
    
    private Var			vCreateMonitor, vListInstances, vStartMonitor, vPauseMonitor,
    				vRestartMonitor, vStopMonitor, vGetCount, vPushStatement;
    private List<IMonitor>	lm;

    public MonitorManagerImpl() throws IOException {
	long millis = System.currentTimeMillis();
	// TODO: convert Monitor.clj into gen-class
	RT.loadResourceScript("eu/superhub/wp4/monitor/core/Monitor.clj");
	System.out.println("time: " + (System.currentTimeMillis() - millis));
	vCreateMonitor = RT.var("eu.superhub.wp4.monitor.core.Monitor",
		"create-monitor");
	vListInstances = RT.var("eu.superhub.wp4.monitor.core.Monitor",
		"list-instances");
	vStartMonitor = RT.var("eu.superhub.wp4.monitor.core.Monitor",
		"start-monitor");
	vPauseMonitor = RT.var("eu.superhub.wp4.monitor.core.Monitor",
		"pause-monitor");
	vStopMonitor = RT.var("eu.superhub.wp4.monitor.core.Monitor",
		"stop-monitor");
	vRestartMonitor = RT.var("eu.superhub.wp4.monitor.core.Monitor",
		"restart-monitor");
	vGetCount = RT.var("eu.superhub.wp4.monitor.core.Monitor",
		"get-count");
	vPushStatement = RT.var("eu.superhub.wp4.monitor.core.Monitor", "push-statement");
	lm = new ArrayList<IMonitor>();
    }

    public IMonitor createInstance(String host, int port) {
	IMonitor m;

	m = (IMonitor) vCreateMonitor.invoke(host, port);
	lm.add(m);
	m.getRuleEngine();

	return m;
    }

    @SuppressWarnings("unchecked")
    public Collection<IMonitor> listInstances() {
	return (Collection<IMonitor>) vListInstances.invoke();
    }

    public void updateConfiguration(IMonitor m, Configuration c) {
	// TODO: Complete this
//	m.updateRules(convertIntoPackage(c.getPolicyModel());
    }

    public void start(IMonitor m) {
	vStartMonitor.invoke(m);
    }

    public void pause(IMonitor m) {
	vPauseMonitor.invoke(m);
    }

    public void restart(IMonitor m) {
	vRestartMonitor.invoke(m);
    }

    public void stop(IMonitor m) {
	vStopMonitor.invoke(m);
    }

    public int getCount(IMonitor im) {
	return ((Long) vGetCount.invoke(im)).intValue();
    }

    public void push(Statement s) {
	vPushStatement.invoke(s);
    }

    @Override
    public BlockingQueue<Metric> getMetricInputStream(IMonitor m) {
	throw new UnsupportedOperationException();
    }

    @Override
    public BlockingQueue<Object> getSituationInputStream(IMonitor m) {
	throw new UnsupportedOperationException();
    }
}

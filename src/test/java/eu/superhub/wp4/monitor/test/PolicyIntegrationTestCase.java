package eu.superhub.wp4.monitor.test;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp4.models.mobilitypolicy.PolicyModel;
import eu.superhub.wp4.monitor.core.IMonitor;
//import eu.superhub.wp4.monitor.core.PolicyMonitor;
import eu.superhub.wp4.monitor.iface.Configuration;
import eu.superhub.wp4.monitor.iface.MonitorManager;
import eu.superhub.wp4.monitor.iface.impl.ConfigurationImpl;
import eu.superhub.wp4.monitor.iface.impl.MonitorManagerImpl;
import eu.superhub.wp4.monitor.situationaldata.SituationalDataPusher;

public class PolicyIntegrationTestCase {
	private Logger logger;

	public PolicyIntegrationTestCase() throws IOException {
		logger = LoggerFactory.getLogger(getClass());
	}

	@Test
	public void testIntegration() throws IOException {
//		PolicyMonitor pm;
//		MonitorManager mm;
//		IMonitor m;
//		Configuration c;
//		SituationalDataPusher sdp;
//		PolicyModel p;
//
//		pm = new PolicyMonitor();
//		p = pm.getPolicy();
//		logger.info(pm.getPolicy().toString());
//		mm = new MonitorManagerImpl();
//		m = mm.createInstance("tranchis.mooo.com", 61616);
//		sdp = new SituationalDataPusher(mm);
//		c = new ConfigurationImpl(p, sdp);
//		mm.updateConfiguration(m, c);
	}
}

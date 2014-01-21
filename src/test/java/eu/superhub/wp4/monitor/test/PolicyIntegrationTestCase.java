package eu.superhub.wp4.monitor.test;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyIntegrationTestCase {
    @SuppressWarnings("unused")
	private Logger	logger;
    
    public PolicyIntegrationTestCase() throws IOException {
	logger = LoggerFactory.getLogger(getClass());
    }
    
    @Test
    public void testIntegration() {
    }
    /*
    public void testIntegration() throws IOException {    	
	PolicyMonitor		pm;
	MonitorManager		mm;
	IMonitor		m;
	Configuration		c;
	SituationalDataPusher	sdp;
	PolicyModel		p;
	
        boolean do_test = false;
        
        if (do_test){
	pm = new PolicyMonitor();
	p = pm.getPolicy();
	logger.info(pm.getPolicy().toString());
	mm = new MonitorManagerImpl();
	m = mm.createInstance("tranchis.mooo.com", 61616);
	sdp = new SituationalDataPusher(mm);
	c = new ConfigurationImpl(p, sdp);
	mm.updateConfiguration(m, c);
	
        }
        
    }
    */
}

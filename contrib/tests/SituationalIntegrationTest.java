package eu.superhub.wp4.monitor.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp4.monitor.situationaldata.ISituationalDataListener;
import eu.superhub.wp4.monitor.situationaldata.SituationalDataPusher;

public class SituationalIntegrationTest implements ISituationalDataListener {
    
    private Logger	logger;
    
    public SituationalIntegrationTest() {
	logger = LoggerFactory.getLogger(getClass());
    }
    
    @Test
    public void testIntegration() {
	SituationalDataPusher	sdp;
	
	sdp = new SituationalDataPusher(this);
	logger.info(sdp.getName());
    }

    public void push(Statement s) {
	System.out.println(s);
    }
}

package eu.superhub.wp4.monitor.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp4.monitor.wp3servicedata.ISituationalDataListener;
import eu.superhub.wp4.monitor.wp3servicedata.SituationalDataPusher;
import eu.superhub.wp4.monitor.wp3servicedata.SituationalDataServiceParameters;

public class SituationalIntegrationTest implements ISituationalDataListener {
    
    private Logger	logger;
    
    public SituationalIntegrationTest() {
	logger = LoggerFactory.getLogger(getClass());
    }
    
    @Test
    public void test() {
	SituationalDataPusher	sdp;
	SituationalDataServiceParameters sdsp;
	
	logger.info("<---------- Starting service enactment test -------->");
	//sdsp = new SituationalDataServiceParameters();
	//sdp = new SituationalDataPusher(this,sdsp);
	//logger.info(sdp.getName());
	logger.info("<---------- Finishing service enactment test -------->");
    }

    public void push(Statement s) {
	System.out.println(s);
    }
    
    public static void main(String[] args)
    {
		
    	SituationalIntegrationTest dummy = new SituationalIntegrationTest();
		dummy.test();
		
    }
}

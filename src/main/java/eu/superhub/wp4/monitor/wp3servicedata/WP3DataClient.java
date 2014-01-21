package eu.superhub.wp4.monitor.wp3servicedata;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.models.situationaldatamodel.interfaces.SituationalDataResponse;
import eu.superhub.wp3.models.situationaldatamodel.statements.Raw;
import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;

public class WP3DataClient extends Thread {
    
    private BlockingQueue<Statement>	queue;
    private Logger			logger;
    SituationalDataServiceParameters sdsp;
    
    public WP3DataClient(SituationalDataServiceParameters _sdsp) {
	// TODO: Switch to Apache Camel?
	queue = new SynchronousQueue<Statement>();
	logger = LoggerFactory.getLogger(getClass());
        sdsp = _sdsp;
	start();
    }
    
    public void run() {	
	SituationalDataResponse	sdrs;
	Iterator<Statement>	is;
	
	SituationalDataServiceWrapper sdsw = new SituationalDataServiceWrapper();
        TrafficSituationServiceWrapper tssw = new TrafficSituationServiceWrapper();
	while (true) {
            try {
                
                sdrs = sdsw.enactServiceTrafficSocialNetwork(sdsp.getServiceTrafficSocialNetworkCityName(),sdsp.getServiceTrafficSocialNetworkStateAbbr(),sdsp.getServiceTrafficSocialNetworkStateName(), sdsp.getServiceTrafficSocialNetworkLatitude(), sdsp.getServiceTrafficSocialNetworkLongitude());
                is = sdrs.getStatement().iterator();
                while(is.hasNext())
                {
                    try {
                        queue.put(is.next());
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
    //		    e.printStackTrace();
                    }
                }
                
                String resp = tssw.enactServicePredictedTrafficFromCellNet(sdsp.getServicePredictedTrafficFromCellNetLongitude(),sdsp.getServicePredictedTrafficFromCellNetLongitude());    
                //Create a raw statement for holding response
                Raw predictTrafficFromCellNetStatement = new Raw();
                predictTrafficFromCellNetStatement.setLocalReliabilityScore(100);
                predictTrafficFromCellNetStatement.setMessage(resp);
                 try {
                        queue.put(predictTrafficFromCellNetStatement);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
    //		    e.printStackTrace();
                    }
                
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                        logger.error(e.getMessage());
    //		    e.printStackTrace();
                }
                
            } catch (WP3DataClientException ex) {
                java.util.logging.Logger.getLogger(WP3DataClient.class.getName()).log(Level.SEVERE, null, ex);
            }	    
	}
    }

    public Statement take() throws InterruptedException {
	return queue.take();
    }
}

package eu.superhub.wp4.monitor.wp3servicedata;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.marshaller.GenericMarshaller;
import eu.superhub.wp3.models.situationaldatamodel.interfaces.SituationalDataResponse;
import eu.superhub.wp3.models.situationaldatamodel.statements.Raw;
import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.Accident;

public class WP3DataClient extends Thread {
    
    private BlockingQueue<Statement>	queue;
    private Logger			logger;
    SituationalDataServiceParameters sdsp;
    private volatile boolean running = true;
    
    public WP3DataClient(SituationalDataServiceParameters _sdsp) { 
	queue = new SynchronousQueue<Statement>();
	logger = LoggerFactory.getLogger(getClass());
        sdsp = _sdsp;
	start();
    }
    
    public void terminate()
    {
    	running = false;
    }
    
    public void run() {	
	SituationalDataResponse	sdrs;
	Iterator<Statement>	is;
		
	
	SituationalDataServiceWrapper sdsw = new SituationalDataServiceWrapper();
        TrafficSituationServiceWrapper tssw = new TrafficSituationServiceWrapper();        
	while (running) {
		logger.info("WP3 Data Client is running '" + running + "'");
            try {
            	if (sdsp.isSituationalDataParameter())
            	{
	            	String respStr = sdsw.enactServiceTrafficSocialNetwork(sdsp.getServiceTrafficSocialNetworkCityName(),sdsp.getServiceTrafficSocialNetworkStateAbbr(),sdsp.getServiceTrafficSocialNetworkStateName(), sdsp.getServiceTrafficSocialNetworkLongitude(), sdsp.getServiceTrafficSocialNetworkLatitude(), sdsp.getServiceStatementType());
	            	//System.out.println(respStr);
	    			GenericMarshaller<SituationalDataResponse> situationalDataResponseMarshaller = new GenericMarshaller<SituationalDataResponse>(SituationalDataResponse.class, Accident.class);
	                sdrs = situationalDataResponseMarshaller.xmlToJava(respStr);
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
	                try {
	                    sleep(sdsp.getSamplingPeriod());
	                } catch (InterruptedException e) {
	                        logger.error(e.getMessage());		   
	                }
            	}
            	if (sdsp.isTrafficDataParameter())
            	{
            	
	                String resp = tssw.enactServicePredictedTrafficFromCellNet(sdsp.getServicePredictedTrafficFromCellNetLongitude(),sdsp.getServicePredictedTrafficFromCellNetLatitude());    
	                //Create a raw statement for holding response
	                Raw predictTrafficFromCellNetStatement = new Raw();
	                predictTrafficFromCellNetStatement.setLocalReliabilityScore(100);
	                predictTrafficFromCellNetStatement.setMessage(resp);
	                 try {
	                        queue.put(predictTrafficFromCellNetStatement);
	                    } catch (InterruptedException e) {
	                        logger.error(e.getMessage());		    
	                    }
	                
	                try {
	                	sleep(sdsp.getSamplingPeriod());
	                } catch (InterruptedException e) {
	                        logger.error(e.getMessage());		   
	                }
            	}
                
               
            } catch (WP3DataClientException | JAXBException ex) {
                java.util.logging.Logger.getLogger(WP3DataClient.class.getName()).log(Level.SEVERE, null, ex);
            }	    
	}
	logger.info("WP3 Data Client is stopping");
	return;
    }

    public Statement take() throws InterruptedException {
	return queue.take();
    }
}
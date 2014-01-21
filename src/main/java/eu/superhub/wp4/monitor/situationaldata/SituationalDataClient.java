package eu.superhub.wp4.monitor.situationaldata;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.models.situationaldatamodel.interfaces.SituationalDataRequest;
import eu.superhub.wp3.models.situationaldatamodel.interfaces.SituationalDataResponse;
import eu.superhub.wp3.models.situationaldatamodel.interfaces.StatementType;
import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp3.situationaldata.provider.SituationalDataProvider;

public class SituationalDataClient extends Thread {
    
    private BlockingQueue<Statement>	queue;
    private Logger			logger;
    
    public SituationalDataClient() {
	// TODO: Switch to Apache Camel?
	queue = new SynchronousQueue<Statement>();
	logger = LoggerFactory.getLogger(getClass());
	start();
    }
    
    public void run() {
	SituationalDataRequest	sdrq;
	SituationalDataResponse	sdrs;
	Iterator<Statement>	is;
	
	sdrq = new SituationalDataRequest();
	sdrq.setStatementType(StatementType.TRAFFIC_FROM_SOCIAL_NETWORK);
	while (true) {
	    sdrs = SituationalDataProvider.getSituationalData(sdrq);
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
		sleep(100);
	    } catch (InterruptedException e) {
		    logger.error(e.getMessage());
//		    e.printStackTrace();
	    }
	}
    }

    public Statement take() throws InterruptedException {
	return queue.take();
    }
}

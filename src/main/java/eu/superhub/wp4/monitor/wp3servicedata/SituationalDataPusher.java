package eu.superhub.wp4.monitor.wp3servicedata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;

public class SituationalDataPusher extends Thread {
    
    private WP3DataClient	sdc;
    private ISituationalDataListener	sdl;
    private Logger			logger;
    
    public SituationalDataPusher(ISituationalDataListener sdl) {
	this.sdl = sdl;
        logger = LoggerFactory.getLogger(getClass());
        logger.warn("No 'SituationalDataServiceParameters' provided, monitor is using default test values!");
        SituationalDataServiceParameters sdsp = new SituationalDataServiceParameters();
	sdc = new WP3DataClient(sdsp);
	logger = LoggerFactory.getLogger(getClass());
	start();
    }
    
    public SituationalDataPusher(ISituationalDataListener sdl, SituationalDataServiceParameters sdsp) {
	this.sdl = sdl;
	sdc = new WP3DataClient(sdsp);
	logger = LoggerFactory.getLogger(getClass());
	start();
    }
    
    public void run() {
	Statement	s;
	
	while(true) {
	    try {
		s = sdc.take();
		sdl.push(s);
	    } catch (InterruptedException e) {
		logger.error(e.getMessage());
//		e.printStackTrace();
	    }
	}
    }
}

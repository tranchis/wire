package eu.superhub.wp4.monitor.situationaldata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;

public class SituationalDataPusher extends Thread {

	private SituationalDataClient sdc;
	private ISituationalDataListener sdl;
	private Logger logger;

	public SituationalDataPusher(ISituationalDataListener sdl) {
		this.sdl = sdl;
		sdc = new SituationalDataClient();
		logger = LoggerFactory.getLogger(getClass());
		start();
	}

	public void run() {
		Statement s;

		while (true) {
			try {
				s = sdc.take();
				sdl.push(s);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
				// e.printStackTrace();
			}
		}
	}
}

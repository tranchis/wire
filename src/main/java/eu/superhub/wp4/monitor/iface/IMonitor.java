package eu.superhub.wp4.monitor.iface;

import java.sql.Timestamp;

import eu.superhub.wp4.models.mobilitypolicy.SampleSeriesSet;
import eu.superhub.wp4.monitor.commons.MonitorException;

public interface IMonitor {
	
	/*
	 * @return Returns all the samples for the requested Indicator between the begin and end time. Uses a SampleSeries class as seen in policy model
     * @param indicatorId  int: ID of the indicator for which samples are requested.
     * @param beginTime    Timestamp: Begin of the period for which samples are requested.
     * @param endTime    Timestamp: End of the period for which samples are requested.
	 */
	SampleSeriesSet getIndicatorSamples(int IndicatorID, Timestamp beginTime, Timestamp endTime) throws MonitorException;
	
	/*
	 * @return Returns all the samples for the requested success Indicator between the begin and end time. Uses a SampleSeries class as seen in policy model
     * @param indicatorId  int: ID of the indicator for which samples are requested.
     * @param beginTime    Timestamp: Begin of the period for which samples are requested.
     * @param endTime    Timestamp: End of the period for which samples are requested.
	 */
	SampleSeriesSet getSuccessIndicatorSamples(int SuccessIndicatorID, Timestamp beginTime, Timestamp endTime) throws MonitorException;
	
	/*
	 * @return void. Just starts the monitoring process
     * @param SamplingPeriod  long: Frequency of invocation of the WP3 services > 1000
	 */
	void startMonitor(long SamplingPeriod) throws MonitorException;
	
	/*
	 * @return void. Just stops the monitoring process 
	 */
	void stopMonitor() throws MonitorException;
}

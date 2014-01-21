/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.superhub.wp4.monitor.iface.impl;

import eu.superhub.wp4.encoder.core.PolicyEncoderCore;
import eu.superhub.wp4.models.mobilitypolicy.Indicator;
import eu.superhub.wp4.models.mobilitypolicy.PolicyModel;
import eu.superhub.wp4.models.mobilitypolicy.SampleSeriesSet;
import eu.superhub.wp4.monitor.indicatorcomputation.IndicatorComputation;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author igomez
 */
public class IndicatorSampleGenerator 
{
    IndicatorComputation dummy;
    private static Logger	logger;
    ArrayList<Integer> indicatorIds;
    Timestamp beginTime, endTime;
    
    public IndicatorSampleGenerator(ArrayList<Integer> _indicatorIds, Timestamp _beginTime, Timestamp _endTime)
    {
        beginTime = _beginTime;
        endTime = _endTime;
        indicatorIds = _indicatorIds;
        logger = LoggerFactory.getLogger(getClass());
        dummy = new IndicatorComputation();
        try
    	{    	
            PolicyEncoderCore core = new PolicyEncoderCore();
            PolicyModel policyModel = core.getPolicyModelFromDB();   
            for (Indicator indicatorMonitor : policyModel.getIndicators().getIndicator())
            {
                if (indicatorIds.contains(indicatorMonitor.getId()))
                {
                    //logger.info("Pelusso : " + indicatorMonitor.getId());
                    Calendar c = Calendar.getInstance();
                    c.setTime(new Date(beginTime.getTime()));
                    int timeUnit = timeUnit = Calendar.DATE;
                    //logger.info("Pelussitas : " + c.getTime() + "|" + endTime);
                    while (endTime.after(c.getTime()))
                    {
                        //logger.info("Maldades : " + c.getTime());
                        double value = generateRandomSample(indicatorMonitor.getId());
                        Timestamp stamp = new Timestamp(c.getTime().getTime());
                        dummy.registerDailySampleValue(stamp, indicatorMonitor,value);
                        c.add(timeUnit, 1);
                    }
                }
            }
            
        }
        catch (Exception E)
        {
            logger.error(E.getMessage());
            E.printStackTrace();
        }
        
        
    }
    
    private double generateRandomSample(Integer indicatorID)
    {
        double result = 666;
        return result;
    }
    
    public String getStringSamples()
    {
        return dummy.toXMLString();
    }
    
    public SampleSeriesSet getSamples()
    {
        return dummy.getIndicatorSamples(indicatorIds, beginTime, endTime);
    }
}

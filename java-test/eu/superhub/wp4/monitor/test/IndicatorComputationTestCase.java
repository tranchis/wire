/*******************************************************************************
* Copyright (c) 2013 SUPERHUB - SUstainable and PERsuasive Human Users moBility                 www.superhub-project.eu/
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* 	  Ignasi Gomez: Developing code to test computation of Indicators from Service data and TimeSeries generation
******************************************************************************/

package eu.superhub.wp4.monitor.test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.marshaller.GenericMarshaller;
import eu.superhub.wp3.models.situationaldatamodel.general.trafficenum.TrafficStatusValueEnum;
import eu.superhub.wp3.models.situationaldatamodel.general.unexpectedenum.AbnormalTrafficTypeEnum;
import eu.superhub.wp3.models.situationaldatamodel.statements.Normalized;
import eu.superhub.wp3.models.situationaldatamodel.statements.Raw;
import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.traffic.SocialData;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.traffic.TrafficFromSocialNetwork;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.AbnormalTraffic;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.Accident;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.PeopleInvolved;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.VehiclesInvolved;
import eu.superhub.wp3.models.situationaldatamodel.time.Stamp;
import eu.superhub.wp4.encoder.core.PolicyEncoderCore;
import eu.superhub.wp4.models.mobilitypolicy.Indicator;
import eu.superhub.wp4.models.mobilitypolicy.IndicatorTemplate;
import eu.superhub.wp4.models.mobilitypolicy.Indicators;
import eu.superhub.wp4.models.mobilitypolicy.PolicyImplementationStatus;
import eu.superhub.wp4.models.mobilitypolicy.PolicyModel;
import eu.superhub.wp4.models.mobilitypolicy.SampleSeriesSet;
import eu.superhub.wp4.models.mobilitypolicy.TimeUnit;
import eu.superhub.wp4.monitor.iface.IMonitor;
import eu.superhub.wp4.monitor.iface.impl.IMonitorImplementation;
import eu.superhub.wp4.monitor.iface.impl.IndicatorSampleGenerator;
import eu.superhub.wp4.monitor.indicatorcomputation.IndicatorComputation;
import javax.xml.bind.JAXBException;
import static junit.framework.Assert.assertTrue;

public class IndicatorComputationTestCase extends TestCase {
	
	
private static Logger	logger;
    
    public IndicatorComputationTestCase() {
	logger = LoggerFactory.getLogger(getClass());
    }
    
    @Test
    public void test() {
        //StringCastTest();
    	//SimpleTest();
    	//StatementTest();
    	ServiceTest();
        //IndicatorSampleGeneratorTest();
    }
             
    public class IndicatorComputationTestCaseThread extends Thread
    {
    	IMonitor dummy;
    	public IndicatorComputationTestCaseThread()
    	{
    		dummy = new IMonitorImplementation();
    	}
    	public void run()
    	{
    		logger.info("<---------- Starting Indicator Computation Test. Service test thread -------->");
    		try {
				dummy.startMonitor(5000);
				logger.info("Start Thread sleep");
				for (int count_chocula = 0; count_chocula < 60; count_chocula ++)
				{
					this.sleep(500);
					logger.info("Sleeping '" + count_chocula +"'");
				}
				logger.info("Finish thread sleep");
                                /*
				Indicators indicators = new Indicators();
                                
				PolicyEncoderCore core = new PolicyEncoderCore();
	    		PolicyModel policyModel = core.getPolicyModelFromDB();    		
				ArrayList<Integer> indicatorIds = new ArrayList<Integer>();
	    		for (Indicator indicatorMonitor : policyModel.getIndicators().getIndicator())
	        	{	    		  	    	
	        		for (IndicatorTemplate indicatorTemplate : policyModel.getIndicatorTemplates().getIndicatorTemplate())
	        		{	    		    			
	        			if ((indicatorMonitor.getTemplateId().equalsIgnoreCase(indicatorTemplate.getId()) && (indicatorTemplate.getFormula().equalsIgnoreCase("Monitoring Indicator"))))
	        			{
	        				if (!indicators.getIndicator().contains(indicatorMonitor))
	        				{
	        					indicators.getIndicator().add(indicatorMonitor);
	        					indicatorIds.add(indicatorMonitor.getId());
	        				}
	        			}
	        		}
	        	}
                        */
                        ArrayList<Integer> indicatorIds = new ArrayList<Integer> ();
                        indicatorIds.add(1001);
                        indicatorIds.add(1002);
                        indicatorIds.add(1003);
                        indicatorIds.add(1004);
                        indicatorIds.add(1005);
                        indicatorIds.add(1006);
                        indicatorIds.add(1007);
                        indicatorIds.add(1008);
                        indicatorIds.add(6);
                        indicatorIds.add(5);    
                        indicatorIds.add(4);  
                        ArrayList<Integer> successIndicatorIds = new ArrayList<Integer> ();
                        successIndicatorIds.add(1);
                        successIndicatorIds.add(2);
                        successIndicatorIds.add(3);
                        successIndicatorIds.add(991);
                        successIndicatorIds.add(992);
                        successIndicatorIds.add(993);
	    		logger.info("Computing Sample series Start");
                         Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        Timestamp endTime = new Timestamp(c.getTime().getTime());
                        int timeUnit = timeUnit = Calendar.DATE;
                        c.add(timeUnit, -15);
                        Timestamp beginTime = new Timestamp(c.getTime().getTime());
	    		for (Integer value : indicatorIds)
	    		{
	    			SampleSeriesSet sampleSeriesSet = dummy.getIndicatorSamples(value, beginTime ,endTime);

	    			String result = "";
	    			GenericMarshaller<SampleSeriesSet> mrsh = new GenericMarshaller<SampleSeriesSet>(
	    					SampleSeriesSet.class);	    			
	    			result = mrsh.javaToXml(sampleSeriesSet);
	    			logger.info(result);	    			
	    		}
                        for (Integer value : successIndicatorIds)
	    		{
                                SampleSeriesSet sampleSeriesSet = dummy.getSuccessIndicatorSamples(value, beginTime ,endTime);

	    			String result = "";
	    			GenericMarshaller<SampleSeriesSet> mrsh = new GenericMarshaller<SampleSeriesSet>(
	    					SampleSeriesSet.class);	    			
	    			result = mrsh.javaToXml(sampleSeriesSet);
	    			logger.info(result);	   
                        }
	    		logger.info("Computing Sample series Finish");
                        dummy.stopMonitor();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    		
    		
    		logger.info("<---------- Finishing Indicator Computation Test. Service test thread -------->");
    		return;
    	}
    }
    
    private void IndicatorSampleGeneratorTest()
    {
        logger.info("Starting IndicatorSampleGeneratorTest");
        
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Timestamp beginTime = new Timestamp(c.getTime().getTime());
        int timeUnit = timeUnit = Calendar.DATE;
        c.add(timeUnit, 5);
        Timestamp endTime = new Timestamp(c.getTime().getTime());
        logger.info("   · beginTime : '" + beginTime + "'");
        logger.info("   · endTime : '" + endTime + "'");
        ArrayList<Integer> indicatorIds = new ArrayList<Integer> ();
        indicatorIds.add(1001);
        indicatorIds.add(1002);
        indicatorIds.add(1003);
        indicatorIds.add(1004);
        indicatorIds.add(1005);
        indicatorIds.add(1006);
        indicatorIds.add(1007);
        indicatorIds.add(1008);
        indicatorIds.add(4);
        indicatorIds.add(5);
        indicatorIds.add(6);
        for (Integer element  : indicatorIds)
        {
            logger.info("   · indicatorId : '" + element + "'");
        }
        IndicatorSampleGenerator dummy = new IndicatorSampleGenerator(indicatorIds, beginTime, endTime);
        logger.info("");
        SampleSeriesSet sampleSeriesSet = dummy.getSamples();
        String Stringresult = "";
		GenericMarshaller<SampleSeriesSet> mrsh = new GenericMarshaller<SampleSeriesSet>(
				SampleSeriesSet.class);
		try {
			Stringresult = mrsh.javaToXml(sampleSeriesSet);
                        logger.info("<----- Success indicator final results ------>");
                        logger.info(Stringresult);
                        logger.info("<----------->");
		} catch (JAXBException e) {			
			logger.error(e.getStackTrace().toString());
		}
        logger.info(Stringresult);
        logger.info("");
        
        logger.info("Finishing IndicatorSampleGeneratorTest");
    }
    
    private void StringCastTest()
    {
        logger.info("Starting String cast test");
        try
        {
            String goodName = "final";
            String badName = "FINAL";
            
            PolicyImplementationStatus.fromValue(goodName);
            PolicyImplementationStatus.fromValue(badName);
        }
        catch (Exception E)
        {
            logger.info(E.getMessage());
            assertTrue(false);
        }
        logger.info("Finished String cast test");
    }
    
    public void ServiceTest() {
    	logger.info("<---------- Starting Indicator Computation Test. Service test -------->");
    	
    	try
    	{  
    		IndicatorComputationTestCaseThread dummy = new IndicatorComputationTestCaseThread();
    		dummy.start();
    		dummy.join();
    	}
    	catch (Exception E)
    	{
    		E.printStackTrace();
    		logger.info(E.getMessage());
    		assert(false);
    	} 
    	
    	
    	
    	logger.info("<---------- Finishing Indicator Computation Test. Service test -------->");
    }
    
    public void StatementTest() {
    	logger.info("<---------- Starting Indicator Computation Test. Statement test -------->");    
    	IndicatorComputation dummy = new IndicatorComputation();    
    	SocialData socialDataContent;
    	Normalized normalizedStatement;
    	TrafficFromSocialNetwork trafficFromSocialNetwork;
    	Stamp ObservedTime;    	
    	Calendar cal = Calendar.getInstance();    	
    	DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
    	Accident accident;
    	AbnormalTraffic abnormalTrafficStatement;
    	Raw rawStatement;
    	
    	try
    	{    		    	
    		PolicyEncoderCore core = new PolicyEncoderCore();
    		PolicyModel policyModel = core.getPolicyModelFromDB();    			    								
			normalizedStatement = new Normalized();			
			socialDataContent = new SocialData();
			trafficFromSocialNetwork = new TrafficFromSocialNetwork();
			
			Indicators indicators = new Indicators();
			ArrayList<Integer> indicatorIds = new ArrayList<Integer>();
    		for (Indicator indicatorMonitor : policyModel.getIndicators().getIndicator())
        	{	    		  	    	
        		for (IndicatorTemplate indicatorTemplate : policyModel.getIndicatorTemplates().getIndicatorTemplate())
        		{	    		    			
        			if ((indicatorMonitor.getTemplateId().equalsIgnoreCase(indicatorTemplate.getId()) && (indicatorTemplate.getFormula().equalsIgnoreCase("Monitoring Indicator"))))
        			{
        				if (!indicators.getIndicator().contains(indicatorMonitor))
        				{
        					indicators.getIndicator().add(indicatorMonitor);
        					indicatorIds.add(indicatorMonitor.getId());
        				}
        			}
        		}
        	}
			

			try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	socialDataContent.setObservedTime(ObservedTime);    	
	    	socialDataContent.setTrafficStatus(TrafficStatusValueEnum.HEAVY);	    	
	    	trafficFromSocialNetwork.getSocialData().add(socialDataContent);
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	socialDataContent.setObservedTime(ObservedTime);    	
	    	socialDataContent.setTrafficStatus(TrafficStatusValueEnum.HEAVY);	    	
	    	trafficFromSocialNetwork.getSocialData().add(socialDataContent);
	    	
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	socialDataContent = new SocialData();
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	socialDataContent.setObservedTime(ObservedTime); 
	    	socialDataContent.setTrafficStatus(TrafficStatusValueEnum.FREE_FLOW);
	    	trafficFromSocialNetwork.getSocialData().add(socialDataContent);
	    		    	
	    	normalizedStatement.getContent().add(trafficFromSocialNetwork);	    	
	    	dummy.registerStatement((Statement)normalizedStatement,indicators);
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	abnormalTrafficStatement = new AbnormalTraffic();
	    	ObservedTime = new Stamp();	    	
	    	ObservedTime.setTime(cal.getTime().getTime());
	    	abnormalTrafficStatement.setObservedTime(ObservedTime);
	    	abnormalTrafficStatement.setAbnormalTrafficType(AbnormalTrafficTypeEnum.STATIONARY_TRAFFIC);
	    	dummy.registerStatement((Statement)abnormalTrafficStatement,indicators);	
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	abnormalTrafficStatement = new AbnormalTraffic();
	    	ObservedTime = new Stamp();	    	
	    	ObservedTime.setTime(cal.getTime().getTime());
	    	abnormalTrafficStatement.setObservedTime(ObservedTime);
	    	abnormalTrafficStatement.setAbnormalTrafficType(AbnormalTrafficTypeEnum.QUEUEING_TRAFFIC);
	    	dummy.registerStatement((Statement)abnormalTrafficStatement,indicators);	
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	accident = new Accident();
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	accident.setObservedTime(ObservedTime);
	    	PeopleInvolved peopleInvolved = new PeopleInvolved();
	    	peopleInvolved.setNumberOfPeople(1);
	    	accident.setPeopleInvolved(peopleInvolved);
	    	VehiclesInvolved vehiclesInvolved = new VehiclesInvolved();
	    	vehiclesInvolved.setNumberOfVehicles(1);
	    	accident.setVehiclesInvolved(vehiclesInvolved);
	    	dummy.registerStatement((Statement)accident,indicators);	
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	accident = new Accident();
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	accident.setObservedTime(ObservedTime);
	    	peopleInvolved = new PeopleInvolved();
	    	peopleInvolved.setNumberOfPeople(1);
	    	accident.setPeopleInvolved(peopleInvolved);
	    	vehiclesInvolved = new VehiclesInvolved();
	    	vehiclesInvolved.setNumberOfVehicles(0);
	    	accident.setVehiclesInvolved(vehiclesInvolved);
	    	dummy.registerStatement((Statement)accident,indicators);	
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	accident = new Accident();
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	accident.setObservedTime(ObservedTime);
	    	peopleInvolved = new PeopleInvolved();
	    	peopleInvolved.setNumberOfPeople(666);
	    	accident.setPeopleInvolved(peopleInvolved);
	    	vehiclesInvolved = new VehiclesInvolved();
	    	vehiclesInvolved.setNumberOfVehicles(1);
	    	accident.setVehiclesInvolved(vehiclesInvolved);	    	
	    	dummy.registerStatement((Statement)accident,indicators);	
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	accident = new Accident();
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	accident.setObservedTime(ObservedTime);
	    	peopleInvolved = new PeopleInvolved();
	    	peopleInvolved.setNumberOfPeople(1);
	    	accident.setPeopleInvolved(peopleInvolved);
	    	vehiclesInvolved = new VehiclesInvolved();
	    	vehiclesInvolved.setNumberOfVehicles(666);
	    	accident.setVehiclesInvolved(vehiclesInvolved);	    	
	    	dummy.registerStatement((Statement)accident,indicators);	
	    	
	    	try {
				Date date = formatter.parse("26/06/1981 07:07:07");
				cal.setTime(date);
			} catch (ParseException e) {
				logger.info(e.getMessage());
				assert(false);
				return;
			}
	    	accident = new Accident();
	    	ObservedTime = new Stamp();
	    	ObservedTime.setTime(cal.getTime().getTime());    	
	    	accident.setObservedTime(ObservedTime);
	    	peopleInvolved = new PeopleInvolved();
	    	peopleInvolved.setNumberOfPeople(0);
	    	accident.setPeopleInvolved(peopleInvolved);
	    	vehiclesInvolved = new VehiclesInvolved();
	    	vehiclesInvolved.setNumberOfVehicles(1);
	    	accident.setVehiclesInvolved(vehiclesInvolved);	    	
	    	dummy.registerStatement((Statement)accident,indicators);	
	    	
	    	rawStatement = new Raw();
	    	String message = "<validityStartTime>2013-07-02T15:24:00.111+02:00</validityStartTime><trafficStatusValue>heavy</trafficStatusValue>";
	    	rawStatement.setMessage(message);
	    	dummy.registerStatement((Statement)rawStatement,indicators);
	    	
	    	rawStatement = new Raw();
	    	message = "<validityStartTime>2013-07-02T15:24:00.111+02:00</validityStartTime><trafficStatusValue>stationayTraffic</trafficStatusValue>";
	    	rawStatement.setMessage(message);
	    	dummy.registerStatement((Statement)rawStatement,indicators);
	    		    
	    	SampleSeriesSet sampleSeriesSet = dummy.getIndicatorSamples(indicatorIds, new Timestamp(349337338000L),new Timestamp(349337343000L));
                logger.info("<----- Indicator results ------>");
	    	System.out.println(dummy.toXMLString());
                logger.info("<----------->");
                //ArrayList<Integer> successIndicatorIds = new ArrayList<Integer>();
                //successIndicatorIds.add(9);                
                //dummy.getSuccessIndicatorSamples(successIndicatorIds, new Timestamp(349336338000L),new Timestamp(349337343000L));
                
	    	
	    			    	
    	}
    	catch (Exception E)
    	{
    		E.printStackTrace();
    		logger.info(E.getMessage());
    		assert(false);
    	} 
    	
    	logger.info("<---------- Finishing Indicator Computation Test. Statement test -------->");  
    	
    }
    
    public void SimpleTest() {
    	
    	ArrayList<Integer> indicators;    	
    	SampleSeriesSet sampleSeriesSet;
    	
    	
    	logger.info("<---------- Starting Indicator Computation Test. Simple test -------->");
    	IndicatorComputation dummy = new IndicatorComputation();    	
    	
    	logger.info("	· Retrieving indicator information from policy database");
    	try
    	{
    		PolicyEncoderCore core = new PolicyEncoderCore();
    		PolicyModel policyModel = core.getPolicyModelFromDB();
    		  		
    	
	    	for (Indicator indicatorMonitor : policyModel.getIndicators().getIndicator())
	    	{	    		  	    	
                    logger.info(" · Retrieved indicator '" +  indicatorMonitor.getName()+ "'");
	    		for (IndicatorTemplate indicatorTemplate : policyModel.getIndicatorTemplates().getIndicatorTemplate())
	    		{	    		    			
                            logger.info(" · Retrieved indicator template '" +  indicatorTemplate.getName()+ "'");
	    			if ((indicatorMonitor.getTemplateId().equalsIgnoreCase(indicatorTemplate.getId()) && (indicatorTemplate.getFormula().equalsIgnoreCase("Monitoring Indicator"))))
	    			{
	    				Calendar cal = Calendar.getInstance();    	
	    		    	DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
	    		    	try {
	    					Date date = formatter.parse("26/06/1981 07:07:07");
	    					cal.setTime(date);
	    				} catch (ParseException e) {
	    					logger.info(e.getMessage());
	    					assert(false);
	    					return;
	    				}
	    				
	    				logger.info("	 · Starting Indicator Computation Test for indicator '" + indicatorMonitor.getName() + "'");
                                	    	        	//First indicators in 5 seconds
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 2);    	    	   
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 3);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	
	    	        	//Second indicator group
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 5);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	
	    	        	//One hundred second gap and third group
	    	        	cal.add(Calendar.SECOND, 100);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 3);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	
	    	        	//Fourth group
	    	        	cal.add(Calendar.SECOND, 2);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	
	    	        	//Fifth group
	    	        	cal.add(Calendar.SECOND, 10);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        	
	    	        	//Sixth group
	    	        	cal.add(Calendar.SECOND, 1);
	    	        	dummy.registerSample(new Timestamp(cal.getTime().getTime()), indicatorMonitor);
	    	        		        		        		        		        
	    	        	 if (indicatorMonitor.getTimeUnit() == TimeUnit.TU_DAY)
                                 {
                                    indicators = new ArrayList<Integer>();
                                    indicators.add(indicatorMonitor.getId());
                                    indicators.add(777);
                                    indicators.add(888);
                                    sampleSeriesSet = dummy.getIndicatorSamples(indicators, new Timestamp(349337227000L),new Timestamp(349423627000L));
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() + "");
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() == 4);
                                 }       
                                 if (indicatorMonitor.getTimeUnit() == TimeUnit.TU_SECOND)
                                {
                                    indicators = new ArrayList<Integer>();
                                    indicators.add(indicatorMonitor.getId());
                                    indicators.add(777);
                                    indicators.add(888);
                                    sampleSeriesSet = dummy.getIndicatorSamples(indicators, new Timestamp(349337338000L),new Timestamp(349337343000L));
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() + "");
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() == 4);

                                    indicators = new ArrayList<Integer>();
                                    indicators.add(indicatorMonitor.getId());
                                    indicators.add(777);
                                    indicators.add(888);    	    	
                                    sampleSeriesSet = dummy.getIndicatorSamples(indicators, new Timestamp(349337337000L),new Timestamp(349337354999L));
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() + "");
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().get(1).getValue() + "");
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() == 4);
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().get(1).getValue() == 1);

                                    indicators = new ArrayList<Integer>();
                                    indicators.add(indicatorMonitor.getId());
                                    indicators.add(777);
                                    indicators.add(888);    	    	
                                    sampleSeriesSet = dummy.getIndicatorSamples(indicators, new Timestamp(349337232999L),new Timestamp(349337354999L));
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() + "");
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().get(1).getValue() + "");
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().get(2).getValue() + "");
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().get(0).getValue() == 2);
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().get(1).getValue() == 4);
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().get(2).getValue() == 1);

                                    indicators = new ArrayList<Integer>();
                                    indicators.add(indicatorMonitor.getId());
                                    indicators.add(777);
                                    indicators.add(888);    	    	    	
                                    sampleSeriesSet = dummy.getIndicatorSamples(indicators, new Timestamp(349337343000L),new Timestamp(349337338000L));
                                    logger.info(sampleSeriesSet.getSampleSeries().get(0).getSample().size() + "");
                                    //assert(sampleSeriesSet.getSampleSeries().get(0).getSample().size() == 0);

                                    indicators = new ArrayList<Integer>();    	
                                    indicators.add(777);
                                    indicators.add(888);    	    	
                                    sampleSeriesSet = dummy.getIndicatorSamples(indicators, new Timestamp(349337338000L),new Timestamp(349337343000L));
                                    logger.info(sampleSeriesSet.getSampleSeries().size() + "");
                                    //assert(sampleSeriesSet.getSampleSeries().size() == 0);
                                }
	    	        	
	    			}
	    		}
	    		
	    		
	    	}
	    	String result = dummy.toXMLString();
        	logger.info(result);
    	}
    	catch (Exception E)
    	{
    		E.printStackTrace();
    		logger.info(E.getMessage());
    		assert(false);
    	}    	    
    	logger.info("<---------- Finishing Indicator Computation Simple test -------->");
        
    }
    
	
	public static void main(String[] args)
    {
		
		IndicatorComputationTestCase dummy = new IndicatorComputationTestCase();
		dummy.test();
		
    }

}
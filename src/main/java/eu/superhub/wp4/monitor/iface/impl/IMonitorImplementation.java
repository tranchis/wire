package eu.superhub.wp4.monitor.iface.impl;

import eu.superhub.wp3.marshaller.GenericMarshaller;


import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.models.situationaldatamodel.interfaces.StatementType;
import eu.superhub.wp3.models.situationaldatamodel.statements.Normalized;
import eu.superhub.wp3.models.situationaldatamodel.statements.Raw;
import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.Content;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Pollution;

import eu.superhub.wp4.encoder.core.PolicyEncoderCore;
import eu.superhub.wp4.models.mobilitypolicy.Indicator;
import eu.superhub.wp4.models.mobilitypolicy.IndicatorTemplate;
import eu.superhub.wp4.models.mobilitypolicy.Indicators;
import eu.superhub.wp4.models.mobilitypolicy.Policy;
import eu.superhub.wp4.models.mobilitypolicy.PolicyModel;
import eu.superhub.wp4.models.mobilitypolicy.SampleSeriesSet;
import eu.superhub.wp4.models.mobilitypolicy.SuccessIndicator;
import eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue;
import eu.superhub.wp4.monitor.commons.MonitorException;
import eu.superhub.wp4.monitor.iface.IMonitor;
import eu.superhub.wp4.monitor.indicatorcomputation.IndicatorComputation;
import eu.superhub.wp4.monitor.wp3servicedata.ISituationalDataListener;
import eu.superhub.wp4.monitor.wp3servicedata.SituationalDataPusher;
import eu.superhub.wp4.monitor.wp3servicedata.SituationalDataServiceParameters;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import eu.superhub.wp4.monitor.core.SDMClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class IMonitorImplementation implements IMonitor, ISituationalDataListener
{
	
	private static Logger	logger;
	private boolean started = false;	
	private IndicatorComputation indicatorComputation;    
	private Indicators indicators;
	private Vector<SituationalDataPusher> SituationalDataPusherSet = new Vector<SituationalDataPusher>();
        private boolean fip_test =true;
	
        private double[] indicatorCrowd = {78,89,85,79,66,58,81,88,86,87,90,68,55,77,79};
        private double[] successIndicatorCrowd = {100,100,100,100,100,100,100,100,100,100,100,100,100,100,100};
        private double[] indicatorUssagePublicTransport = {35.5,36.6,35.8,35.2,34,33,32,31,30,29,28,26,24,22,20};
        private double[] successIndicatorUssagePublicTransport =  {100,100,50,0,0,0,0,0,0,0,0,0,0,0,0};
        
        private Vector<Float> CarbonDioxideIndexVector = new Vector<Float>();
        private int CarbonDioxideIndexIndicator = 4;
        private int CarbonDioxideIndexSuccessIndicator = 992;
        double[] CarbonDioxideIndexVectorSamples = {0,0.1,0.2,0.3,0.4,0.5,0.5,0.55,0.6,0.61,0.62,0.63,0.64,0.65,0.66};
        
        private Vector<Float> PrivateTrafficVehicleKilometerVector = new Vector<Float>();
        private int PrivateTrafficVehicleKilometerIndicator = 6661;
        private int PrivateTrafficVehicleKilometerSuccessIndicator = 691;
        double[] PrivateTrafficVehicleKilometerVectorSamples = {25,21,23,24,5,10,26,24,21,23,22,3,8,28,26};
        
        private Vector<Float> PrivateTransportModalShareVector = new Vector<Float>();
        private int PrivateTransportModalShareIndicator = 6661;
        private int PrivateTransportModalShareSuccessIndicator = 691;
        double[] PrivateTransportModalShareVectorSamples = {25,21,23,24,5,10,26,24,21,23,22,3,8,28,26};
        
        private Vector<Float> NitrogenOxideIndexVector = new Vector<Float>();
        private int NitrogenOxideIndexIndicator = 6663;
        private int NitrogenOxideIndexSuccessIndicator  = 693;
        double[] NitrogenOxideIndexVectorSamples = {0, 0.01, 0.02, 0.03, 0.03, 0.035, 0.04, 0.04, 0.05, 0.06, 0.08, 0.08, 0.08, 0.10, 0.15};
        
        private Vector<Float> SulfurDioxideIndexVector = new Vector<Float>();
        private int SulfurDioxideIndexIndicator = 6664;
        private int SulfurDioxideIndexSuccessIndicator  = 694;
        double[] SulfurDioxideIndexVectorSamples = {0.10, 0.12, 0.12, 0.13, 0.15, 0.17, 0.17, 0.19, 0.19, 0.19, 0.20, 0.21, 0.22, 0.22, 0.23};
        
        private Vector<Float> AtmosphericParticulatesIndexVector = new Vector<Float>();
        private int AtmosphericParticulatesIndexIndicator  = 6662;
        private int AtmosphericParticulatesIndexSuccessIndicator  = 692;
        double[] AtmosphericParticulatesIndexVectorSamples = {25,21,23,24,5,10,26,24,21,23,22,3,8,28,26};
        
        private Vector<Float> VolatileOrganicCompoundIndexVector = new Vector<Float>();
        private int VolatileOrganicCompoundIndexIndicator = 6665;
        private int VolatileOrganicCompoundIndexSuccessIndicator  = 695;
        double[] VolatileOrganicCompoundIndexVectorSamples = {25,21,23,24,5,10,26,24,21,23,22,3,8,28,26};
        
        private Vector<Float> PublicTransportModalShareVector = new Vector<Float>();
        private int PublicTransportModalShareIndicator = 6666;
        private int PublicTransportModalShareSuccessIndicator  = 696;
        double[] PublicTransportModalShareVectorSamples = {0.30, 0.33, 0.38, 0.31, 0.22, 0.15, 0.31, 0.35, 0.33, 0.31, 0.28, 0.19, 0.17, 0.35, 0.33};
        
        private Vector<Float> PrivateTrafficMillionVector = new Vector<Float>();
        private int PrivateTrafficMillionIndicator = 6667;
        private int PrivateTrafficSuccessMillionIndicator  = 697;
        double[] PrivateTrafficMillionVectorSamples = {23, 26, 23.5, 24.5, 12, 10, 23, 23.5, 26, 24.5, 25, 13, 11, 25, 25.5};
        
        private Vector<Float> MinutesDayOvercrowdingVector = new Vector<Float>();
        private int MinutesDayOvercrowdingIndicator  = 6668;
        private int MinutesDayOvercrowdingSuccessIndicator  = 698;
        double[] MinutesDayOvercrowdingVectorSamples = {170,165,180,115,40,80,175,165,185,160,120,50,30,180,185};
        
        private Vector<Float> TimesDayOvercrowdingVector = new Vector<Float>();
        private int TimesDayOvercrowdingIndicator  = 6669;
        private int TimesDayOvercrowdingSuccessIndicator  = 699;
        double[] TimesDayOvercrowdingVectorSamples = {3,3,3,3,0,1,3,3,3,3,3,0,1,3,3};
        
        private Vector<Float> BikeSharingFullStationVector = new Vector<Float>();
        private int BikeSharingFullStationIndicator  = 6670;
        private int BikeSharingFullStationSuccessIndicator = 670;
        double[] BikeSharingFullStationVectorSamples = {300, 280, 320, 290, 400, 420, 310, 315, 330, 290, 305, 410, 390, 325, 310};

        private Vector<Float> BikeSharingEmptyStationVector = new Vector<Float>();
        private int BikeSharingEmptyStationIndicator  = 6671;
        private int BikeSharingEmptyStationSuccessIndicator = 671;
        double[] BikeSharingEmptyStationVectorSamples = {130, 140, 125, 120, 30, 15, 130, 140, 135, 120, 120, 40, 20, 135, 140};

        private Vector<Float> BikeSharingBikesPerDayVector = new Vector<Float>();
        private int BikeSharingBikesPerDayIndicator = 6672;
        private int BikeSharingBikesPerDaySuccessIndicator = 672;
        double[] BikeSharingBikesPerDayVectorSamples = {105, 100, 90, 95, 50, 35, 110, 90, 95, 85, 100, 50, 40, 105, 95};

        private Vector<Float> BikeSharingModalShareVector = new Vector<Float>();
        private int BikeSharingModalShareIndicator  = 6673;
        private int BikeSharingModalShareSuccessIndicator = 673;
        double[] BikeSharingModalShareVectorSamples = {0.28, 0.31, 0.36, 0.29, 0.20, 0.13, 0.29, 0.33, 0.31, 0.29, 0.26, 0.17, 0.15, 0.33, 0.31};
        
        //private double[] indicatorCO2 = {0,0.1,0.2,0.3,0.4,0.5,0.5,0.55,0.6,0.61,0.62,0.63,0.64,0.65,0.66};
        //private double[] successIndicatorCO2=  {100,100,100,100,100,100,50,50,50,0,0,0,0,0,0};
        
	@Override
	public SampleSeriesSet getIndicatorSamples(int id, Timestamp beginTime,
			Timestamp endTime) throws MonitorException  {
            logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
            logger.info("This is getIndicatorSamples function with parameters: id '" + id + "' beginTime '" + beginTime + "' endTime '" + endTime + "'");
            logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
            //TODO: Add fake indicator samples
		if (!endTime.after(beginTime))
		{
			throw new MonitorException("End time value '" + beginTime.getTime() + "' is not after End time value '" + endTime.getTime() + "'");
		}
		ArrayList<Integer> indicatorIds = new ArrayList<Integer>();
		indicatorIds.add(id);
                if (indicatorComputation.getIndicatorSamples(indicatorIds, beginTime, endTime).getSampleSeries().size() == 0)
                {
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                    logger.info("This is getIndicatorSamples function returning sampleSeries of size '0'");
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                }
                else
                {
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                    logger.info("This is getIndicatorSamples function returning sampleSeries of size '" + indicatorComputation.getIndicatorSamples(indicatorIds, beginTime, endTime).getSampleSeries().get(0).getSample().size() + "'");
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                }
                
		return indicatorComputation.getIndicatorSamples(indicatorIds, beginTime, endTime);
							
	}

	@Override
	public SampleSeriesSet getSuccessIndicatorSamples(int id, Timestamp beginTime,
			Timestamp endTime) throws MonitorException  {
		
		if (!endTime.after(beginTime))
		{
			throw new MonitorException("End time value '" + beginTime.getTime() + "' is not after End time value '" + endTime.getTime() + "'");
		}
                logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                logger.info("This is getSuccessIndicatorSamples function with parameters: id '" + id + "' beginTime '" + beginTime + "' endTime '" + endTime + "'");
                logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
		ArrayList<Integer> successIndicatorIds = new ArrayList<Integer>();
                successIndicatorIds.add(id);
                if (indicatorComputation.getSuccesIndicatorSamples(successIndicatorIds, beginTime, endTime).getSampleSeries().size() == 0)
                {
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                    logger.info("This is getSuccessIndicatorSamples function returning sampleSeries of size '0'");
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                }
                else
                {
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                    logger.info("This is getSuccessIndicatorSamples function returning sampleSeries of size '" + indicatorComputation.getSuccesIndicatorSamples(successIndicatorIds, beginTime, endTime).getSampleSeries().get(0).getSample().size() + "'");
                    logger.info("|<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>||<·>|");
                }
		return indicatorComputation.getSuccesIndicatorSamples(successIndicatorIds, beginTime, endTime);
	}
		
	
	@Override
	public void startMonitor(long SamplingPeriod) throws MonitorException  {
		logger = LoggerFactory.getLogger(getClass());
		if (SamplingPeriod < 1000)
		{
			throw new MonitorException("Invalid sampling period '" + SamplingPeriod + "'. The minimum value is 1000");
		}
		started = true;
		logger.info("Starting monitoring process with  '" + SamplingPeriod + "' millisencods");	
                if (this.fip_test)
                {
                    logger.info("Loading Data from SDM");
                    RandomizeSamples();
                    logger.info("Data from SDM loaded");
                    Calendar cc = Calendar.getInstance();
                    cc.setTime(new java.util.Date());
                    int timeUnit = timeUnit = Calendar.DATE;
                    cc.add(timeUnit, -45);
                    Timestamp beginTime = new Timestamp(cc.getTime().getTime());
                    cc.add(timeUnit, new Long(SamplingPeriod).intValue());
                    Timestamp endTime = new Timestamp(cc.getTime().getTime());
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
                    for (int dummy = 6661; dummy < 6674; dummy++)
                    {
                        indicatorIds.add(dummy);
                    }
                    indicatorIds.add(661);
                    logger = LoggerFactory.getLogger(getClass());
                    indicatorComputation = new IndicatorComputation();
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
                                timeUnit = Calendar.DATE;
                                //logger.info("Pelussitas : " + c.getTime() + "|" + endTime);
                                int count_chocula = 0;
                                while (endTime.after(c.getTime()))
                                {
                                    
                                    //logger.info("Maldades : " + c.getTime());
                                    double value = generateRandomSample(indicatorMonitor.getId(),count_chocula);
                                    Timestamp stamp = new Timestamp(c.getTime().getTime());
                                    indicatorComputation.registerDailySampleValue(stamp, indicatorMonitor,value);
                                    c.add(timeUnit, 1);
                                    count_chocula++;
                                    if (count_chocula == this.CarbonDioxideIndexVector.size()- 1)
                                    {
                                        count_chocula = 0;
                                    }
                                }
                            }
                        }
                        ArrayList<Integer> successIndicatorIds = new ArrayList<Integer> ();
                        successIndicatorIds.add(1);
                        successIndicatorIds.add(2);
                        successIndicatorIds.add(3);
                        successIndicatorIds.add(991);
                        successIndicatorIds.add(992);
                        successIndicatorIds.add(993);
                        for (int dummy = 691; dummy < 674; dummy++)
                        {
                            successIndicatorIds.add(dummy);
                        }
                         for (Policy policy : policyModel.getPolicies().getPolicy())
                         {
                             if ((policy.getId() == 100002) || (policy.getId() == 101) || (policy.getId() == 102) || (policy.getId() == 103))
                             {
                                for (SuccessIndicator successIndicator : policy.getPurpose().getInstantiatedSuccessIndicators().getSuccessIndicator())
                                {
                                    if (successIndicatorIds.contains(successIndicator.getId())) 
                                    {
                                        Calendar c = Calendar.getInstance();
                                        c.setTime(new Date(beginTime.getTime()));
                                        timeUnit = Calendar.DATE;
                                        //logger.info("Pelussitas : " + c.getTime() + "|" + endTime);
                                        int count_chocula = 0;
                                        while (endTime.after(c.getTime()))
                                        {
                                            //logger.info("Maldades : " + c.getTime());
                                            double value = generateRandomSuccessIndicatorSample(successIndicator.getId(), count_chocula);
                                            Timestamp stamp = new Timestamp(c.getTime().getTime());
                                            indicatorComputation.registerDailySuccessIndicatorSampleValue(stamp, successIndicator,value);
                                            c.add(timeUnit, 1);
                                            count_chocula++;
                                            if (count_chocula == this.CarbonDioxideIndexVector.size()- 1)
                                            {
                                                count_chocula = 0;
                                            }
                                        }
                                        
                                    }
                                }
                                logger.info("Computed success indicators");
                             }
                            

                        }
                    }
                    catch (Exception E)
                    {
                        logger.error(E.getMessage());
                        E.printStackTrace();
                    }
                }
                else
                {
                    try
                    {    		    
                            indicatorComputation = new IndicatorComputation();
                            PolicyEncoderCore core = new PolicyEncoderCore();
                            PolicyModel policyModel = core.getPolicyModelFromDB();   
                            SituationalDataPusher	sdp = null;
                                    SituationalDataServiceParameters sdsp = null;

                            int max_id = -1;
                            //Add direct indicators
                            indicators = new Indicators();						
                            for (Indicator indicatorMonitor : policyModel.getIndicators().getIndicator())
                            {	    		  	    	
                                //TODO: Add fake indicators
                                    for (IndicatorTemplate indicatorTemplate : policyModel.getIndicatorTemplates().getIndicatorTemplate())
                                    {	    		    			
                                            if ((indicatorMonitor.getTemplateId().equalsIgnoreCase(indicatorTemplate.getId()) && (indicatorTemplate.getFormula().equalsIgnoreCase("Monitoring Indicator"))))
                                            {
                                                    if (!indicators.getIndicator().contains(indicatorMonitor))
                                                    {


                                                            sdsp = new SituationalDataServiceParameters();
                                                            sdsp.setSamplingPeriod(SamplingPeriod);
                                                            if (indicatorMonitor.getId() > max_id) {
                                                                max_id = indicatorMonitor.getId();
                                                            }
                                                            indicators.getIndicator().add(indicatorMonitor);
                                                            if (indicatorComputation.isTrafficGPSIndicator(indicatorMonitor))
                                                            {
                                                                    sdsp.setServicePredictedTrafficFromCellNetLatitude(BigInteger.valueOf(Long.parseLong(getIndicatorField(indicatorMonitor,"latitude"))));
                                                                    sdsp.setServicePredictedTrafficFromCellNetLongitude(BigInteger.valueOf(Long.parseLong(getIndicatorField(indicatorMonitor,"longitude"))));
                                                                    sdsp.setTrafficDataParameter(true);
                                                                    sdsp.setSituationalDataParameter(false);
                                                            }
                                                            else
                                                            {
                                                                    sdsp.setServiceTrafficSocialNetworkCityName(getIndicatorField(indicatorMonitor,"city"));        						
                                                                    sdsp.setServiceTrafficSocialNetworkStateAbbr(getIndicatorField(indicatorMonitor,"country"));        						
                                                                    sdsp.setServiceTrafficSocialNetworkStateName(getIndicatorField(indicatorMonitor,"statename"));        						
                                                                    sdsp.setServiceTrafficSocialNetworkLatitude(Long.parseLong(getIndicatorField(indicatorMonitor,"latitude")));
                                                                    sdsp.setServiceTrafficSocialNetworkLongitude(Long.parseLong(getIndicatorField(indicatorMonitor,"longitude")));
            //        						sdsp.setServiceTrafficSocialNetworkCityName("Barcelona");
            //        						sdsp.setServiceTrafficSocialNetworkStateAbbr("ES");
            //        						sdsp.setServiceTrafficSocialNetworkStateName("Spain");
            //        						System.out.println(" -> " + getIndicatorField(indicatorMonitor,"city"));
            //        						System.out.println(" -> " + getIndicatorField(indicatorMonitor,"country"));
            //        						System.out.println(" -> " + getIndicatorField(indicatorMonitor,"statename"));
            //        						System.out.println(" -> " + getIndicatorField(indicatorMonitor,"latitude"));
            //        						System.out.println(" -> " + getIndicatorField(indicatorMonitor,"longitude"));
                                                                    sdsp.setTrafficDataParameter(false);
                                                                    sdsp.setSituationalDataParameter(true);
                                                                    if (indicatorComputation.isTrafficSocialDataIndicator(indicatorMonitor))
                                                            {        						
                                                                    sdsp.setServiceStatementType(StatementType.TRAFFIC_FROM_SOCIAL_NETWORK.value());            						
                                                            }
                                                            if (indicatorComputation.isAbnormalTrafficIndicator(indicatorMonitor))
                                                            {
                                                                    sdsp.setServiceStatementType(StatementType.ABNORMAL_TRAFFIC.value());
                                                            }
                                                            if (indicatorComputation.isAccidentIndicator(indicatorMonitor))
                                                            {
                                                                    sdsp.setServiceStatementType(StatementType.ACCIDENT.value());
                                                            }
                                                            }        					

                                                            sdp = new SituationalDataPusher(this,sdsp);
                                                            SituationalDataPusherSet.add(sdp);        					
                                                    }
                                            }
                                    }
                            }



                    }
                    catch (Exception E)
                    {    		
                            logger.info(E.getMessage());
                            throw new MonitorException(E.getMessage());
                    } 
                            }
		
	}
        
        private void RandomizeSamples()
        {
            /*
            int sign = 1;
            double value;
            int number;
            Random generator = new Random();
            int count_chocula = 0;
            for (double element : this.successIndicatorCrowd)
            {
                
                number = generator.nextInt(2);
                if (number==0)
                {
                    sign = 1;
                }
                if (number==1)
                {
                    sign = -1;
                }
                number = generator.nextInt(5);
                value = number;
                value = value * sign;
                if (value < 0) value = value * -1;
                this.indicatorCrowd[count_chocula] = this.indicatorCrowd[count_chocula] + value;   
                value = value / 10;
                this.successIndicatorCrowd[count_chocula] = this.successIndicatorCrowd[count_chocula] + value / 5;   
                if (this.indicatorCrowd[count_chocula] < 0) this.indicatorCrowd[count_chocula] = 0;
                if (this.successIndicatorCrowd[count_chocula] < 0) this.successIndicatorCrowd[count_chocula] = 0;
                if (this.successIndicatorCrowd[count_chocula] > 100) this.successIndicatorCrowd[count_chocula] = 100;
                
                this.indicatorCrowd[count_chocula] = (double)Math.round(this.indicatorCrowd[count_chocula] * 10000) / 10000;
                this.successIndicatorCrowd[count_chocula] = (double)Math.round(this.successIndicatorCrowd[count_chocula] * 100) / 100;
                
                count_chocula++;
            }
            count_chocula = 0;
            for (double element : this.successIndicatorCO2)
            {
                
                number = generator.nextInt(2);
                if (number==0)
                {
                    sign = 1;
                }
                if (number==1)
                {
                    sign = -1;
                }
                number = generator.nextInt(5);
                value = number;
                value = value * sign / 1000;
                if (value < 0) value = value * -1;
                this.indicatorCO2[count_chocula] = this.indicatorCO2[count_chocula] + value;   
                this.successIndicatorCO2[count_chocula] = this.successIndicatorCO2[count_chocula] + value / 5;   
                if (this.indicatorCO2[count_chocula] < 0) this.indicatorCO2[count_chocula] = 0;
                if (this.successIndicatorCO2[count_chocula] < 0) this.successIndicatorCO2[count_chocula] = 0;
                if (this.successIndicatorCO2[count_chocula] > 100) this.successIndicatorCO2[count_chocula] = 100;
                
                this.indicatorCO2[count_chocula] = (double)Math.round(this.indicatorCO2[count_chocula] * 10000) / 10000;
                this.successIndicatorCO2[count_chocula] = (double)Math.round(this.successIndicatorCO2[count_chocula] * 100) / 100;
                
                count_chocula++;
            }
            count_chocula = 0;
            for (double element : this.successIndicatorUssagePublicTransport)
            {
                
                number = generator.nextInt(2);
                if (number==0)
                {
                    sign = 1;
                }
                if (number==1)
                {
                    sign = -1;
                }
                number = generator.nextInt(5);
                value = number;
                value = value * sign;
                this.indicatorUssagePublicTransport[count_chocula] = this.indicatorUssagePublicTransport[count_chocula] + value;                  
                value = value / 10;
                this.successIndicatorUssagePublicTransport[count_chocula] = this.successIndicatorUssagePublicTransport[count_chocula] + value / 5;   
                if (this.indicatorUssagePublicTransport[count_chocula] < 0) this.indicatorUssagePublicTransport[count_chocula] = 0;
                if (this.successIndicatorUssagePublicTransport[count_chocula] < 0) this.successIndicatorUssagePublicTransport[count_chocula] = 0;
                if (this.successIndicatorUssagePublicTransport[count_chocula] > 100) this.successIndicatorUssagePublicTransport[count_chocula] = 100;
                
                this.indicatorUssagePublicTransport[count_chocula] = (double)Math.round(this.indicatorUssagePublicTransport[count_chocula] * 10000) / 10000;
                this.successIndicatorUssagePublicTransport[count_chocula] = (double)Math.round(this.successIndicatorUssagePublicTransport[count_chocula] * 100) / 100;
                
                count_chocula++;
            }
            */
            
            //Gather samples from the Situational Data Model provider
            String		response = "", q = "";
            SDMClient sc = new SDMClient("http://atalaya-barcelona.mooo.com:3000/sdm");

            this.CarbonDioxideIndexVector = new Vector<Float> ();
            this.AtmosphericParticulatesIndexVector = new Vector<Float> ();
            this.NitrogenOxideIndexVector = new Vector<Float> ();
            this.SulfurDioxideIndexVector = new Vector<Float> ();
            this.VolatileOrganicCompoundIndexVector = new Vector<Float> ();
            
                    
            q = "{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.statements.Normalized\",\"reliabilityScore\":0.0,\"Content\":[{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Pollution\",\"pollutionActiveArea\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.location.Area\",\"ratio\":5000.0,\"PointByCoordinates\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.location.PointByCoordinates\",\"latitudeE6\":41600000,\"longitudeE6\":2000000}},\"validPeriod\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.time.Interval\",\"end_time\":20000000,\"init_time\":15000000},\"Indexes\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Indexes\",\"airQualityUndex\":0.0,\"atmosphericParticulatesIndex\":0.0,\"carbonDioxideIndex\":0.5,\"nitrogenOxideIndex\":0.0,\"sulfurDioxideIndex\":0.0,\"volatileOrganicCompoundIndex\":0.0},\"localReliability\":0.0}]}";
            response = sc.query(q);
            String responseqr = "";
            GenericMarshaller mrsh = new GenericMarshaller(Normalized.class);
                try {
                    Normalized responseStatement = (Normalized) mrsh.jsonToJava(response);
                    for (Content content : responseStatement.getContent() )
                    {
                        Pollution responsePollution  = (Pollution) content;

                        CarbonDioxideIndexVector.add(responsePollution.getIndexes().getCarbonDioxideIndex());
                        AtmosphericParticulatesIndexVector.add(responsePollution.getIndexes().getAtmosphericParticulatesIndex());
                        NitrogenOxideIndexVector.add(responsePollution.getIndexes().getNitrogenOxideIndex());
                        SulfurDioxideIndexVector.add(responsePollution.getIndexes().getSulfurDioxideIndex());
                        VolatileOrganicCompoundIndexVector.add(responsePollution.getIndexes().getVolatileOrganicCompoundIndex());
                        
                    }
                    logger.info("Obtained aggregated data " +response);
                    Raw dummy = new Raw();
                    dummy.setLocalReliabilityScore(0);
                    dummy.setMessage("Pelusso Maldades");
                    dummy.setUuid(java.util.UUID.randomUUID().toString());
                    mrsh = new GenericMarshaller(Raw.class);
                    String qr;
                    qr = mrsh.javaToJson(dummy);
                    
                    responseqr = sc.query(qr);
                    logger.info("Obtained aggregated data " +responseqr);
                    Raw rawResult = (Raw)mrsh.jsonToJava(responseqr);
                    String rawmessage =  rawResult.getMessage();
                    Object obj=JSONValue.parse(rawmessage);
                    JSONArray array=(JSONArray)obj;
                    double value;
                    Long lvalue;
                    String key;
                    float fvalue;
                    for (int i = 0; i < array.size(); i++)
                    {
                        JSONObject obj2=(JSONObject)array.get(i);
                        //System.out.println(obj2);

                        key = "publicTransportModalShare";
                        value = (Double)obj2.get(key);
                        fvalue = (float) value;
                        this.PublicTransportModalShareVector.add(fvalue);

                        key = "timesDayOvercrowding";
                        lvalue = (Long)obj2.get(key);
                        fvalue = (float) lvalue;
                        this.TimesDayOvercrowdingVector.add(fvalue);

                        key = "minutesDayOvercrowding";
                        value = (Double)obj2.get(key);
                        fvalue = (float) value;
                        this.MinutesDayOvercrowdingVector.add(fvalue);

                        key = "fullStations";
                        lvalue = (Long)obj2.get(key);
                        fvalue = (float) lvalue;
                        this.BikeSharingFullStationVector.add(fvalue);

                        key = "emptyStations";
                        lvalue = (Long)obj2.get(key);
                        fvalue = (float) lvalue;
                        this.BikeSharingEmptyStationVector.add(fvalue);

                        key = "bikesPerDay";
                        lvalue = (Long)obj2.get(key);
                        fvalue = (float) value;
                        this.BikeSharingBikesPerDayVector.add(fvalue);

                        key = "bikeModalShare";
                        value = (Double)obj2.get(key);
                        fvalue = (float) value;
                        this.BikeSharingModalShareVector.add(fvalue);

                        key = "privateTrafficVehicleKilometer";
                        value = (Double)obj2.get(key);
                        fvalue = (float) value;
                        this.PrivateTrafficVehicleKilometerVector.add(fvalue);
                        
                        key = "privateTransportModalShare";
                        value = (Double)obj2.get(key);
                        fvalue = (float) value;
                        this.PrivateTrafficVehicleKilometerVector.add(fvalue);

                    }
                                      
                   
                   
                    
                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                }
           
           
                
           /*
           for (double element : this.CarbonDioxideIndexVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               felement = felement + variation;
               
               this.CarbonDioxideIndexVector.add(felement);
           }
           
           for (double element : this.PrivateTrafficVehicleKilometerVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.PrivateTrafficVehicleKilometerVector.add(felement);
           }
           
           for (double element : this.NitrogenOxideIndexVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.NitrogenOxideIndexVector.add(felement);
           }
           
           for (double element : this.SulfurDioxideIndexVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.SulfurDioxideIndexVector.add(felement);
           }
           
           for (double element : this.AtmosphericParticulatesIndexVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.AtmosphericParticulatesIndexVector.add(felement);
           }
           for (double element : this.VolatileOrganicCompoundIndexVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.VolatileOrganicCompoundIndexVector.add(felement);
           }
            
           for (double element : this.PublicTransportModalShareVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.PublicTransportModalShareVector.add(felement);
           }
           for (double element : this.PrivateTrafficMillionVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.PrivateTrafficMillionVector.add(felement);
           }
           for (double element : this.MinutesDayOvercrowdingVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.MinutesDayOvercrowdingVector.add(felement);
           }
           for (double element : this.TimesDayOvercrowdingVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.TimesDayOvercrowdingVector.add(felement);
           }
           for (double element : this.BikeSharingFullStationVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.BikeSharingFullStationVector.add(felement);
           }
           for (double element : this.BikeSharingEmptyStationVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.BikeSharingEmptyStationVector.add(felement);
           }
           for (double element : this.BikeSharingBikesPerDayVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.BikeSharingBikesPerDayVector.add(felement);
           }
           for (double element : this.BikeSharingModalShareVectorSamples)
           {
               float felement = (float) element;
               float variation = (float)(felement * 0.01);
               int magnitude = 0 + (int)(Math.random() * ((10 - 0) + 1));
               int sign = 0 + (int)(Math.random() * ((1 - 0) + 1));
               if (sign == 0) sign = -1;
               variation = (magnitude * variation) * sign;
               this.BikeSharingModalShareVector.add(felement);
           }
           */
            
        }
        
        private double generateRandomSample(Integer indicatorID, int count)
        {
            double result = 666;
            
            if (indicatorID == this.CarbonDioxideIndexIndicator)
            {
                result = this.CarbonDioxideIndexVector.get(count);
            }
            if (indicatorID == this.PrivateTrafficVehicleKilometerIndicator)
            {
                result = this.PrivateTrafficVehicleKilometerVector.get(count);
            }
            
            if (indicatorID == this.AtmosphericParticulatesIndexIndicator)
            {
                result = this.AtmosphericParticulatesIndexVector.get(count);
            }
            
            if (indicatorID == this.NitrogenOxideIndexIndicator)
            {
                result = this.NitrogenOxideIndexVector.get(count);
            }
            
            if (indicatorID == this.SulfurDioxideIndexIndicator)
            {
                result = this.SulfurDioxideIndexVector.get(count);
            }
            
            if (indicatorID == this.VolatileOrganicCompoundIndexIndicator)
            {
                result = this.VolatileOrganicCompoundIndexVector.get(count);
            }
            
            if (indicatorID == this.PublicTransportModalShareIndicator)
            {
                result = this.PublicTransportModalShareVector.get(count);
            }
            
            if (indicatorID == this.PrivateTrafficMillionIndicator)
            {
                result = this.PrivateTrafficMillionVector.get(count);
            }
            
            if (indicatorID == this.MinutesDayOvercrowdingIndicator)
            {
                result = this.MinutesDayOvercrowdingVector.get(count);
            }
            
            if (indicatorID == this.TimesDayOvercrowdingIndicator)
            {
                result = this.TimesDayOvercrowdingVector.get(count);
            }
            
            if (indicatorID == this.BikeSharingFullStationIndicator)
            {
                result = this.BikeSharingFullStationVector.get(count);
            }
            
            if (indicatorID == this.BikeSharingEmptyStationIndicator)
            {
                result = this.BikeSharingEmptyStationVector.get(count);
            }
            
            if (indicatorID == this.BikeSharingBikesPerDayIndicator)
            {
                result = this.BikeSharingBikesPerDayVector.get(count);
            }

            if (indicatorID == this.BikeSharingModalShareIndicator)
            {
                result = this.BikeSharingModalShareVector.get(count);
            }
            
            if (indicatorID == this.PrivateTransportModalShareIndicator)
            {
                result = this.PrivateTransportModalShareVector.get(count);
            }
            
            
            return result;
        }
        
         private double generateRandomSuccessIndicatorSample(Integer successIndicatorID, int count)
        {
            double result = 69;
            /*
            if (successIndicatorID == 1 || successIndicatorID == 991)
            {
                logger.info("Gathering data for success indicator 'Increase in usage of public transport'");
                result = this.successIndicatorUssagePublicTransport[count];
            }
            if (successIndicatorID == 2 || successIndicatorID == 993)
            {
                logger.info("Gathering data for success indicator 'Reduction in overcrowdings'");
                result = this.successIndicatorCrowd[count];
            }
            */
            if (successIndicatorID == this.CarbonDioxideIndexSuccessIndicator)
            {
                float value = this.CarbonDioxideIndexVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.5");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 200 - (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;
                
            }
            
            if (successIndicatorID == this.PrivateTrafficVehicleKilometerSuccessIndicator)
            {
                float value = this.PrivateTrafficVehicleKilometerVector.get(count);
                float MAX_VALUE = Float.parseFloat("24");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 100 - (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.NitrogenOxideIndexSuccessIndicator)
            {
                float value = this.NitrogenOxideIndexVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.08");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 200 - (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.SulfurDioxideIndexSuccessIndicator)
            {

                float value = this.SulfurDioxideIndexVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.16");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 200 - (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.AtmosphericParticulatesIndexSuccessIndicator)
            {
                float value = this.AtmosphericParticulatesIndexVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.005");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 200 - (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            
            
            if (successIndicatorID == this.VolatileOrganicCompoundIndexSuccessIndicator)
            {
                float value = this.VolatileOrganicCompoundIndexVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.005");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 200 - (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.PublicTransportModalShareSuccessIndicator)
            {
                float value = this.PublicTransportModalShareVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.35");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            
            if (successIndicatorID == this.PrivateTrafficSuccessMillionIndicator)
            {
                float value = this.PrivateTrafficMillionVector.get(count);
                float MAX_VALUE = Float.parseFloat("15000000 ");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 200 - (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.MinutesDayOvercrowdingSuccessIndicator)
            {
                float value = this.MinutesDayOvercrowdingVector.get(count);
                float MAX_VALUE = Float.parseFloat("180");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 100 - (value*50/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.TimesDayOvercrowdingSuccessIndicator)
            {
                float value = this.TimesDayOvercrowdingVector.get(count);
                float MAX_VALUE = Float.parseFloat("3");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 100 - (value*50/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.BikeSharingFullStationSuccessIndicator)
            {
                float value = this.BikeSharingFullStationVector.get(count);
                float MAX_VALUE = Float.parseFloat("720");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 100 - (value*50/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.BikeSharingEmptyStationSuccessIndicator)
            {
                float value = this.BikeSharingEmptyStationVector.get(count);
                float MAX_VALUE = Float.parseFloat("720");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= 100 - (value*50/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.BikeSharingBikesPerDaySuccessIndicator)
            {
                float value = this.BikeSharingBikesPerDayVector.get(count);
                float MAX_VALUE = Float.parseFloat("140");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            if (successIndicatorID == this.BikeSharingModalShareSuccessIndicator)
            {
                float value = this.BikeSharingModalShareVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.3");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            if (successIndicatorID == this.PrivateTransportModalShareSuccessIndicator)
            {
                float value = this.PrivateTransportModalShareVector.get(count);
                float MAX_VALUE = Float.parseFloat("0.3");
                if (value <= MAX_VALUE) result = 100;
                if(value > MAX_VALUE) result= (value*100/MAX_VALUE);
                if (result > 100) result = 100;
                if (result < 100) result = 100;

            }
            
            return result;
        }
                
        
	@Override
	public void stopMonitor() throws MonitorException {
		if (!started)
		{
			throw new MonitorException("Monitor has not been started yet");
		}		
                if (this.fip_test)
                {
                    logger.info("Stoping monitoring process '");
                }
                else
                {
                    logger.info("Stoping monitoring process '" + SituationalDataPusherSet.size() +  "'");
                    started = false;
                    for (SituationalDataPusher sdp : SituationalDataPusherSet)
                    {
                            sdp.terminate();			
                    }
                }
		
		
	}

	@Override
	public void push(Statement s) {
		
		indicatorComputation.registerStatement(s, indicators);
	}
	
	private String getIndicatorField(Indicator indicator, String fieldName) throws MonitorException
	{
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			if (templateParameterValue.getFieldid().equalsIgnoreCase(fieldName))
			{
				return templateParameterValue.getValue();
			}
		}		
		throw new MonitorException("Field with name '" + fieldName + "' not found among parameter value for indicator with id '" + indicator.getId() +"'");
	}
	
}

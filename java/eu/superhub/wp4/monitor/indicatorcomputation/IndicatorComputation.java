/*******************************************************************************
* Copyright (c) 2013 SUPERHUB - SUstainable and PERsuasive Human Users moBility                 www.superhub-project.eu/
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* 	  Ignasi Gomez: Developing code to compute values of indicators from data obtained from the services
******************************************************************************/

package eu.superhub.wp4.monitor.indicatorcomputation;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.marshaller.GenericMarshaller;
import eu.superhub.wp3.models.situationaldatamodel.statements.Normalized;
import eu.superhub.wp3.models.situationaldatamodel.statements.Raw;
import eu.superhub.wp3.models.situationaldatamodel.statements.Statement;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.Content;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.traffic.SocialData;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.traffic.TrafficFromSocialNetwork;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.AbnormalTraffic;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.Accident;
import eu.superhub.wp3.models.situationaldatamodel.time.Stamp;
import eu.superhub.wp4.encoder.core.PolicyEncoderCore;
import eu.superhub.wp4.models.mobilitypolicy.ElementKind;
import eu.superhub.wp4.models.mobilitypolicy.FormulaDependency;
import eu.superhub.wp4.models.mobilitypolicy.Indicator;
import eu.superhub.wp4.models.mobilitypolicy.IndicatorTemplate;
import eu.superhub.wp4.models.mobilitypolicy.Indicators;
import eu.superhub.wp4.models.mobilitypolicy.Policy;
import eu.superhub.wp4.models.mobilitypolicy.PolicyModel;
import eu.superhub.wp4.models.mobilitypolicy.Sample;
import eu.superhub.wp4.models.mobilitypolicy.SampleSeries;
import eu.superhub.wp4.models.mobilitypolicy.SampleSeriesElementId;
import eu.superhub.wp4.models.mobilitypolicy.SampleSeriesParameterValues;
import eu.superhub.wp4.models.mobilitypolicy.SampleSeriesSet;
import eu.superhub.wp4.models.mobilitypolicy.SuccessIndicator;
import eu.superhub.wp4.models.mobilitypolicy.SuccessIndicatorId;
import eu.superhub.wp4.models.mobilitypolicy.SuccessIndicatorTemplate;
import eu.superhub.wp4.models.mobilitypolicy.TemplateParameterValue;
import eu.superhub.wp4.models.mobilitypolicy.TimeUnit;
import eu.superhub.wp4.monitor.commons.MonitorException;


public class IndicatorComputation {
	
	private SampleSeriesSet IndicatorSampleSeriesSet;	
        private SampleSeriesSet SuccessIndicatorSampleSeriesSet;
	private Logger	logger;
	public IndicatorComputation(){
		IndicatorSampleSeriesSet = new SampleSeriesSet();
                SuccessIndicatorSampleSeriesSet = new SampleSeriesSet();
		logger = LoggerFactory.getLogger(getClass());
	}
		
	public void registerStatement(Statement s, Indicators indicators){
		for (Indicator indicator : indicators.getIndicator())
		{			
			if (isTrafficSocialDataStatement(s)  && isTrafficSocialDataIndicator(indicator))
			{
				Normalized normalizedStatement = (Normalized)s;
				for (Content content : normalizedStatement.getContent())
				{
					TrafficFromSocialNetwork trafficFromSocialNetwork = (TrafficFromSocialNetwork) content;				
					for (SocialData socialData : trafficFromSocialNetwork.getSocialData())
					{
						if (socialData.getTrafficStatus().value().equalsIgnoreCase(getIndicatorStatus(indicator)))
						{
							Stamp t = (Stamp)socialData.getObservedTime();							
							registerSample(new Timestamp(t.getTime()),indicator);							
						}
					}
				}
    				
			}					
			if (isAccidentStatement(s) && isAccidentIndicator(indicator))
			{
				int expected_people, expected_vehicle, actual_people, actual_vehicle;
				actual_people = ((Accident)s).getPeopleInvolved().getNumberOfPeople();
				actual_vehicle = ((Accident)s).getVehiclesInvolved().getNumberOfVehicles();
				expected_people = getIndicatorPeopleInvolved(indicator);
				expected_vehicle = getIndicatorVehiclesInvolved(indicator);
				if ( (actual_people >= expected_people) && (actual_vehicle >= expected_vehicle)) 
				{
					
					Stamp t = (Stamp)((Accident)s).getObservedTime();	
					registerSample(new Timestamp(t.getTime()),indicator);				
				}				
			}
			if (isAbnormalTrafficStatement(s) && isAbnormalTrafficIndicator(indicator))
			{											
					AbnormalTraffic abnormalTrafficStament = (AbnormalTraffic)s;
					Stamp t = (Stamp)abnormalTrafficStament.getObservedTime();
					String actual_status = abnormalTrafficStament.getAbnormalTrafficType().value();
					String expected_status = getIndicatorStatus(indicator);
					//System.out.println(actual_status + "|" + expected_status);
					if (expected_status.equalsIgnoreCase(actual_status))
					{
						registerSample(new Timestamp(t.getTime()),indicator);
					}
								
			}
			if (isTrafficGPSStatement(s) && isTrafficGPSIndicator(indicator))
			{
				String expected_status = getIndicatorStatus(indicator);
				String message = ((Raw)s).getMessage();
				String actual_status = message.substring(message.indexOf("<trafficStatusValue>") + "<trafficStatusValue>".length(), message.indexOf("</trafficStatusValue>"));
				String time = message.substring(message.indexOf("<validityStartTime>") + "<validityStartTime>".length(), message.indexOf("</validityStartTime>"));
				String mask = "yyyy-mm-ddThh:mm:ssZ";
				DateTimeFormatter parser = ISODateTimeFormat.dateTime();
				DateTime dateStr;		      
				dateStr = parser.parseDateTime(time);						        		       
				if (expected_status.equalsIgnoreCase(actual_status))
				{
					registerSample(new Timestamp(dateStr.getMillis()),indicator);
				}
				//System.out.println(actual_status + "" + time);
				
				
			}
		}
    			
		
	}
	private boolean isTrafficGPSStatement(Statement s)
	{
		return s.getClass().getCanonicalName().equalsIgnoreCase(Raw.class.getCanonicalName());
	} 
	private boolean isTrafficSocialDataStatement(Statement s)
	{
		return s.getClass().getCanonicalName().equalsIgnoreCase(Normalized.class.getCanonicalName());
	}
	private boolean isAccidentStatement(Statement s)
	{
		return s.getClass().getCanonicalName().equalsIgnoreCase(Accident.class.getCanonicalName());
	}
	private boolean isAbnormalTrafficStatement(Statement s)
	{		
		return s.getClass().getCanonicalName().equalsIgnoreCase(AbnormalTraffic.class.getCanonicalName());
	}
	
	public boolean isTrafficGPSIndicator(Indicator indicator)
	{
		ArrayList<String> expectedValues = new ArrayList<String>();
		expectedValues.add("latitude");
		expectedValues.add("longitude");
		expectedValues.add("status");
		//expectedValues.add("pelusso");
		
		ArrayList<String> foundValues = new ArrayList<String>();
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			foundValues.add(templateParameterValue.getFieldid());
			
			//foundValues.add("Pelusso");
		}		
		return foundValues.containsAll(expectedValues) && expectedValues.containsAll(foundValues);
	}
	
	public boolean isAccidentIndicator(Indicator indicator)
	{
		ArrayList<String> expectedValues = new ArrayList<String>();
		expectedValues.add("statename");
		expectedValues.add("country");
		expectedValues.add("latitude");
		expectedValues.add("longitude");
		expectedValues.add("PeopleInvolved");
		expectedValues.add("VehiclesInvolved");
		expectedValues.add("city");
		//expectedValues.add("pelusso");
		
		ArrayList<String> foundValues = new ArrayList<String>();
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			foundValues.add(templateParameterValue.getFieldid());
			
			//foundValues.add("Pelusso");
		}		
		return foundValues.containsAll(expectedValues) && expectedValues.containsAll(foundValues);
	}
	
	private int getIndicatorPeopleInvolved(Indicator indicator)
	{
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			if (templateParameterValue.getFieldid().equalsIgnoreCase("PeopleInvolved"))
			{
				return Integer.parseInt(templateParameterValue.getValue());
			}
		}		
		return Integer.MAX_VALUE;
	}
	
	private int getIndicatorVehiclesInvolved(Indicator indicator)
	{
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			if (templateParameterValue.getFieldid().equalsIgnoreCase("VehiclesInvolved"))
			{
				return Integer.parseInt(templateParameterValue.getValue());
			}
		}		
		return Integer.MAX_VALUE;
	}
	
	public boolean isTrafficSocialDataIndicator(Indicator indicator)
	{
		ArrayList<String> expectedValues = new ArrayList<String>();
		expectedValues.add("statename");
		expectedValues.add("country");
		expectedValues.add("latitude");
		expectedValues.add("longitude");
		expectedValues.add("status");
		expectedValues.add("city");
		//expectedValues.add("pelusso");
		ArrayList<String> expectedStatus = new ArrayList<String>();
		expectedStatus.add("impossible");
		expectedStatus.add("congested");
		expectedStatus.add("heavy");
		expectedStatus.add("freeFlow");
		expectedStatus.add("unknown");		
		ArrayList<String> foundValues = new ArrayList<String>();
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			foundValues.add(templateParameterValue.getFieldid());
			if (templateParameterValue.getFieldid().equalsIgnoreCase("status"))
			{
				if (!expectedStatus.contains(templateParameterValue.getValue()))
						{
							return false;
						}
			}
			//foundValues.add("Pelusso");
		}		
		return foundValues.containsAll(expectedValues) && expectedValues.containsAll(foundValues);
	}
	
	public boolean isAbnormalTrafficIndicator(Indicator indicator)
	{
		ArrayList<String> expectedValues = new ArrayList<String>();
		expectedValues.add("statename");
		expectedValues.add("country");
		expectedValues.add("latitude");
		expectedValues.add("longitude");
		expectedValues.add("status");
		expectedValues.add("city");
		//expectedValues.add("pelusso");
		ArrayList<String> expectedStatus = new ArrayList<String>();
		expectedStatus.add("queueingTraffic");
		expectedStatus.add("stopAndGo");
		expectedStatus.add("slowTraffic");
		expectedStatus.add("stationaryTraffic");		
		ArrayList<String> foundValues = new ArrayList<String>();
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			foundValues.add(templateParameterValue.getFieldid());
			if (templateParameterValue.getFieldid().equalsIgnoreCase("status"))
			{
				if (!expectedStatus.contains(templateParameterValue.getValue()))
						{
							return false;
						}
			}
			//foundValues.add("Pelusso");
		}		
		return foundValues.containsAll(expectedValues) && expectedValues.containsAll(foundValues);
	}
	
	private String getIndicatorStatus(Indicator indicator)
	{
		
		for (TemplateParameterValue templateParameterValue : indicator.getParameterValue())
		{
			if (templateParameterValue.getFieldid().equalsIgnoreCase("status"))
			{
				return templateParameterValue.getValue();
			}
		}		
		return "Not found";
	}
        
        public void registerSample(Timestamp t, Indicator indicator){
		boolean indicatorFound = false;
		boolean sampleFound = false;
		int count_chocula = 0;
		for (SampleSeries sampleSeries : IndicatorSampleSeriesSet.getSampleSeries())
		{
			if (sampleSeries.getName().equalsIgnoreCase(indicator.getName())  && sampleSeries.getContext().equalsIgnoreCase("Monitor"))
			{
				indicatorFound = true;
				int sub_count_chocula = 0;
				for (Sample sample : sampleSeries.getSample())
				{								
					Timestamp samplet = new Timestamp (sample.getEndtime());							
					if (samplet.compareTo(t) >= 0)
					{					
						sampleFound = true;
						sample.setValue(sample.getValue()+1);								
						sampleSeries.getSample().set(sub_count_chocula, sample);
					}
					sub_count_chocula ++;					
				}
				if (!sampleFound)
				{
					Sample sample = generateSample(t, indicator);					
					sampleSeries.getSample().add(sample);
				}
				IndicatorSampleSeriesSet.getSampleSeries().set(count_chocula, sampleSeries);
			}
			count_chocula++;
			
		}
		if (!indicatorFound)
		{
			SampleSeries sampleSeries = new SampleSeries();
			sampleSeries.setContext("Monitor");
			SampleSeriesElementId sampleSeriesElementId = new SampleSeriesElementId();
			sampleSeriesElementId.setIndicatorId(indicator.getId());			
			sampleSeries.setElementId(sampleSeriesElementId);
			sampleSeries.setElementKind(ElementKind.EK_INDICATOR);
			sampleSeries.setId(IndicatorSampleSeriesSet.getSampleSeries().size() + 1);
			sampleSeries.setName(indicator.getName());			
			SampleSeriesParameterValues sampleSeriesParameterValues = new SampleSeriesParameterValues();
			sampleSeriesParameterValues.getParameterValue().addAll(indicator.getParameterValue());
			sampleSeries.setParameterValues(sampleSeriesParameterValues);	
			Sample sample = generateSample(t, indicator);					
			sampleSeries.getSample().add(sample);
			IndicatorSampleSeriesSet.getSampleSeries().add(sampleSeries);
		}
	}
	
        public void registerDailySuccessIndicatorSampleValue(Timestamp t, SuccessIndicator successIndicator, double value)
        {
            boolean successIndicatorFound = false;
		int count_chocula = 0;
		for (SampleSeries sampleSeries : SuccessIndicatorSampleSeriesSet.getSampleSeries())
		{
			if (sampleSeries.getName().equalsIgnoreCase(successIndicator.getName())  && sampleSeries.getContext().equalsIgnoreCase("Monitor"))
			{
				
				successIndicatorFound = true;
				
				Sample sample = generateDailySampleValue(t, value);					
				sampleSeries.getSample().add(sample);
				SuccessIndicatorSampleSeriesSet.getSampleSeries().set(count_chocula, sampleSeries);
			}
			count_chocula++;
			
		}
		if (!successIndicatorFound)
		{
			SampleSeries sampleSeries = new SampleSeries();
			sampleSeries.setContext("Monitor");
			SampleSeriesElementId sampleSeriesElementId = new SampleSeriesElementId();
			sampleSeriesElementId.setIndicatorId(successIndicator.getId());			
			sampleSeries.setElementId(sampleSeriesElementId);
			sampleSeries.setElementKind(ElementKind.EK_SUCCESS_INDICATOR);
			sampleSeries.setId(SuccessIndicatorSampleSeriesSet.getSampleSeries().size() + 1);
			sampleSeries.setName(successIndicator.getName());			
			SampleSeriesParameterValues sampleSeriesParameterValues = new SampleSeriesParameterValues();
			sampleSeriesParameterValues.getParameterValue().addAll(successIndicator.getParameterValue());
			sampleSeries.setParameterValues(sampleSeriesParameterValues);	
			Sample sample = generateDailySampleValue(t, value);					
			sampleSeries.getSample().add(sample);
			SuccessIndicatorSampleSeriesSet.getSampleSeries().add(sampleSeries);
		}
        }
        
	public void registerDailySampleValue(Timestamp t, Indicator indicator, double value){
		boolean indicatorFound = false;
		int count_chocula = 0;
		for (SampleSeries sampleSeries : IndicatorSampleSeriesSet.getSampleSeries())
		{
			if (sampleSeries.getName().equalsIgnoreCase(indicator.getName())  && sampleSeries.getContext().equalsIgnoreCase("Monitor"))
			{
				
				indicatorFound = true;
				
				Sample sample = generateDailySampleValue(t, value);					
				sampleSeries.getSample().add(sample);
				IndicatorSampleSeriesSet.getSampleSeries().set(count_chocula, sampleSeries);
			}
			count_chocula++;
			
		}
		if (!indicatorFound)
		{
			SampleSeries sampleSeries = new SampleSeries();
			sampleSeries.setContext("Monitor");
			SampleSeriesElementId sampleSeriesElementId = new SampleSeriesElementId();
			sampleSeriesElementId.setIndicatorId(indicator.getId());			
			sampleSeries.setElementId(sampleSeriesElementId);
			sampleSeries.setElementKind(ElementKind.EK_INDICATOR);
			sampleSeries.setId(IndicatorSampleSeriesSet.getSampleSeries().size() + 1);
			sampleSeries.setName(indicator.getName());			
			SampleSeriesParameterValues sampleSeriesParameterValues = new SampleSeriesParameterValues();
			sampleSeriesParameterValues.getParameterValue().addAll(indicator.getParameterValue());
			sampleSeries.setParameterValues(sampleSeriesParameterValues);	
			Sample sample = generateDailySampleValue(t, value);					
			sampleSeries.getSample().add(sample);
			IndicatorSampleSeriesSet.getSampleSeries().add(sampleSeries);
		}
	}
	
        public Sample generateDailySampleValue(Timestamp t, double value)
	{		
		Sample sample = new Sample();
		sample.setValue(value);
               	sample.setBegintime(t.getTime());
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(t.getTime()));
                int timeUnit = timeUnit = Calendar.DATE;
                c.add(timeUnit, 1);					
		sample.setEndtime(c.getTime().getTime());
                return sample;
        }
        
	public Sample generateSample(Timestamp t, Indicator indicator)
	{		
		Sample sample = new Sample();
		sample.setValue(1);
		sample.setBegintime(t.getTime());
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(t.getTime()));
		int timeUnit = 0;
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_DAY) == 0)
		{
			timeUnit = Calendar.DATE;
		}
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_HOUR) == 0)
		{
			timeUnit = Calendar.HOUR_OF_DAY;
		}
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_MILISECOND) == 0)
		{
			timeUnit = Calendar.MILLISECOND;
		}
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_MINUTE) == 0)
		{
			timeUnit = Calendar.MINUTE;
		}
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_MONTH) == 0)
		{
			timeUnit = Calendar.MONTH;
		}
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_SECOND) == 0)
		{
			timeUnit = Calendar.SECOND;
		}
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_WEEK) == 0)
		{
			timeUnit = Calendar.WEEK_OF_YEAR;
		}
		if (indicator.getTimeUnit().compareTo(TimeUnit.TU_YEAR) == 0)
		{
			timeUnit = Calendar.YEAR;
		}					
		c.add(timeUnit, indicator.getSamplingPeriod());					
		sample.setEndtime(c.getTime().getTime());
		
		return sample;
	}
	
	public String toXMLString()
	{
		String result = "";
		GenericMarshaller<SampleSeriesSet> mrsh = new GenericMarshaller<SampleSeriesSet>(
				SampleSeriesSet.class);
		try {
			result = mrsh.javaToXml(IndicatorSampleSeriesSet);
		} catch (JAXBException e) {			
			logger.error(e.getStackTrace().toString());
		}
		return result;
	}
	
        public SampleSeriesSet getSuccessIndicatorSamples(ArrayList<Integer> successIndicators, Timestamp init_time, Timestamp end_time) throws MonitorException{
            SampleSeriesSet result = new SampleSeriesSet();
            ArrayList<Sample> resultSet = new ArrayList<Sample>();
            String successIndicatorName = "";
            
            //For every success indicator to compute
            for (Integer id : successIndicators){
                ArrayList<Integer> indicatorIds = new ArrayList<Integer>();
                int policyID = -1;
                ArrayList<TemplateParameterValue> templateParameterValueList = new ArrayList<TemplateParameterValue>();
                
                //Look for the formula dependencies of the success indicator
                String formula = "";
                try {
                PolicyEncoderCore core = new PolicyEncoderCore();
    		PolicyModel policyModel = core.getPolicyModelFromDB(); 
                for (Policy policy : policyModel.getPolicies().getPolicy()){
                    policyID = policy.getId();
                    for (SuccessIndicator successIndicator : policy.getPurpose().getInstantiatedSuccessIndicators().getSuccessIndicator()){
                        if (successIndicator.getId() == id) {
                            successIndicatorName = successIndicator.getName();
                            for (SuccessIndicatorTemplate successIndicatorTemplate : policyModel.getSuccessIndicatorTemplates().getSuccessIndicatorTemplate()){
                                if (successIndicator.getTemplateId().equalsIgnoreCase(successIndicatorTemplate.getId()))
                                {
                                    templateParameterValueList = new ArrayList<TemplateParameterValue>();
                                    formula = successIndicatorTemplate.getFormula();
                                    templateParameterValueList.addAll(successIndicator.getParameterValue());
                                    for (FormulaDependency formulaDependency : successIndicatorTemplate.getFormulaDependency()){
                                        for (Indicator indicatorMonitor : policyModel.getIndicators().getIndicator())
                                        {	    		  	    	
                                                for (IndicatorTemplate indicatorTemplate : policyModel.getIndicatorTemplates().getIndicatorTemplate())
                                                {	    		    			
                                                        if ((indicatorMonitor.getTemplateId().equalsIgnoreCase(indicatorTemplate.getId()) && (indicatorTemplate.getFormula().equalsIgnoreCase("Monitoring Indicator"))))
                                                        {
                                                            if (indicatorTemplate.getId().equalsIgnoreCase(formulaDependency.getDependencyTemplateId())){
                                                                if (!indicatorIds.contains(indicatorMonitor.getId())){
                                                                    indicatorIds.add(indicatorMonitor.getId());
                                                                }
                                                            }
                                                        }
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //Compute time series for the indicators based on the monitoring results already received
                SampleSeriesSet subResult = getIndicatorSamples(indicatorIds, init_time, end_time);
                //And store the time series in a string
                String XMLresult = "";
		GenericMarshaller<SampleSeriesSet> mrsh = new GenericMarshaller<SampleSeriesSet>(
				SampleSeriesSet.class);
		
			XMLresult = mrsh.javaToXml(subResult);
			
                //For debugging, just comment if it gets noisy
                //logger.info("<----- Success indicator intermediate results ------>");
                //logger.info(XMLresult);
                //logger.info("<----------->");
                //Run the formula on the time series to obtain the new time series
                Configuration config = new Configuration();
                DynamicQueryContext DataDynamicContext = new DynamicQueryContext(config);
                DataDynamicContext.setContextItem(config.buildDocument(new StreamSource(new java.io.StringReader(XMLresult))));
                StaticQueryContext sqc = new StaticQueryContext (config);
                XQueryExpression exp = sqc.compileQuery(formula);  
                
                
                List<Double> results = exp.evaluate(DataDynamicContext);
           	//logger.info("Pelusso ->" + results.size());
		resultSet = new ArrayList<Sample>();
                int count_chocula = 3;
                Sample r = new Sample();
                for (Double item : results)
                {
                        if (count_chocula % 3 == 0)
                        {
                                r = new Sample();
                                r.setValue(item);	  
                        }	
                        if (count_chocula % 3 == 1)
                        {   		
                                r.setBegintime(item.longValue());	  
                        }	    
                        if (count_chocula % 3 == 2)
                        {
                                r.setEndtime(item.longValue());	  
                                resultSet.add(r);
                        }		        	
                        count_chocula++;
                        //System.out.println(item.longValue());

                }        

                        //Generate the sample series
                SampleSeriesElementId sampleSeriesElementId = new SampleSeriesElementId();      
                SuccessIndicatorId kk = new SuccessIndicatorId();
                kk.setPolicyId(policyID);
                kk.setSuccessIndicatorId(String.valueOf(id));
                sampleSeriesElementId.setSuccessIndicatorId(kk);        

                SampleSeries formulaResult = new SampleSeries();
                formulaResult.setContext("Monitor");
                formulaResult.setElementId(sampleSeriesElementId);
                formulaResult.setElementKind(ElementKind.EK_SUCCESS_INDICATOR);
                SampleSeriesParameterValues sampleSeriesParameterValues = new SampleSeriesParameterValues();
                sampleSeriesParameterValues.getParameterValue().addAll(templateParameterValueList);
                formulaResult.setParameterValues(sampleSeriesParameterValues);

                formulaResult.setId(0);
                formulaResult.setName(successIndicatorName);		
                formulaResult.getSample().addAll(resultSet);
                result.getSampleSeries().add(formulaResult);
                }                
                catch (Exception E) {
                    logger.info(E.getMessage());
                    throw new MonitorException(E.getMessage());
                }
                
                
            }
            
            
            String Stringresult = "";
		GenericMarshaller<SampleSeriesSet> mrsh = new GenericMarshaller<SampleSeriesSet>(
				SampleSeriesSet.class);
		try {
			Stringresult = mrsh.javaToXml(result);
                        logger.info("<----- Success indicator final results ------>");
                        logger.info(Stringresult);
                        logger.info("<----------->");
		} catch (JAXBException e) {			
			logger.error(e.getStackTrace().toString());
		}
            
            return result;
        }
        
        public SampleSeriesSet getSuccesIndicatorSamples(ArrayList<Integer> successIdicators, Timestamp init_time, Timestamp end_time)
	{
            SampleSeriesSet result = new SampleSeriesSet();
		for (SampleSeries sampleSeries : SuccessIndicatorSampleSeriesSet.getSampleSeries())
		{
			if (successIdicators.contains(sampleSeries.getElementId().getIndicatorId()))
			{
				SampleSeries sampleSeriesResult = new SampleSeries();
				sampleSeriesResult.setContext(sampleSeries.getContext());
				sampleSeriesResult.setElementId(sampleSeries.getElementId());
				sampleSeriesResult.setElementKind(sampleSeries.getElementKind());
				sampleSeriesResult.setId(sampleSeries.getId());
				sampleSeriesResult.setName(sampleSeries.getName());
				sampleSeriesResult.setParameterValues(sampleSeries.getParameterValues());
				sampleSeriesResult.setTemplate(sampleSeries.getTemplate());
				for (Sample sample : sampleSeries.getSample())
				{
					Timestamp begin = new Timestamp (sample.getBegintime());
					Timestamp end = new Timestamp (sample.getEndtime());					
					if ((init_time.compareTo(begin) <= 0) && (end_time.compareTo(end) >= 0 ))
					{
						sampleSeriesResult.getSample().add(sample);
					}
				}
				result.getSampleSeries().add(sampleSeriesResult);				
			}
		}
		return result;
        }
        
	public SampleSeriesSet getIndicatorSamples(ArrayList<Integer> indicators, Timestamp init_time, Timestamp end_time)
	{
		SampleSeriesSet result = new SampleSeriesSet();
		for (SampleSeries sampleSeries : IndicatorSampleSeriesSet.getSampleSeries())
		{
			if (indicators.contains(sampleSeries.getElementId().getIndicatorId()))
			{
				SampleSeries sampleSeriesResult = new SampleSeries();
				sampleSeriesResult.setContext(sampleSeries.getContext());
				sampleSeriesResult.setElementId(sampleSeries.getElementId());
				sampleSeriesResult.setElementKind(sampleSeries.getElementKind());
				sampleSeriesResult.setId(sampleSeries.getId());
				sampleSeriesResult.setName(sampleSeries.getName());
				sampleSeriesResult.setParameterValues(sampleSeries.getParameterValues());
				sampleSeriesResult.setTemplate(sampleSeries.getTemplate());
				for (Sample sample : sampleSeries.getSample())
				{
					Timestamp begin = new Timestamp (sample.getBegintime());
					Timestamp end = new Timestamp (sample.getEndtime());					
					if ((init_time.compareTo(begin) <= 0) && (end_time.compareTo(end) >= 0 ))
					{
						sampleSeriesResult.getSample().add(sample);
					}
				}
				result.getSampleSeries().add(sampleSeriesResult);				
			}
		}
		return result;
	}
	
}
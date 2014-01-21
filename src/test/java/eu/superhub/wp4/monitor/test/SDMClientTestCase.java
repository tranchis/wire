package eu.superhub.wp4.monitor.test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import eu.superhub.wp3.marshaller.GenericMarshaller;
import eu.superhub.wp3.models.situationaldatamodel.statements.Normalized;
import eu.superhub.wp3.models.situationaldatamodel.statements.Raw;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.Content;
import eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Pollution;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.superhub.wp4.monitor.core.SDMClient;
import java.io.IOException;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//import eu.superhub.wp4.monitor.core.SDMClient;

public class SDMClientTestCase {
	private Logger	logger;
        
        private void serviceTest()
        {
            logger = LoggerFactory.getLogger(getClass());
		
		logger.info("<-------------- Starting Situational Data model client test -------->");
                
		//SDMClient	sc;
		String		response = "", q = "";
                String responseqr = "";
		
		q = "{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.statements.Normalized\",\"reliabilityScore\":0.0,\"Content\":[{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Pollution\",\"pollutionActiveArea\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.location.Area\",\"ratio\":5000.0,\"PointByCoordinates\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.location.PointByCoordinates\",\"latitudeE6\":41600000,\"longitudeE6\":2000000}},\"validPeriod\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.time.Interval\",\"end_time\":90000000,\"init_time\":15000000},\"Indexes\":{\"class\":\"eu.superhub.wp3.models.situationaldatamodel.statements.normalized.content.pollution.Indexes\",\"airQualityUndex\":0.0,\"atmosphericParticulatesIndex\":0.0,\"carbonDioxideIndex\":0.5,\"nitrogenOxideIndex\":0.0,\"sulfurDioxideIndex\":0.0,\"volatileOrganicCompoundIndex\":0.0},\"localReliability\":0.0}]}";
                    SDMClient sc = new SDMClient("http://atalaya-barcelona.mooo.com:3000/sdm");
		logger.info(q);
                response = sc.query(q);
                logger.info(response);
		
                GenericMarshaller mrsh = new GenericMarshaller(Normalized.class);
                try {
                    
                    Normalized responseStatement = (Normalized) mrsh.jsonToJava(response);
                    for (Content content : responseStatement.getContent() )
                    {
                        
                        Pollution responsePollution  = (Pollution) content;
                        logger.info("Pollution obtained as response is :");
                        logger.info("   · getInitTime : '"  + responsePollution.getValidPeriod().getInitTime() + "'");
                        logger.info("   · getEndTime : '"  + responsePollution.getValidPeriod().getEndTime()+ "'");
                        logger.info("   · getAirQualityUndex : '"  + responsePollution.getIndexes().getAirQualityUndex()+ "'");
                        logger.info("   · getAtmosphericParticulatesIndex : '"  + responsePollution.getIndexes().getAtmosphericParticulatesIndex()+ "'");
                        logger.info("   · getCarbonDioxideIndex : '"  + responsePollution.getIndexes().getCarbonDioxideIndex()+ "'");
                        logger.info("   · getNitrogenOxideIndex : '"  + responsePollution.getIndexes().getNitrogenOxideIndex()+ "'");
                        logger.info("   · getSulfurDioxideIndex : '"  + responsePollution.getIndexes().getSulfurDioxideIndex()+ "'");
                        logger.info("   · getVolatileOrganicCompoundIndex : '"  + responsePollution.getIndexes().getVolatileOrganicCompoundIndex()+ "'");
                    }
                    
                
                Raw dummy = new Raw();
                dummy.setLocalReliabilityScore(0);
                dummy.setMessage("Pelusso Maldades");
                dummy.setUuid(java.util.UUID.randomUUID().toString());
                mrsh = new GenericMarshaller(Raw.class);
                String qr;
                qr = mrsh.javaToJson(dummy);
                logger.info(qr);
                responseqr = sc.query(qr);
                logger.info("<----- Raw statement ----->");
                logger.info(responseqr);
                Raw rawResult = (Raw)mrsh.jsonToJava(responseqr);
                String rawmessage =  rawResult.getMessage();
                Object obj=JSONValue.parse(rawmessage);
                JSONArray array=(JSONArray)obj;
                Double value;
                Long lvalue;
                String key;
                for (int i = 0; i < array.size(); i++)
                {
                    JSONObject obj2=(JSONObject)array.get(i);
                    //System.out.println(obj2);
                    
                    key = "publicTransportModalShare";
                    value = (Double)obj2.get(key);
                    System.out.println(key + " '" + value + "'");

                    key = "timesDayOvercrowding";
                    lvalue = (Long)obj2.get(key);
                    System.out.println(key + " '" + lvalue + "'");

                    key = "minutesDayOvercrowding";
                    value = (Double)obj2.get(key);
                    System.out.println(key + " '" + value + "'");

                    key = "fullStations";
                    lvalue = (Long)obj2.get(key);
                    System.out.println(key + " '" + lvalue + "'");

                    key = "emptyStations";
                    lvalue = (Long)obj2.get(key);
                    System.out.println(key + " '" + lvalue + "'");

                    key = "bikesPerDay";
                    lvalue = (Long)obj2.get(key);
                    System.out.println(key + " '" + value + "'");

                    key = "bikeModalShare";
                    value = (Double)obj2.get(key);
                    System.out.println(key + " '" + value + "'");

                    key = "privateTrafficVehicleKilometer";
                    value = (Double)obj2.get(key);
                    System.out.println(key + " '" + value + "'");
                    
                    key = "privateTransportModalShare";
                    value = (Double)obj2.get(key);
                    System.out.println(key + " '" + value + "'");

                }
                logger.info("<---------------->");
                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                    fail("Exception when using generic marshaller '" + ex.getMessage() + "'");
                }
            
                        if(!response.contains("carbonDioxideIndex")) {
			fail("Not expected behaviour!");
                        }
                        if(!responseqr.contains("publicTransportModalShare")) {
			fail("Not expected behaviour!");}
                        if(!responseqr.contains("timesDayOvercrowding")) {
			fail("Not expected behaviour!");}
                        if(!responseqr.contains("minutesDayOvercrowding")) {
			fail("Not expected behaviour!");}
                        if(!responseqr.contains("fullStations")) {
			fail("Not expected behaviour!");}
                        if(!responseqr.contains("emptyStations")) {
			fail("Not expected behaviour!");}
                        if(!responseqr.contains("bikesPerDay")) {
			fail("Not expected behaviour!");}
                        if(!responseqr.contains("bikeModalShare")) {
			fail("Not expected behaviour!");}
                        if(!responseqr.contains("privateTrafficVehicleKilometer")) {
			fail("Not expected behaviour!");}
		
		logger.info("<-------------- Finishing Situational Data model client test -------->");
        }
        
        private void parseTest()
        {

            String result = "[{\"publicTransportModalShare\":0.2222732942153567,\"timesDayOvercrowding\":0.8683271291188639,\"minutesDayOvercrowding\":0.007876497310462693,\"fullStations\":4500,\"emptyStations\":3450,\"bikesPerDay\":0.9598106222340153,\"bikeModalShare\":0.9486392938844698,\"privateTrafficVehicleKilometer\":0.5078404903460818},{\"publicTransportModalShare\":0.9529096955196941,\"timesDayOvercrowding\":0.3297979572384757,\"minutesDayOvercrowding\":0.6467751763149439,\"fullStations\":4455,\"emptyStations\":3330,\"bikesPerDay\":0.30320555661964144,\"bikeModalShare\":0.24780604845184018,\"privateTrafficVehicleKilometer\":0.6009742289309283}]";
            
            Object obj=JSONValue.parse(result);
            JSONArray array=(JSONArray)obj;
            Double value;
            Long lvalue;
            String key;
            for (int i = 0; i < array.size(); i++)
            {
                JSONObject obj2=(JSONObject)array.get(i);
                //System.out.println(obj2);
                key = "publicTransportModalShare";
                
                value = (Double)obj2.get(key);
                System.out.println(key + " '" + value + "'");
                
                key = "timesDayOvercrowding";
                value = (Double)obj2.get(key);
                System.out.println(key + " '" + value + "'");
                
                key = "minutesDayOvercrowding";
                value = (Double)obj2.get(key);
                System.out.println(key + " '" + value + "'");
                
                key = "fullStations";
                lvalue = (Long)obj2.get(key);
                System.out.println(key + " '" + lvalue + "'");
                
                key = "emptyStations";
                lvalue = (Long)obj2.get(key);
                System.out.println(key + " '" + lvalue + "'");
                
                key = "bikesPerDay";
                value = (Double)obj2.get(key);
                System.out.println(key + " '" + value + "'");
                
                key = "bikeModalShare";
                value = (Double)obj2.get(key);
                System.out.println(key + " '" + value + "'");
                
                key = "privateTrafficVehicleKilometer";
                value = (Double)obj2.get(key);
                System.out.println(key + " '" + value + "'");
                
            }
            
        }
        
	@Test
	public void test() {
		
		serviceTest();
                //parseTest();
                
		
	}
}

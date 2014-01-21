/*******************************************************************************
 * Copyright (c) 2013 SUPERHUB - SUstainable and PERsuasive Human Users moBility    	         www.superhub-project.eu/ 
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ignasi Gomez - Wp4 Situational data service enactor implementation
 *      - Contains a class creator 
 *      - Contains and a function to get traffic from social networks statements. 
 *      
 ******************************************************************************/
package eu.superhub.wp4.monitor.wp3servicedata;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.marshaller.GenericMarshaller;
import eu.superhub.wp3.models.situationaldatamodel.interfaces.SituationalDataRequest;
import eu.superhub.wp3.models.situationaldatamodel.interfaces.SituationalDataResponse;
import eu.superhub.wp3.models.situationaldatamodel.interfaces.StatementType;
import eu.superhub.wp3.models.situationaldatamodel.location.PointByCoordinates;
import eu.superhub.wp3.models.situationaldatamodel.statements.unexpected.Accident;


public class SituationalDataServiceWrapper 
{
	private Logger	logger;
	public SituationalDataServiceWrapper()
	{
		logger = LoggerFactory.getLogger(getClass());
	}

	public SituationalDataResponse enactServiceTrafficSocialNetwork(String city, String country, String stateName, long longitude, long latitude) throws WP3DataClientException
	{
		try {       
			logger.info("Message request for service WP3:SituationalData.getSituationalData. Enacting the service");                   
			//Create the request from situational data model (dependency of this project) and fill it with the information received from the message
			//An alternative would be to marshall the JSON inside the message (which should a Situational Data request by itself) but this is less flexible to changes in the model
			logger.info("    · Creating situationaldata request");                        
			SituationalDataRequest req = new SituationalDataRequest();
			PointByCoordinates loc = new PointByCoordinates();
			loc.setCity(city);
			loc.setCountry(country);
			loc.setStateName(stateName);
			loc.setLatitudeE6(longitude);
			loc.setLongitudeE6(latitude);                        
			req.setStatementType(StatementType.TRAFFIC_FROM_SOCIAL_NETWORK);
			req.setObservedLocation(loc);

			//And transform the object to XML
			logger.info("    · Marshalling the situational data request");                        
			GenericMarshaller<SituationalDataRequest> situationalDataRequestMarshaller = new GenericMarshaller<SituationalDataRequest>(SituationalDataRequest.class);                       
			String xmlRequest = situationalDataRequestMarshaller.javaToXml(req);

			logger.info("    · And the marshalled request is '" + xmlRequest + "'");  

			logger.info("Sending XML...:\n {}" + xmlRequest);

			//Send the message via jms
			logger.info("    · Sending the message. Service enactment");                     
			ProducerTemplate producerTemplate = JMSServicesUtils.getProducerTemplate();   
			String response = (String)producerTemplate.requestBody("jms:wp3:situationaldata:getsituationaldata",xmlRequest);

			logger.info("    · And the service response is '" + response + "'");                                                
			//Unmarshall the response and print it. Monitor will use this information 
			GenericMarshaller<SituationalDataResponse> situationalDataResponseMarshaller = new GenericMarshaller<SituationalDataResponse>(SituationalDataResponse.class, Accident.class);
			SituationalDataResponse resp = situationalDataResponseMarshaller.xmlToJava(response.toString());
			logger.info("    · And the unmrahsalled service response contains '" + resp.getStatement().size() + "' statements");  
			return resp;

		} catch (Exception e) {
			e.printStackTrace();                        
			throw new WP3DataClientException("{\"result\": \"notAccepted\", \"comment\": \" Exception during service enactment '" + e.fillInStackTrace() + "' \"}");
		}        
	}

}

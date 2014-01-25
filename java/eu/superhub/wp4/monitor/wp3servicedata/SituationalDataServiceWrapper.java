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

public class SituationalDataServiceWrapper {
	private Logger logger;

	public SituationalDataServiceWrapper() {
		logger = LoggerFactory.getLogger(getClass());
	}

	public String enactServiceTrafficSocialNetwork(String city, String country,
			String stateName, long longitude, long latitude,
			String statementType) throws WP3DataClientException {
		// Available types:
		// ABNORMAL_TRAFFIC("AbnormalTraffic"),
		// ACCIDENT("Accident"),
		// TRAFFIC_FROM_SOCIAL_NETWORK("TrafficFromSocialNetwork");
		try {
			logger.info("Message request for service WP3:SituationalData.getSituationalData. Enacting the service");
			// Create the request from situational data model (dependency of
			// this project) and fill it with the information received from the
			// message
			// An alternative would be to marshall the JSON inside the message
			// (which should a Situational Data request by itself)
			// but this is less flexible to changes in the model
			logger.info("    Â· Creating situationaldata request");
			String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<SituationalDataRequest xmlns:location=\"http://superhub-project.eu/wp3/location\" xmlns=\"http://superhub-project.eu/wp3/interfaces\">"
					+ "<statementType>"
					+ statementType
					+ "</statementType>"
					+ "<observedLocation>"
					+ "<location:city>"
					+ city
					+ "</location:city>"
					+ "<location:country>"
					+ country
					+ "</location:country>"
					+ "<location:stateName>"
					+ stateName
					+ "</location:stateName>"
					+ "<location:latitudeE6>"
					+ latitude
					+ "</location:latitudeE6>"
					+ "<location:longitudeE6>"
					+ longitude
					+ "</location:longitudeE6>"
					+ "</observedLocation>" + "</SituationalDataRequest>";
			// logger.info("Sending XML...:\n {}" + xmlRequest);

			// Send the message via jms
			logger.info("    Â· Sending the message. Service enactment");
			ProducerTemplate producerTemplate = JMSServicesUtils
					.getProducerTemplate();
			String response = (String) producerTemplate.requestBody(
					"jms:wp3.SituationalData.getSituationalData", xmlRequest);

			// logger.info("    Â· And the service response is '" + response +
			// "'");
			return response;

		} catch (Exception e) {
			e.printStackTrace();
			throw new WP3DataClientException(
					"{\"result\": \"notAccepted\", \"comment\": \" Exception during service enactment '"
							+ e.fillInStackTrace() + "' \"}");
		}
	}

}

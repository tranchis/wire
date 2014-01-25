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

import java.math.BigInteger;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.superhub.wp3.marshaller.GenericMarshaller;
import eu.superhub.wp3.trafficsituation.fromcellnetdatamodel.request.Request;
import eu.superhub.wp3.trafficsituation.fromcellnetdatamodel.response.Response;

public class TrafficSituationServiceWrapper {
	private Logger logger;

	public TrafficSituationServiceWrapper() {
		logger = LoggerFactory.getLogger(getClass());
	}

	public String enactServicePredictedTrafficFromCellNet(BigInteger longitude,
			BigInteger latitude) throws WP3DataClientException {
		try {
			logger.info("Message request for service WP3:TrafficSituation.getPredictedTrafficFromCellNet. Enacting the service");
			ProducerTemplate producerTemplate = JMSServicesUtils
					.getProducerTemplate();

			// Create the request from java classes generated from xsd
			// An alternative would be to marshall the JSON inside the message
			// (which should a Situational Data request by itself)
			// but this is less flexible to changes in the model
			System.out.println("    · Creating request");
			Request req = new Request(longitude, latitude);

			// And transform the object to XML
			logger.info("    · Marshalling the request");
			GenericMarshaller<Request> requestMarshaller = new GenericMarshaller<Request>(
					Request.class);
			String xmlRequest = requestMarshaller.javaToXml(req);

			logger.info("    · And the marshalled request is '" + xmlRequest
					+ "'");

			// Send the message via jms
			logger.info("    · Sending the message. Service enactment");
			String responseString = (String) producerTemplate.requestBody(
					"jms:wp3.TrafficSituation.getPredictedTrafficFromCellNet",
					xmlRequest);

			logger.info("    · And the unmarshalled service response is '"
					+ responseString + "'");

			logger.info("    · Unmashalling the response");
			GenericMarshaller<Response> responseMarshaller = new GenericMarshaller<Response>(
					Response.class);
			Response response = (Response) responseMarshaller
					.xmlToJava(responseString);
			logger.info("    · And the marshalled service response is :  "
					+ "\n          · forecast '" + response.getForecast()
					+ "' " + "\n          · start time '"
					+ response.getValidityStartTime() + "' "
					+ "\n          · end time '"
					+ response.getValidityEndTime() + "' "
					+ "\n          · traffic status '"
					+ response.getTrafficStatus() + "' "
					+ "\n          · latitude '" + response.getLatitude()
					+ "' " + "\n          · longitude '"
					+ response.getLongitude() + "'");

			// return response;
			return responseString;

		} catch (Exception e) {
			e.printStackTrace();
			throw new WP3DataClientException(
					"{\"result\": \"notAccepted\", \"comment\": \" Exception during service enactment '"
							+ e.fillInStackTrace() + "' \"}");
		}
	}

}

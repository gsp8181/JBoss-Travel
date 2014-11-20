/*
 * Geoffrey Prytherch - Adapted from JBoss Examples, with the licence given below
 * 
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.travelagent.travelplan;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.json.JSONObject;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 * @author Geoffrey Prytherch
 * @see TravelPlanValidator
 * @see TravelPlanRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class TravelPlanService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private TravelPlanValidator validator;

    @Inject
    private TravelPlanRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    private final Long travelAgentTaxi = (long) 10000;
    private final Long travelAgentFlight = (long) 18181;
    private final Long travelAgentHotel = (long) 18181;
    
    /**
     * <p>Returns a List of all persisted {@link TravelPlan} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of TravelPlan objects
     */
    List<TravelPlan> findAllOrderedByName() {
        return crud.findAll();
    }

    /**
     * <p>Returns a single TravelPlan object, specified by a Long id.<p/>
     * 
     * @param id The id field of the TravelPlan to be returned
     * @return The TravelPlan with the specified id
     */
    TravelPlan findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Writes the provided TravelPlan object to the application database.<p/>
     *
     * <p>Validates the data in the provided TravelPlan object using a {@link TravelPlanValidator} object.<p/>
     * 
     * @param travelPlan The TravelPlan object to be written to the database using a {@link TravelPlanRepository} object
     * @return The TravelPlan object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    TravelPlan create(TravelSketch travelSketch) throws ConstraintViolationException, ValidationException, Exception {
    	TravelPlan travelPlan = new TravelPlan();//validate travelsketch?
    	Customer c = new Customer();
    	c.setId(travelSketch.getCustomerId());
    	travelPlan.setCustomer(c);
    	log.info("TravelPlanService.create() - Creating travelplan for customer #" + travelPlan.getCustomer().getId());
    	
    	try
    	{
		travelPlan.setHotelBookingId(bookHotel(travelSketch));
    	
    	travelPlan.setFlightBookingId(bookFlight(travelSketch));
    	
    	travelPlan.setTaxiBookingId(bookTaxi(travelSketch));
    	
    	
    	// Check to make sure the data fits with the parameters in the TravelPlan model and passes validation.
    	validator.validateTravelPlan(travelPlan);
    	
		// Write the travelPlan to the database.
        TravelPlan rtn =  crud.create(travelPlan);
        return rtn;
    	} catch (Exception e)
    	{
    		revert(travelPlan);
    		
    		throw e;
    	}

    }

	private Long bookTaxi(TravelSketch travelSketch) throws Exception {
		URI uri = new URIBuilder().setScheme("http")
				.setHost("jbosscontactsangularjs-110060653.rhcloud.com")
				.setPath("/rest/bookings")
				.build();
		HttpPost req = new HttpPost(uri);
		StringEntity params = new StringEntity("{\"customerId\":\"" + travelAgentTaxi.toString() + "\",\"taxiId\":\"" + travelSketch.getTaxiId().toString() +"\",\"bookingDate\":\"" + travelSketch.getBookingDate() + "\"}");
		req.addHeader("Content-Type", "application/json");
		req.setEntity(params);
		CloseableHttpResponse response = httpClient.execute(req);
		if(response.getStatusLine().getStatusCode() != 201)
		{
			throw new Exception("Failed to create a flight booking");
		}
		String responseBody = EntityUtils.toString(response.getEntity());
		JSONObject responseJson = new JSONObject(responseBody);
		long rtn = responseJson.getLong("id");
		HttpClientUtils.closeQuietly(response);
		return rtn;
	}

	private long bookHotel(TravelSketch travelSketch)
			throws Exception {
		URI uri = new URIBuilder().setScheme("http")
				.setHost("travel.gsp8181.co.uk")
				.setPath("/rest/bookings")
				//.setHost("localhost")
				//.setPort(8080)
				//.setPath("/travel/rest/bookings")
				.build();
		HttpPost req = new HttpPost(uri);
		StringEntity params = new StringEntity("{\"customer\":{\"id\":\"" + travelAgentFlight.toString() + "\"},\"hotel\":{\"id\":\"" + travelSketch.getHotelId().toString() +"\"},\"bookingDate\":\"" + travelSketch.getBookingDate() + "\"}");
		req.addHeader("Content-Type", "application/json");
		req.setEntity(params);
		CloseableHttpResponse response = httpClient.execute(req);
		if(response.getStatusLine().getStatusCode() != 201)
		{
			throw new Exception("Failed to create a hotel booking");
		}
		String responseBody = EntityUtils.toString(response.getEntity());
		JSONObject responseJson = new JSONObject(responseBody);
		long rtn = responseJson.getLong("id");
		HttpClientUtils.closeQuietly(response);
		return rtn;
	}
	
	private long bookFlight(TravelSketch travelSketch)
			throws Exception {
		URI uri = new URIBuilder().setScheme("http")
				.setHost("jbosscontactsangularjs-110336260.rhcloud.com")
				.setPath("/rest/bookings")
				.build();
		HttpPost req = new HttpPost(uri);
		StringEntity params = new StringEntity("{\"customerId\":\"" + travelAgentFlight.toString() + "\",\"flightId\":\"" + travelSketch.getFlightId().toString() +"\",\"bookingDate\":\"" + travelSketch.getBookingDate() + "\"}");
		req.addHeader("Content-Type", "application/json");
		req.setEntity(params);
		CloseableHttpResponse response = httpClient.execute(req);
		if(response.getStatusLine().getStatusCode() != 201)
		{
			throw new Exception("Failed to create a flight booking");
		}
		String responseBody = EntityUtils.toString(response.getEntity());
		JSONObject responseJson = new JSONObject(responseBody);
		long rtn = responseJson.getLong("id");
		HttpClientUtils.closeQuietly(response);
		return rtn;
	}
	
	private void revert(TravelPlan travelPlan) throws Exception
	{
		// do independently
		if(travelPlan.getHotelBookingId() != null)
		{
		URI uri = new URIBuilder().setScheme("http")
				.setHost("travel.gsp8181.co.uk")
				.setPath("/rest/bookings/" + travelPlan.getHotelBookingId())
				//.setHost("localhost")
				//.setPort(8080)
				//.setPath("/travel/rest/bookings/" + travelPlan.getHotelBookingId())
				.build();
		HttpDelete req = new HttpDelete(uri);
		CloseableHttpResponse response = httpClient.execute(req);
		if(response.getStatusLine().getStatusCode() != 204)
			{
				
			}
		//String responseBody = EntityUtils.toString(response.getEntity());
		HttpClientUtils.closeQuietly(response);
		}
		
		if(travelPlan.getFlightBookingId() != null)
		{
		URI uri = new URIBuilder().setScheme("http")
				.setHost("jbosscontactsangularjs-110336260.rhcloud.com")
				.setPath("/rest/bookings/" + travelPlan.getFlightBookingId())
				.build();
		HttpDelete req = new HttpDelete(uri);
		CloseableHttpResponse response = httpClient.execute(req);
		if(response.getStatusLine().getStatusCode() != 204)
			{
				
			}
		//String responseBody = EntityUtils.toString(response.getEntity());
		HttpClientUtils.closeQuietly(response);
		}
		if(travelPlan.getTaxiBookingId() != null)
		{
		URI uri = new URIBuilder().setScheme("http")
				.setHost("jbosscontactsangularjs-110060653.rhcloud.com")
				.setPath("/rest/bookings/" + travelPlan.getTaxiBookingId())
				.build();
		HttpDelete req = new HttpDelete(uri);
		CloseableHttpResponse response = httpClient.execute(req);
		if(response.getStatusLine().getStatusCode() != 204)
			{
				
			}
		//String responseBody = EntityUtils.toString(response.getEntity());
		HttpClientUtils.closeQuietly(response);
		}
	}

    /**
     * <p>Deletes the provided TravelPlan object from the application database if found there.<p/>
     * 
     * @param travelPlan The TravelPlan object to be removed from the application database
     * @return The TravelPlan object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    TravelPlan delete(TravelPlan travelPlan) throws Exception {
        //log.info("TravelPlanService.delete() - Deleting " + travelPlan.getFirstName() + " " + travelPlan.getLastName());
        
    	if (travelPlan.getId() == null) {
    	       
            log.info("TravelPlanService.delete() - No ID was found so can't Delete.");
            return null;
        }
    	
		URI uriH = new URIBuilder().setScheme("http")
				.setHost("travel.gsp8181.co.uk")
				.setPath("/rest/bookings/" + travelPlan.getHotelBookingId())
				//.setHost("localhost")
				//.setPort(8080)
				//.setPath("/travel/rest/bookings/" + travelPlan.getHotelBookingId())
				.build();
		HttpDelete reqH = new HttpDelete(uriH);
		CloseableHttpResponse responseH = httpClient.execute(reqH);
		if(responseH.getStatusLine().getStatusCode() != 204)
			{
				
			}
		//String responseBody = EntityUtils.toString(response.getEntity());
		HttpClientUtils.closeQuietly(responseH);
		
		URI uriF = new URIBuilder().setScheme("http")
				.setHost("jbosscontactsangularjs-110336260.rhcloud.com")
				.setPath("/rest/bookings/" + travelPlan.getFlightBookingId())
				.build();
		HttpDelete reqF = new HttpDelete(uriF);
		CloseableHttpResponse responseF = httpClient.execute(reqF);
		if(responseF.getStatusLine().getStatusCode() != 204)
			{
				
			}
		//String responseBody = EntityUtils.toString(response.getEntity());
		HttpClientUtils.closeQuietly(responseF);
		
		
		TravelPlan deletedTravelPlan = null;
		deletedTravelPlan = crud.delete(travelPlan);
		return deletedTravelPlan;
    	
    }

}

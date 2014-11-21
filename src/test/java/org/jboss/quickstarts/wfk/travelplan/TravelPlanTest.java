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
package org.jboss.quickstarts.wfk.travelplan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRESTService;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.quickstarts.wfk.customer.CustomerService;
import org.jboss.quickstarts.wfk.customer.CustomerValidator;
import org.jboss.quickstarts.wfk.hotel.Hotel;
import org.jboss.quickstarts.wfk.hotel.HotelRESTService;
import org.jboss.quickstarts.wfk.hotel.HotelRepository;
import org.jboss.quickstarts.wfk.hotel.HotelService;
import org.jboss.quickstarts.wfk.hotel.HotelValidator;
import org.jboss.quickstarts.wfk.travelagent.travelplan.TravelPlan;
import org.jboss.quickstarts.wfk.travelagent.travelplan.TravelPlanRESTService;
import org.jboss.quickstarts.wfk.travelagent.travelplan.TravelPlanRepository;
import org.jboss.quickstarts.wfk.travelagent.travelplan.TravelPlanService;
import org.jboss.quickstarts.wfk.travelagent.travelplan.TravelPlanValidator;
import org.jboss.quickstarts.wfk.travelagent.travelplan.TravelSketch;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test
 * the JAX-RS endpoint for TravelPlan creation functionality (see
 * {@link TravelPlanRESTService#createTravelPlan(TravelPlan)
 * createTravelPlan(TravelPlan)}).
 * <p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see TravelPlanRESTService
 */
@RunWith(Arquillian.class)
public class TravelPlanTest {

	/**
	 * <p>
	 * Compiles an Archive using Shrinkwrap, containing those external
	 * dependencies necessary to run the tests.
	 * </p>
	 *
	 * <p>
	 * Note: This code will be needed at the start of each Arquillian test, but
	 * should not need to be edited, except to pass *.class values to
	 * .addClasses(...) which are appropriate to the functionality you are
	 * trying to test.
	 * </p>
	 *
	 * @return Micro test war to be deployed and executed.
	 */
	@Deployment
	public static Archive<?> createTestArchive() {
		// HttpComponents and org.JSON are required by TravelPlanService
		File[] libs = Maven
				.resolver()
				.loadPomFromFile("pom.xml")
				.resolve("org.apache.httpcomponents:httpclient:4.3.2",
						"org.json:json:20140107").withTransitivity().asFile();

		Archive<?> archive = ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClasses(TravelPlan.class, TravelPlanRESTService.class,
						TravelPlanRepository.class, TravelPlanValidator.class,
						TravelPlanService.class, TravelSketch.class, Customer.class,
						CustomerRESTService.class, CustomerRepository.class,
						CustomerValidator.class, CustomerService.class,
						Resources.class)
				.addAsLibraries(libs)
				.addAsResource("META-INF/test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource("arquillian-ds.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		return archive;
	}

	@Inject
	TravelPlanRESTService travelPlanRESTService;

	@Inject
	CustomerRESTService customerRESTService;

	@Inject
	private @Named("httpClient") CloseableHttpClient httpClient;
	
	@Inject
	@Named("logger")
	Logger log;

	// Make a complete booking and TEST

	// Make one portion fail and check for reverts

	// Cancel booking and test
	
	@After
	public void tearDown() throws Exception
	{
		Response response = travelPlanRESTService.retrieveAllTravelPlans();
		String responseBody = EntityUtils.toString((HttpEntity) response.getEntity());
		JSONArray responseJSON = new JSONArray(responseBody);
		for(int i = 0;i<responseJSON.length();i++)
		{
			JSONObject jo = responseJSON.getJSONObject(i);
			travelPlanRESTService.deleteTravelPlan(jo.getLong("id"));
		}
		
	}
	
	@Test
	@InSequence(1)
	public void TestBooking() throws Exception
	{
		TravelSketch ts1 = new TravelSketch();
		ts1.setFlightId(10001L);
		ts1.setHotelId(1099L);
		ts1.setTaxiId(101L);
		ts1.setBookingDate("2018-03-29");
		ts1.setCustomerId(createTestCustomer());
		
		Response response = travelPlanRESTService.createTravelPlan(ts1);
		
		assertEquals("Unexpected response", 201, response.getStatus());
		
		long flightId = 0;
		long hotelId = 0;
		long taxiId = 0;
		
		Response r1 = travelPlanRESTService.retrieveAllTravelPlans();
		String responseBody = EntityUtils.toString((HttpEntity) r1.getEntity());
		JSONArray responseJSON = new JSONArray(responseBody);
		for(int i = 0;i<responseJSON.length();i++)
		{
			JSONObject jo = responseJSON.getJSONObject(i);
			if(jo.getJSONObject("customer").getLong("id") == createTestCustomer())
			{
				flightId = jo.getLong("flightBookingId");
				hotelId = jo.getLong("hotelBookingId");
				taxiId = jo.getLong("taxiBookingId");
			}
		} 
		assertNotEquals("Flight did not book", 0L, flightId);
		assertNotEquals("Taxi did not book", 0L, taxiId);
		assertNotEquals("Hotel did not book", 0L, hotelId);
		
		
		
		
		
		
		
		
		
		
		
			URI uri = new URIBuilder()
					.setScheme("http")
					.setHost("travel.gsp8181.co.uk")
					.setPath("/rest/bookings/" + hotelId)
					.build();
			HttpGet req = new HttpGet(uri);
			CloseableHttpResponse response2 = httpClient.execute(req);
			assertEquals("Hotel did not book",200,response2.getStatusLine().getStatusCode());

			HttpClientUtils.closeQuietly(response2);

		
			URI uri1 = new URIBuilder()
					.setScheme("http")
					.setHost("jbosscontactsangularjs-110336260.rhcloud.com")
					.setPath(
							"/rest/bookings/" + flightId)
					.build();
			HttpGet req1 = new HttpGet(uri1);
			CloseableHttpResponse response3 = httpClient.execute(req1);
			assertEquals("Flight did not book",200,response3.getStatusLine().getStatusCode());
			HttpClientUtils.closeQuietly(response3);
			
			URI uri2 = new URIBuilder().setScheme("http")
					.setHost("jbosscontactsangularjs-110060653.rhcloud.com")
					.setPath("/rest/bookings/" + taxiId)
					.build();
			HttpGet req2 = new HttpGet(uri2);
			CloseableHttpResponse response4 = httpClient.execute(req2);
			assertEquals("Taxi did not book",200,response4.getStatusLine().getStatusCode());
			HttpClientUtils.closeQuietly(response4);
		
		
		
	}
	
	@Test
	@InSequence(2)
	public void TestFail() throws Exception
	{
		
	}
	
	@Test
	@InSequence(3)
	public void TestCancel() throws Exception
	{
		
	}
	
	@Test
	@InSequence(4)
	public void TestDoubleBook() throws Exception
	{
		
	}

	/**
	 * <p>
	 * A utility method to construct a
	 * {@link org.jboss.quickstarts.wfk.customer.Customer Customer} object for
	 * use in testing. This object is not persisted.
	 * </p>
	 *
	 * @return The ID of the created customer
	 */
	private Long createTestCustomer() throws Exception {

		Response c1 = customerRESTService
				.retrieveCustomersByEmail("testbooking@ncl.ac.uk");

		if (c1.getStatus() == 200) {
			String responseBody = EntityUtils.toString((HttpEntity) c1
					.getEntity());
			JSONObject responseJson = new JSONObject(responseBody);
			return responseJson.getLong("id");
		} else {

			Customer customer = new Customer();
			customer.setName("TEST CUSTOMER");
			customer.setEmail("testbooking@ncl.ac.uk");
			customer.setPhoneNumber("07419999999");

			Response response = customerRESTService.createCustomer(customer);

			if (response.getStatus() != 201) {
				throw new Exception("Customer could not be created");
			} else {
				Response c2 = customerRESTService
						.retrieveCustomersByEmail("testbooking@ncl.ac.uk");

				if (c2.getStatus() == 200) {
					String responseBody = EntityUtils.toString((HttpEntity) c2
							.getEntity());
					JSONObject responseJson = new JSONObject(responseBody);
					return responseJson.getLong("id");
				} else {
					throw new Exception("Customer could not be created");
				}
			}

		}
	}



}

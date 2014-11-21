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
package org.jboss.quickstarts.wfk.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
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
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>
 * A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test
 * the JAX-RS endpoint for Booking creation functionality (see
 * {@link BookingRESTService#createBooking(Booking) createBooking(Booking)}).
 * <p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see BookingRESTService
 */
@RunWith(Arquillian.class)
public class AddBookingTest {

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
		// HttpComponents and org.JSON are required by BookingService
		File[] libs = Maven
				.resolver()
				.loadPomFromFile("pom.xml")
				.resolve("org.apache.httpcomponents:httpclient:4.3.2",
						"org.json:json:20140107").withTransitivity().asFile();

		Archive<?> archive = ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClasses(Booking.class, BookingRESTService.class,
						BookingRepository.class, BookingValidator.class,
						BookingService.class, Customer.class, CustomerRESTService.class, CustomerRepository.class, CustomerValidator.class, CustomerService.class,Hotel.class, HotelRESTService.class, HotelRepository.class, HotelValidator.class, HotelService.class, Resources.class)
				.addAsLibraries(libs)
				.addAsResource("META-INF/test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource("arquillian-ds.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		return archive;
	}

	@Inject
	BookingRESTService bookingRESTService;

	@Inject
	CustomerRESTService customerRESTService;

	@Inject
	HotelRESTService hotelRESTService;

	@Inject
	@Named("logger")
	Logger log;

	@Test
	@InSequence(1)
	public void testRegister() throws Exception {
		Booking booking = createBookingInstance(createTestCustomer(),
				createTestHotel(), "2020-05-1");
		Response response = bookingRESTService.createBooking(booking);

		assertEquals("Unexpected response status", 201, response.getStatus());
		log.info(" New booking was persisted and returned status "
				+ response.getStatus());
	}

	@SuppressWarnings("unchecked")
	@Test
	@InSequence(2)
	public void testInvalidRegister() throws Exception {
		Booking booking = createBookingInstance(0L, 0L, "2020-06-01");
		Response response = bookingRESTService.createBooking(booking);

		assertEquals("Unexpected response status", 400, response.getStatus());
		assertNotNull("response.getEntity() should not be null",
				response.getEntity());
		assertEquals(
				"Unexpected response.getEntity(). It contains "
						+ response.getEntity(), 4,
				((Map<String, String>) response.getEntity()).size());
		log.info("Invalid booking register attempt failed with return code "
				+ response.getStatus());
	}

	@SuppressWarnings("unchecked")
	@Test
	@InSequence(3)
	public void testDateInPast() throws Exception {
		Booking booking = createBookingInstance(createTestCustomer(),
				createTestHotel(), "2010-05-02");
		Response response = bookingRESTService.createBooking(booking);

		assertEquals("Unexpected response status", 400, response.getStatus());
		assertNotNull("response.getEntity() should not be null",
				response.getEntity());
		assertEquals(
				"Unexpected response.getEntity(). It contains "
						+ response.getEntity(), 4,
				((Map<String, String>) response.getEntity()).size());
		log.info("Past Date booking register attempt failed with return code "
				+ response.getStatus());
	}

	@Test
	@InSequence(4)
	public void testGetAllBookings() throws Exception {
		Response response = bookingRESTService.retrieveAllBookings();

		assertEquals("Unexpected response status", 200, response.getStatus());
		log.info(" All bookings were retrieved with one request and returned status "
				+ response.getStatus());
	}
	
	@Test
	@InSequence(5)
	public void testGetAllBookingsForCustomer() throws Exception {
		createBookingInstance(createTestCustomer(),
				createTestHotel(), "2010-05-06");
		createBookingInstance(createTestCustomer(),
				createTestHotel(), "2010-05-07");
		Response response = bookingRESTService.retrieveBookingByCustomerId(createTestCustomer());
		assertEquals("Unexpected response status", 200, response.getStatus());
		
		
		String responseBody = EntityUtils.toString((HttpEntity) response.getEntity());
		JSONArray responseJson = new JSONArray(responseBody);
		
		assertEquals("Unexpected length of records", 2, responseJson.length());
		
		log.info(" All bookings for the test customer were retrieved with one request and returned status "
				+ response.getStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@InSequence(6)
	public void testDoubleBook() throws Exception {
		createBookingInstance(createTestCustomer(),
				createTestHotel(), "2010-05-02");
		Booking booking2 = createBookingInstance(createTestCustomer(),
				createTestHotel(), "2010-05-02");
		Response response = bookingRESTService.createBooking(booking2);

		assertEquals("Unexpected response status", 400, response.getStatus());
		assertNotNull("response.getEntity() should not be null",
				response.getEntity());
		assertEquals(
				"Unexpected response.getEntity(). It contains "
						+ response.getEntity(), 4,
				((Map<String, String>) response.getEntity()).size());
		log.info("Past Date booking register attempt failed with return code "
				+ response.getStatus());
	}

	/*@SuppressWarnings("unchecked")
	@Test
	@InSequence(7)
	public void testChangeId() throws Exception {
		Booking booking = createBookingInstance(createTestCustomer(),
				createTestHotel(), "2020-05-1");
		Response response = bookingRESTService.createBooking(booking);

		assertTrue("NOT YET IMPLEMENTED", false);
		assertEquals("Unexpected response status", 400, response.getStatus());
		assertNotNull("response.getEntity() should not be null",
				response.getEntity());
		assertEquals(
				"Unexpected response.getEntity(). It contains "
						+ response.getEntity(), 4,
				((Map<String, String>) response.getEntity()).size());
		log.info("Special chars in Name booking register attempt failed with return code "
				+ response.getStatus());
	}*/

	@Test
	@InSequence(8)
	public void testDelete() throws Exception {
		Booking booking = createBookingInstance(createTestCustomer(),
				createTestHotel(), "2010-05-06");
		Response response = bookingRESTService.createBooking(booking);
		
		assertEquals("Unexpected response status", 200, response.getStatus());

		bookingRESTService.deleteBooking(1L);
		
		Response rF = bookingRESTService.retrieveBookingByCustomerId(createTestCustomer());
		assertEquals("Unexpected response status", 200, rF.getStatus());
		
		
		String responseBody = EntityUtils.toString((HttpEntity) rF.getEntity());
		JSONArray responseJson = new JSONArray(responseBody);
		
		assertEquals("Unexpected length of records", 0, responseJson.length());
		
		log.info("Booking for test customer was successfully deleted and returned status code "
				+ rF.getStatus());
		
	}

	/**
	 * <p>
	 * A utility method to construct a
	 * {@link org.jboss.quickstarts.wfk.booking.Booking Booking} object for use
	 * in testing. This object is not persisted.
	 * </p>
	 *
	 * @param customerId
	 *            The associated customer ID of the Booking being created
	 * @param hotelId
	 *            The associated hotel ID of the Booking being created
	 * @param bookingDateString
	 *            The string representation of the booking date of the Booking
	 *            being created in format yyyy-MM-dd
	 * @return The Booking object create
	 * @throws ParseException
	 *             When the date string cannot be parsed
	 */
	private Booking createBookingInstance(Long customerId, Long hotelId,
			String bookingDateString) throws ParseException {
		Booking booking = new Booking();

		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
		Date bookingDate = parser.parse(bookingDateString);
		booking.setBookingDate(bookingDate);

		Customer c = new Customer();
		c.setId(customerId);
		booking.setCustomer(c);

		Hotel h = new Hotel();
		h.setId(hotelId);
		booking.setHotel(h);

		return booking;
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
		
		/**
		 * <p>
		 * A utility method to construct a
		 * {@link org.jboss.quickstarts.wfk.hotel.Hotel Hotel} object for
		 * use in testing. This object is not persisted.
		 * </p>
		 *
		 * @return The ID of the created hotel
		 */
		private Long createTestHotel() throws Exception {

			Response c1 = hotelRESTService.retrieveAllHotels();

			if (c1.getStatus() == 200) {
				String responseBody = EntityUtils.toString((HttpEntity) c1.getEntity());
				JSONArray responseJson = new JSONArray(responseBody);
				for(int i = 0;i<responseJson.length();i++)
				{
					JSONObject current = responseJson.getJSONObject(i);
					if( current.getString("phoneNumber") == "07418995999")
					{
						return current.getLong("id");
					}
				}
			} 

				Hotel hotel = new Hotel();
				hotel.setName("TEST HOTEL");
				hotel.setPostcode("H72 3PZ");
				hotel.setPhoneNumber("07418995999");

				Response response = hotelRESTService.createHotel(hotel);

				if (response.getStatus() != 201) {
					throw new Exception("Hotel could not be created");
				} else {
					
					Response c2 = hotelRESTService.retrieveAllHotels();

					if (c2.getStatus() == 200) {
						String responseBody = EntityUtils.toString((HttpEntity) c2.getEntity());
						JSONArray responseJson = new JSONArray(responseBody);
						for(int i = 0;i<responseJson.length();i++)
						{
							JSONObject current = responseJson.getJSONObject(i);
							if( current.getString("phoneNumber") == "07418995999")
							{
								return current.getLong("id");
							}
						}
					} 
					throw new Exception("Hotel could not be created");
					
				}

			

	}
}

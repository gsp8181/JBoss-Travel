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
package org.jboss.quickstarts.wfk.hotel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Hotel creation functionality
 * (see {@link HotelRESTService#createHotel(Hotel) createHotel(Hotel)}).<p/>
 *
 * 
 * @author balunasj
 * @author Joshua Wilson
 * @see HotelRESTService
 */
@RunWith(Arquillian.class)
public class AddHotelTest {

    /**
     * <p>Compiles an Archive using Shrinkwrap, containing those external dependencies necessary to run the tests.</p>
     *
     * <p>Note: This code will be needed at the start of each Arquillian test, but should not need to be edited, except
     * to pass *.class values to .addClasses(...) which are appropriate to the functionality you are trying to test.</p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        //HttpComponents and org.JSON are required by ContactService
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(
                "org.apache.httpcomponents:httpclient:4.3.2",
                "org.json:json:20140107"
        ).withTransitivity().asFile();

        Archive<?> archive = ShrinkWrap
            .create(WebArchive.class, "test.war")
            .addClasses(Hotel.class, 
            			HotelRESTService.class, 
            			HotelRepository.class, 
            			HotelValidator.class, 
            			HotelService.class, 
                        Resources.class)
            .addAsLibraries(libs)
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("arquillian-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
    }

    @Inject
    HotelRESTService hotelRESTService;
    
    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

    @Test
    @InSequence(1)
    public void testRegister() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel", "A00 0AA", "01910001234");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New customer was persisted and returned status " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(2)
    public void testInvalidRegister() throws Exception {
    	Hotel hotel = createHotelInstance("", "", "");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    @InSequence(3)
    public void testDuplicatePhone() throws Exception {
        // Register an initial user
    	Hotel hotel = createHotelInstance("One Hotel", "Q82 1AP", "08007383773");
        hotelRESTService.createHotel(hotel);

        // Register a different user with the same email
        Hotel anotherHotel = createHotelInstance("Another Hotel", "O11 9XZ", "08007383773");
        Response response = hotelRESTService.createHotel(anotherHotel);

        assertEquals("Unexpected response status", 409, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains" + response.getEntity(), 1,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Duplicate hotel register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @InSequence(4)
    public void testInvalidName() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel By The Sea With Loads Of Letters In The Name", "A01 0AA", "01910401234");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @InSequence(5)
    public void testInvalidPhoneNumber() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel Two", "A01 0AA", "0191040123411");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @InSequence(6)
    public void testInvalidPhoneNumberTwo() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel Two", "A01 0AA", "01910ABC234");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @InSequence(7)
    public void testInvalidPostcode() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel Two", "A0& 0AA", "01910401234");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @InSequence(8)
    public void testInvalidPostcodeTwo() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel Two", "A01AA 0AA", "01910401234");
        Response response = hotelRESTService.createHotel(hotel);

        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }
    
    @Test
    @InSequence(9)
    public void testGetAllHotels() throws Exception
    {
            Response response = hotelRESTService.retrieveAllHotels();

            assertEquals("Unexpected response status", 200, response.getStatus());
            log.info(" All hotels were retrieved with one request and returned status " + response.getStatus());
    }
    
    /*@SuppressWarnings("unchecked")
	@Test
    @InSequence(10)
    public void testChangeId() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel Two", "A01 0AA", "01910401234");
    	Response response = hotelRESTService.createHotel(hotel);

    	assertTrue("NOT IMPLEMENTED", false);
        assertEquals("Unexpected response status", 400, response.getStatus());
        assertNotNull("response.getEntity() should not be null", response.getEntity());
        assertEquals("Unexpected response.getEntity(). It contains " + response.getEntity(), 4,
            ((Map<String, String>) response.getEntity()).size());
        log.info("Invalid customer register attempt failed with return code " + response.getStatus());
    }*/
    
    
    @Test
    @InSequence(11)
    public void testDelete() throws Exception {
    	Hotel hotel = createHotelInstance("Good Hotel Del", "A08 0AA", "01910201234");
        Response response = hotelRESTService.createHotel(hotel);
        
        assertEquals("Unexpected response status", 201, response.getStatus());
        
        Response r2 = hotelRESTService.deleteHotel(hotel.getId());
        
        assertEquals("Unexpected response status", 400, r2.getStatus());
        log.info("Delete hotel failed with return code " + r2.getStatus());
    }
    
    
    

    /**
     * <p>A utility method to construct a {@link org.jboss.quickstarts.wfk.hotel.Hotel Hotel} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param name 		The name of the Hotel being created
     * @param postcode  The postcode of the Hotel being created
     * @param phone     The phone number of the Hotel being created
     * @return The Contact object create
     */
    private Hotel createHotelInstance(String name, String postcode, String phone) {
    	Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setPostcode(postcode);
        hotel.setPhoneNumber(phone);
        return hotel;
    }
}

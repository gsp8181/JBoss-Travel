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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * <p>This class exposes the functionality of {@link BookingService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the Booking resource is rest/bookings .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Geoffrey Prytherch
 * @see BookingService
 * @see javax.ws.rs.core.Response
 */
/*
 * The Path annotation defines this as a REST Web Service using JAX-RS.
 * 
 * By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they 
 * can be overridden by adding the Consumes or Produces annotations to the individual method.
 * 
 * It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow 
 * transaction demarcation when accessing the database." - Antonio Goncalves
 * 
 */
@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class BookingRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private BookingService service;
    
    /**
     * <p>Search for and return all the Bookings.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of Bookings
     */
    @GET
    public Response retrieveAllBookings() {
        List<Booking> bookings = service.findAllOrderedByIds();
        return Response.ok(bookings).build();
    }

    /**
     * <p>Search for and return a Booking identified by email address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between email addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate email addresses.</p>
     *
     *
     * @param email The string parameter value provided as a Booking's email
     * @return A Response containing a single Booking
     */
    /*@GET
    @Path("/{email:^.+@.+$}")
    public Response retrieveBookingsByEmail(@PathParam("email") String email) {
        Booking booking;
        try {
            booking = service.findByEmail(email);
        } catch (NoResultException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(booking).build();
    }*/
    
    /**
     * <p>Search for and return a Booking identified by id.</p>
     * 
     * @param id The long parameter value provided as a Booking's id
     * @return A Response containing a single Booking
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveBookingById(@PathParam("id") long id) {
        Booking booking = service.findById(id);
        if (booking == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("retrieveBookingById: found Booking " + booking.getId()  + " = customer: " + booking.getCustomer().getId() + " hotel: " + booking.getHotel().getId() + " on " + booking.getBookingDate());
        
        return Response.ok(booking).build();
    }
    
    /**
     * <p>Search for and return a collection of bookings identified by its customerId.</p>
     * 
     * @param id The long parameter value provided as a Booking's customerId
     * @return A Response containing a list of Bookings
     */
    @GET
    @Path("/customer/{customerId:[0-9]+}")
    public Response retrieveBookingByCustomerId(@PathParam("customerId") long customerId) {
        List<Booking> bookings = service.findByCustomerId(customerId);
        if (bookings == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        for(Booking booking : bookings)
        	log.info("retrieveBookingById: found Booking " + booking.getId()  + " = customer: " + booking.getCustomer().getId() + " hotel: " + booking.getHotel().getId() + " on " + booking.getBookingDate());
        
        return Response.ok(bookings).build();
    }

    /**
     * <p>Creates a new booking from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>created</i> via {@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createBooking(Booking booking) {
        log.info("createBooking started. Booking = customer: " + booking.getCustomer().getId() + " hotel: " + booking.getHotel().getId() + " on " + booking.getBookingDate());
        if (booking == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new Booking.
            service.create(booking);

            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);
            
            log.info("createBooking completed. Booking =  = customer: " + booking.getCustomer().getId() + " hotel: " + booking.getHotel().getId() + " on " + booking.getBookingDate());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            if(e.getMessage().startsWith("Date/Hotel"))
            		{
            responseObj.put("hotelId/bookingDate", "That Hotel and Date combination is already used, please use a unique combination");
            		} else if (e.getMessage().startsWith("Hotel ID"))
            		{
            			responseObj.put("hotelId", "That Hotel ID provided does not match a corresponding record in the Hotel database");
            		} else if(e.getMessage().startsWith("Customer ID"))
            		{
            			responseObj.put("customerId", "That Customer ID provided does not match a corresponding record in the Customer database");
            		}
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Updates a booking with the ID provided in the Booking. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>updated</i> via {@link BookingService#update(Booking)}
     * @param id The long parameter value provided as the id of the Booking to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    public Response updateBooking(@PathParam("id") long id, Booking booking) {
        if (booking == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateBooking started. Booking " + booking.getId()  + " = customer: " + booking.getCustomer().getId() + " hotel: " + booking.getHotel().getId() + " on " + booking.getBookingDate());

        if (booking.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The booking ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(booking.getId()) == null) {
            // Verify if the booking exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the Booking.
            service.update(booking);

            // Create an OK Response and pass the booking back in case it is needed.
            builder = Response.ok(booking);

            log.info("updateBooking completed. Booking " + booking.getId()  + " = customer: " + booking.getCustomer().getId() + " hotel: " + booking.getHotel().getId() + " on " + booking.getBookingDate());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            if(e.getMessage().startsWith("Date/Hotel"))
    		{
    responseObj.put("hotelId/bookingDate", "That Hotel and Date combination is already used, please use a unique combination");
    		} else if (e.getMessage().startsWith("Hotel ID"))
    		{
    			responseObj.put("hotelId", "That Hotel ID provided does not match a corresponding record in the Hotel database");
    		} else if(e.getMessage().startsWith("Customer ID"))
    		{
    			responseObj.put("customerId", "That Customer ID provided does not match a corresponding record in the Customer database");
    		}
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>Deletes a booking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the Booking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    public Response deleteBooking(@PathParam("id") Long id) {
        log.info("deleteBooking started. Booking ID = " + id);
        Response.ResponseBuilder builder = null;

        try {
            Booking booking = service.findById(id);
            if (booking != null) {
                service.delete(booking);
            } else {
                log.info("BookingRESTService - deleteBooking - No booking with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteBooking completed. Booking " + booking.getId()  + " = customer: " + booking.getCustomer().getId() + " hotel: " + booking.getHotel().getId() + " on " + booking.getBookingDate());
        } catch (Exception e) {
            log.info("Exception - " + e.toString());
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }
    
    /**
     * <p>Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can be used
     * by calling client applications to display violations to users.<p/>
     * 
     * @param violations A Set of violations that need to be reported in the Response body
     * @return A Bad Request (400) Response containing all violation messages
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }


}

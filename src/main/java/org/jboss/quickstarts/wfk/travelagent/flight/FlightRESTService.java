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
package org.jboss.quickstarts.wfk.travelagent.flight;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * <p>This class exposes the functionality of {@link CustomerService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the Customer resource is rest/Customers .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Geoffrey Prytherch
 * @see CustomerService
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
@Path("/travelagent/flights")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class FlightRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private FlightService service;    
    
    /**
     * <p>Search for and return all the Customers.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of Customers
     */
    @GET
    public Response retrieveAllFlights() {
        JSONArray flights = service.findAllOrderedByName();
        
        if (flights == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(flights.toString()).build();
        
    }
    
    /**
     * <p>Search for and return a Hotel identified by id.</p>
     * 
     * @param id The long parameter value provided as a Hotel's id
     * @return A Response containing a single Hotel
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveFlightById(@PathParam("id") long id) {
        JSONObject flight = service.findById(id);
        if (flight == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        //log.info("findById " + id + ": found Hotel = " + Hotel.getName() + " " + Hotel.getPostcode() + " " + Hotel.getPhoneNumber() + " " + Hotel.getId());
        
        return Response.ok(flight.toString()).build();
    }
    
    
}
/*
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
package org.jboss.quickstarts.wfk.customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.NoSuchEntityException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;

/**
 * <p>This class exposes the functionality of {@link CustomerService} over HTTP endpoints as a RESTful resource via
 * JAX-RS.</p>
 *
 * <p>Full path for accessing the Customer resource is rest/Customers .</p>
 *
 * <p>The resource accepts and produces JSON.</p>
 * 
 * @author Joshua Wilson
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
@Path("/Customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class CustomerRESTService {
    @Inject
    private @Named("logger") Logger log;
    
    @Inject
    private CustomerService service;
    
    /**
     * <p>Search for and return all the Customers.  They are sorted alphabetically by name.</p>
     * 
     * @return A Response containing a list of Customers
     */
    @GET
    public Response retrieveAllCustomers() {
        List<Customer> Customers = service.findAllOrderedByName();
        return Response.ok(Customers).build();
    }

    /**
     * <p>Search for and return a Customer identified by email address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between email addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate email addresses.</p>
     *
     *
     * @param email The string parameter value provided as a Customer's email
     * @return A Response containing a single Customer
     */
    @GET
    @Path("/{email:^.+@.+$}")
    public Response retrieveCustomersByEmail(@PathParam("email") String email) {
        Customer Customer;
        try {
            Customer = service.findByEmail(email);
        } catch (NoResultException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(Customer).build();
    }
    
    /**
     * <p>Search for and return a Customer identified by id.</p>
     * 
     * @param id The long parameter value provided as a Customer's id
     * @return A Response containing a single Customer
     */
    @GET
    @Path("/{id:[0-9]+}")
    public Response retrieveCustomerById(@PathParam("id") long id) {
        Customer Customer = service.findById(id);
        if (Customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Customer = " + Customer.getFirstName() + " " + Customer.getLastName() + " " + Customer.getEmail() + " " + Customer.getPhoneNumber() + " "
                + Customer.getBirthDate() + " " + Customer.getId());
        
        return Response.ok(Customer).build();
    }

    /**
     * <p>Creates a new Customer from the values provided. Performs validation and will return a JAX-RS response with either 200 (ok)
     * or with a map of fields, and related errors.</p>
     * 
     * @param Customer The Customer object, constructed automatically from JSON input, to be <i>created</i> via {@link CustomerService#create(Customer)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    public Response createCustomer(Customer Customer) {
        log.info("createCustomer started. Customer = " + Customer.getFirstName() + " " + Customer.getLastName() + " " + Customer.getEmail() + " " + Customer.getPhoneNumber() + " "
            + Customer.getBirthDate() + " " + Customer.getId());
        if (Customer == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        
        Response.ResponseBuilder builder = null;

        try {
            // Go add the new Customer.
            service.create(Customer);

            // Create a "Resource Created" 201 Response and pass the Customer back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(Customer);
            
            log.info("createCustomer completed. Customer = " + Customer.getFirstName() + " " + Customer.getLastName() + " " + Customer.getEmail() + " " + Customer.getPhoneNumber() + " "
                + Customer.getBirthDate() + " " + Customer.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "That email is already used, please use a unique email");
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
     * <p>Updates a Customer with the ID provided in the Customer. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.</p>
     * 
     * @param Customer The Customer object, constructed automatically from JSON input, to be <i>updated</i> via {@link CustomerService#update(Customer)}
     * @param id The long parameter value provided as the id of the Customer to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    public Response updateCustomer(@PathParam("id") long id, Customer Customer) {
        if (Customer == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        log.info("updateCustomer started. Customer = " + Customer.getFirstName() + " " + Customer.getLastName() + " " + Customer.getEmail() + " " + Customer.getPhoneNumber() + " "
                + Customer.getBirthDate() + " " + Customer.getId());

        if (Customer.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Response response = Response.status(Response.Status.CONFLICT).entity("The Customer ID cannot be modified").build();
            throw new WebApplicationException(response);
        }
        if (service.findById(Customer.getId()) == null) {
            // Verify if the Customer exists. Return 404, if not present.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        
        Response.ResponseBuilder builder = null;
        
        try {
            // Apply the changes the Customer.
            service.update(Customer);

            // Create an OK Response and pass the Customer back in case it is needed.
            builder = Response.ok(Customer);

            log.info("updateCustomer completed. Customer = " + Customer.getFirstName() + " " + Customer.getLastName() + " " + Customer.getEmail() + " " + Customer.getPhoneNumber() + " "
                + Customer.getBirthDate() + " " + Customer.getId());
        } catch (ConstraintViolationException ce) {
            log.info("ConstraintViolationException - " + ce.toString());
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            log.info("ValidationException - " + e.toString());
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<String, String>();
            responseObj.put("email", "That email is already used, please use a unique email");
            responseObj.put("error", "This is where errors are displayed that are not related to a specific field");
            responseObj.put("anotherError", "You can find this error message in /src/main/java/org/jboss/quickstarts/wfk/rest/CustomerRESTService.java line 242.");
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
     * <p>Deletes a Customer using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 200 OK or with a map of fields, and related errors.</p>
     * 
     * @param id The Long parameter value provided as the id of the Customer to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        log.info("deleteCustomer started. Customer ID = " + id);
        Response.ResponseBuilder builder = null;

        try {
            Customer Customer = service.findById(id);
            if (Customer != null) {
                service.delete(Customer);
            } else {
                log.info("CustomerRESTService - deleteCustomer - No Customer with matching ID was found so can't Delete.");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            builder = Response.noContent();
            log.info("deleteCustomer completed. Customer = " + Customer.getFirstName() + " " + Customer.getLastName() + " " + Customer.getEmail() + " " + Customer.getPhoneNumber() + " "
                + Customer.getBirthDate() + " " + Customer.getId());
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

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


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

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
 * @author Joshua Wilson
 * @see CustomerValidator
 * @see CustomerRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class CustomerService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private CustomerValidator validator;

    @Inject
    private CustomerRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Customer} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of Customer objects
     */
    List<Customer> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Customer object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    Customer findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Customer object, specified by a String email.</p>
     *
     * <p>If there is more than one Customer with the specified email, only the first encountered will be returned.<p/>
     * 
     * @param email The email field of the Customer to be returned
     * @return The first Customer with the specified email
     */
    Customer findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * <p>Returns a single Customer object, specified by a String firstName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param firstName The firstName field of the Customer to be returned
     * @return The first Customer with the specified firstName
     */
    Customer findByFirstName(String firstName) {
        return crud.findByFirstName(firstName);
    }

    /**
     * <p>Returns a single Customer object, specified by a String lastName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param lastName The lastName field of the Customer to be returned
     * @return The first Customer with the specified lastName
     */
    Customer findByLastName(String lastName) {
        return crud.findByFirstName(lastName);
    }

    /**
     * <p>Writes the provided Customer object to the application database.<p/>
     *
     * <p>Validates the data in the provided Customer object using a {@link CustomerValidator} object.<p/>
     * 
     * @param Customer The Customer object to be written to the database using a {@link CustomerRepository} object
     * @return The Customer object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Customer create(Customer Customer) throws ConstraintViolationException, ValidationException, Exception {
        log.info("CustomerService.create() - Creating " + Customer.getFirstName() + " " + Customer.getLastName());
        
        // Check to make sure the data fits with the parameters in the Customer model and passes validation.
        validator.validateCustomer(Customer);

        //Perform a rest call to get the state of the Customer from the allareacodes.com API
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", Customer.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        Customer.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);


        // Write the Customer to the database.
        return crud.create(Customer);
    }

    /**
     * <p>Updates an existing Customer object in the application database with the provided Customer object.<p/>
     *
     * <p>Validates the data in the provided Customer object using a CustomerValidator object.<p/>
     * 
     * @param Customer The Customer object to be passed as an update to the application database
     * @return The Customer object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Customer update(Customer Customer) throws ConstraintViolationException, ValidationException, Exception {
        log.info("CustomerService.update() - Updating " + Customer.getFirstName() + " " + Customer.getLastName());
        
        // Check to make sure the data fits with the parameters in the Customer model and passes validation.
        validator.validateCustomer(Customer);

        //Perform a rest call to get the state of the Customer from the allareacodes.com API
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", Customer.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        Customer.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);

        // Either update the Customer or add it if it can't be found.
        return crud.update(Customer);
    }

    /**
     * <p>Deletes the provided Customer object from the application database if found there.<p/>
     * 
     * @param Customer The Customer object to be removed from the application database
     * @return The Customer object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Customer delete(Customer Customer) throws Exception {
        log.info("CustomerService.delete() - Deleting " + Customer.getFirstName() + " " + Customer.getLastName());
        
        Customer deletedCustomer = null;
        
        if (Customer.getId() != null) {
            deletedCustomer = crud.delete(Customer);
        } else {
            log.info("CustomerService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedCustomer;
    }

}

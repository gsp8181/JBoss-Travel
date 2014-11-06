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
package org.jboss.quickstarts.wfk.contact;


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
 * @see ContactValidator
 * @see ContactRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class ContactService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private ContactValidator validator;

    @Inject
    private ContactRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Contact} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of Contact objects
     */
    List<Contact> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Contact object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Contact to be returned
     * @return The Contact with the specified id
     */
    Contact findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Contact object, specified by a String email.</p>
     *
     * <p>If there is more than one Contact with the specified email, only the first encountered will be returned.<p/>
     * 
     * @param email The email field of the Contact to be returned
     * @return The first Contact with the specified email
     */
    Contact findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * <p>Returns a single Contact object, specified by a String firstName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param firstName The firstName field of the Contact to be returned
     * @return The first Contact with the specified firstName
     */
    Contact findByFirstName(String firstName) {
        return crud.findByFirstName(firstName);
    }

    /**
     * <p>Returns a single Contact object, specified by a String lastName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param lastName The lastName field of the Contact to be returned
     * @return The first Contact with the specified lastName
     */
    Contact findByLastName(String lastName) {
        return crud.findByFirstName(lastName);
    }

    /**
     * <p>Writes the provided Contact object to the application database.<p/>
     *
     * <p>Validates the data in the provided Contact object using a {@link ContactValidator} object.<p/>
     * 
     * @param contact The Contact object to be written to the database using a {@link ContactRepository} object
     * @return The Contact object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Contact create(Contact contact) throws ConstraintViolationException, ValidationException, Exception {
        log.info("ContactService.create() - Creating " + contact.getFirstName() + " " + contact.getLastName());
        
        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateContact(contact);

        //Perform a rest call to get the state of the contact from the allareacodes.com API
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", contact.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        contact.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);


        // Write the contact to the database.
        return crud.create(contact);
    }

    /**
     * <p>Updates an existing Contact object in the application database with the provided Contact object.<p/>
     *
     * <p>Validates the data in the provided Contact object using a ContactValidator object.<p/>
     * 
     * @param contact The Contact object to be passed as an update to the application database
     * @return The Contact object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Contact update(Contact contact) throws ConstraintViolationException, ValidationException, Exception {
        log.info("ContactService.update() - Updating " + contact.getFirstName() + " " + contact.getLastName());
        
        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateContact(contact);

        //Perform a rest call to get the state of the contact from the allareacodes.com API
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", contact.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        contact.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);

        // Either update the contact or add it if it can't be found.
        return crud.update(contact);
    }

    /**
     * <p>Deletes the provided Contact object from the application database if found there.<p/>
     * 
     * @param contact The Contact object to be removed from the application database
     * @return The Contact object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Contact delete(Contact contact) throws Exception {
        log.info("ContactService.delete() - Deleting " + contact.getFirstName() + " " + contact.getLastName());
        
        Contact deletedContact = null;
        
        if (contact.getId() != null) {
            deletedContact = crud.delete(contact);
        } else {
            log.info("ContactService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedContact;
    }

}

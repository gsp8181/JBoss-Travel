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
    TravelPlan create(TravelPlan travelPlan) throws ConstraintViolationException, ValidationException, Exception {
        log.info("TravelPlanService.create() - Creating " + travelPlan.getFirstName() + " " + travelPlan.getLastName());
        
        // Check to make sure the data fits with the parameters in the TravelPlan model and passes validation.
        validator.validateTravelPlan(travelPlan);

        //Perform a rest call to get the state of the travelPlan from the allareacodes.com API
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.allareacodes.com")
                .setPath("/api/1.0/api.json")
                .setParameter("npa", travelPlan.getPhoneNumber().substring(1,4))
                .setParameter("tracking_email", "h.firth@ncl.ac.uk")
                .setParameter("tracking_url", "http://www.ncl.ac.uk/undergraduate/modules/module/CSC8104")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray areaCodes = responseJson.getJSONArray("area_codes");
        travelPlan.setState(areaCodes.getJSONObject(0).getString("state"));
        HttpClientUtils.closeQuietly(response);


        // Write the travelPlan to the database.
        return crud.create(travelPlan);
    }

    /**
     * <p>Deletes the provided TravelPlan object from the application database if found there.<p/>
     * 
     * @param travelPlan The TravelPlan object to be removed from the application database
     * @return The TravelPlan object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    TravelPlan delete(TravelPlan travelPlan) throws Exception {
        log.info("TravelPlanService.delete() - Deleting " + travelPlan.getFirstName() + " " + travelPlan.getLastName());
        
        TravelPlan deletedTravelPlan = null;
        
        if (travelPlan.getId() != null) {
            deletedTravelPlan = crud.delete(travelPlan);
        } else {
            log.info("TravelPlanService.delete() - No ID was found so can't Delete.");
        }
        
        return deletedTravelPlan;
    }

}

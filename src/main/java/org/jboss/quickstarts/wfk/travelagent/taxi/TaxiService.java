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
package org.jboss.quickstarts.wfk.travelagent.taxi;


import java.net.URI;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.quickstarts.wfk.contact.Contact;
import org.jboss.quickstarts.wfk.hotel.HotelRepository;
import org.jboss.quickstarts.wfk.hotel.HotelValidator;
import org.json.JSONArray;
import org.json.JSONObject;

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
 * @see HotelValidator
 * @see HotelRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class TaxiService {

    @Inject
    private @Named("logger") Logger log;
	
    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Contact} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of Contact objects
     */
    JSONArray findAllOrderedByName() {
    	try
    	{
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("jbosscontactsangularjs-110060653.rhcloud.com")
                .setPath("/rest/taxis")
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONArray responseJson = new JSONArray(responseBody);
        
        HttpClientUtils.closeQuietly(response);
        return responseJson;
    	} catch (Exception e) {
    	log.info(e.toString());
    	return null;
    	}
    }

	JSONObject findById(Long id) {
		
    	try
    	{
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("jbosscontactsangularjs-110060653.rhcloud.com")
                .setPath("/rest/taxis/id/" + id.toString())
                //.setParameter("id", id.toString())
                .build();
        HttpGet req = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(req);
        String responseBody = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(responseBody);
        
        HttpClientUtils.closeQuietly(response);
        return responseJson;
    	} catch (Exception e) {
    	log.info(e.toString());
    	return null;
    	}
    	
	}
}
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
package org.jboss.quickstarts.wfk.hotel;


import org.apache.http.impl.client.CloseableHttpClient;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

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
 * @see HotelValidator
 * @see HotelRepository
 */

//@Dependent annotation designates the default scope, listed here so that you know what scope is being used.
@Dependent
public class HotelService {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private HotelValidator validator;

    @Inject
    private HotelRepository crud;

    @Inject
    private @Named("httpClient") CloseableHttpClient httpClient;
    
    /**
     * <p>Returns a List of all persisted {@link Hotel} objects, sorted alphabetically by last name.<p/>
     * 
     * @return List of Hotel objects
     */
    List<Hotel> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     * 
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    Hotel findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Hotel object, specified by a String email.</p>
     *
     * <p>If there is more than one Hotel with the specified email, only the first encountered will be returned.<p/>
     * 
     * @param email The email field of the Hotel to be returned
     * @return The first Hotel with the specified email
     */
    /*Hotel findByEmail(String email) {
        return crud.findByEmail(email);
    }*/

    /**
     * <p>Returns a single Hotel object, specified by a String name.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param name The firstName field of the Hotel to be returned
     * @return The first Hotel with the specified name
     */
    Hotel findByFirstName(String name) {
        return crud.findByName(name);
    }

    /**
     * <p>Returns a single Hotel object, specified by a String lastName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     * 
     * @param lastName The lastName field of the Hotel to be returned
     * @return The first Hotel with the specified lastName
     */
    /*Hotel findByLastName(String lastName) {
        return crud.findByFirstName(lastName);
    }*/

    /**
     * <p>Writes the provided Hotel object to the application database.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a {@link HotelValidator} object.<p/>
     * 
     * @param Hotel The Hotel object to be written to the database using a {@link HotelRepository} object
     * @return The Hotel object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel create(Hotel Hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelService.create() - Creating " + Hotel.getName());
        
        // Check to make sure the data fits with the parameters in the Hotel model and passes validation.
        validator.validateHotel(Hotel);


        // Write the Hotel to the database.
        return crud.create(Hotel);
    }

    /**
     * <p>Updates an existing Hotel object in the application database with the provided Hotel object.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a HotelValidator object.<p/>
     * 
     * @param Hotel The Hotel object to be passed as an update to the application database
     * @return The Hotel object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel update(Hotel Hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelService.update() - Updating " + Hotel.getName());
        
        // Check to make sure the data fits with the parameters in the Hotel model and passes validation.
        validator.validateHotel(Hotel);

        // Either update the Hotel or add it if it can't be found.
        return crud.update(Hotel);
    }

}

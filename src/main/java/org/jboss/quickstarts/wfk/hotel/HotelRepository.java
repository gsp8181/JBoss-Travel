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

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link HotelService} with the
 * Domain/Entity Object (see {@link Hotel}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Joshua Wilson
 * @see Hotel
 * @see javax.persistence.EntityManager
 */
public class HotelRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link Hotel} objects, sorted alphabetically by last name.</p>
     * 
     * @return List of Hotel objects
     */
    List<Hotel> findAllOrderedByName() {
        TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_ALL, Hotel.class); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     *
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    Hotel findById(Long id) {
        return em.find(Hotel.class, id);
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
        TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_BY_EMAIL, Hotel.class).setParameter("email", email); 
        return query.getSingleResult();
    }*/

    /**
     * <p>Returns a single Hotel object, specified by a String name.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param name The name field of the Hotel to be returned
     * @return The first Hotel with the specified name
     */
    Hotel findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hotel> criteria = cb.createQuery(Hotel.class);
        Root<Hotel> Hotel = criteria.from(Hotel.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(Hotel).where(cb.equal(Hotel.get(Hotel_.name), name));
        criteria.select(Hotel).where(cb.equal(Hotel.get("name"), name));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Persists the provided Hotel object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param Hotel The Hotel object to be persisted
     * @return The Hotel object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel create(Hotel Hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelRepository.create() - Creating " + Hotel.getName());
        
        // Write the Hotel to the database.
        em.persist(Hotel);
        
        return Hotel;
    }

    /**
     * <p>Updates an existing Hotel object in the application database with the provided Hotel object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param Hotel The Hotel object to be merged with an existing Hotel
     * @return The Hotel that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Hotel update(Hotel Hotel) throws ConstraintViolationException, ValidationException, Exception {
        log.info("HotelRepository.update() - Updating " + Hotel.getName());
        
        // Either update the Hotel or add it if it can't be found.
        em.merge(Hotel);
        
        return Hotel;
    }

}

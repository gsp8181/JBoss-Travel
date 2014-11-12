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
 * <p>This is a Repository class and connects the Service/Control layer (see {@link TravelPlanService} with the
 * Domain/Entity Object (see {@link TravelPlan}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Geoffrey Prytherch
 * @see TravelPlan
 * @see javax.persistence.EntityManager
 */
public class TravelPlanRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link TravelPlan} objects, sorted alphabetically by last name.</p>
     * 
     * @return List of TravelPlan objects
     */
    List<TravelPlan> findAllOrderedByName() {
        TypedQuery<TravelPlan> query = em.createNamedQuery(TravelPlan.FIND_ALL, TravelPlan.class); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single TravelPlan object, specified by a Long id.<p/>
     *
     * @param id The id field of the TravelPlan to be returned
     * @return The TravelPlan with the specified id
     */
    TravelPlan findById(Long id) {
        return em.find(TravelPlan.class, id);
    }

    /**
     * <p>Returns a single TravelPlan object, specified by a String email.</p>
     *
     * <p>If there is more than one TravelPlan with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the TravelPlan to be returned
     * @return The first TravelPlan with the specified email
     */
    TravelPlan findByEmail(String email) {
        TypedQuery<TravelPlan> query = em.createNamedQuery(TravelPlan.FIND_BY_EMAIL, TravelPlan.class).setParameter("email", email); 
        return query.getSingleResult();
    }

    /**
     * <p>Returns a single TravelPlan object, specified by a String firstName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param firstName The firstName field of the TravelPlan to be returned
     * @return The first TravelPlan with the specified firstName
     */
    TravelPlan findByFirstName(String firstName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TravelPlan> criteria = cb.createQuery(TravelPlan.class);
        Root<TravelPlan> travelPlan = criteria.from(TravelPlan.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(travelPlan).where(cb.equal(travelPlan.get(TravelPlan_.firstName), firstName));
        criteria.select(travelPlan).where(cb.equal(travelPlan.get("firstName"), firstName));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Returns a single TravelPlan object, specified by a String lastName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param lastName The lastName field of the TravelPlan to be returned
     * @return The first TravelPlan with the specified lastName
     */
    TravelPlan findByLastName(String lastName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TravelPlan> criteria = cb.createQuery(TravelPlan.class);
        Root<TravelPlan> travelPlan = criteria.from(TravelPlan.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(travelPlan).where(cb.equal(travelPlan.get(TravelPlan_.lastName), lastName));
        criteria.select(travelPlan).where(cb.equal(travelPlan.get("lastName"), lastName));
        return em.createQuery(criteria).getSingleResult();
    }

    /**
     * <p>Persists the provided TravelPlan object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param travelPlan The TravelPlan object to be persisted
     * @return The TravelPlan object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    TravelPlan create(TravelPlan travelPlan) throws ConstraintViolationException, ValidationException, Exception {
        log.info("TravelPlanRepository.create() - Creating " + travelPlan.getFirstName() + " " + travelPlan.getLastName());
        
        // Write the travelPlan to the database.
        em.persist(travelPlan);
        
        return travelPlan;
    }

    /**
     * <p>Updates an existing TravelPlan object in the application database with the provided TravelPlan object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param travelPlan The TravelPlan object to be merged with an existing TravelPlan
     * @return The TravelPlan that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    TravelPlan update(TravelPlan travelPlan) throws ConstraintViolationException, ValidationException, Exception {
        log.info("TravelPlanRepository.update() - Updating " + travelPlan.getFirstName() + " " + travelPlan.getLastName());
        
        // Either update the travelPlan or add it if it can't be found.
        em.merge(travelPlan);
        
        return travelPlan;
    }

    /**
     * <p>Deletes the provided TravelPlan object from the application database if found there</p>
     *
     * @param travelPlan The TravelPlan object to be removed from the application database
     * @return The TravelPlan object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    TravelPlan delete(TravelPlan travelPlan) throws Exception {
        log.info("TravelPlanRepository.delete() - Deleting " + travelPlan.getFirstName() + " " + travelPlan.getLastName());
        
        if (travelPlan.getId() != null) {
            /*
             * The Hibernate session (aka EntityManager's persistent context) is closed and invalidated after the commit(), 
             * because it is bound to a transaction. The object goes into a detached status. If you open a new persistent 
             * context, the object isn't known as in a persistent state in this new context, so you have to merge it. 
             * 
             * Merge sees that the object has a primary key (id), so it knows it is not new and must hit the database 
             * to reattach it. 
             * 
             * Note, there is NO remove method which would just take a primary key (id) and a entity class as argument. 
             * You first need an object in a persistent state to be able to delete it.
             * 
             * Therefore we merge first and then we can remove it.
             */
            em.remove(em.merge(travelPlan));
            
        } else {
            log.info("TravelPlanRepository.delete() - No ID was found so can't Delete.");
        }
        
        return travelPlan;
    }

}

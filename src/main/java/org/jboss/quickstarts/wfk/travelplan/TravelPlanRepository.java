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
     * <p>Returns a List of all persisted {@link TravelPlan} objects, sorted alphabetically by id.</p>
     * 
     * @return List of TravelPlan objects
     */
    List<TravelPlan> findAll() {
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
        log.info("TravelPlanRepository.create() - Creating TravelPlan for " + travelPlan.getCustomerId());
        
        // Write the travelPlan to the database.
        em.persist(travelPlan);
        
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
        log.info("TravelPlanRepository.delete() - Cancelling TravelPlan #" + travelPlan.getId());
        
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

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
package org.jboss.quickstarts.wfk.booking;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.jboss.quickstarts.wfk.contact.Contact;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingService} with the
 * Domain/Entity Object (see {@link Booking}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 * 
 * @author Geoffrey Prytherch
 * @see Booking
 * @see javax.persistence.EntityManager
 */
public class BookingRepository {

    @Inject
    private @Named("logger") Logger log;

    @Inject
    private EntityManager em;
    
    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.</p>
     * 
     * @return List of Booking objects
     */
    List<Booking> findAllOrderedByIds() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class); 
        return query.getResultList();
    }

    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    Booking findById(Long id) {
        return em.find(Booking.class, id);
    }
    
    /**
     * <p>Returns a single Booking object, specified by a Long hotelId and Date bookingDate.</p>
     *
     * <p>If there is more than one Contact with the specified email, only the first encountered will be returned.<p/>
     *
     * @param hotelId The hotelId field of the Booking to be returned
     * @param bookingDate The bookingDate field of the Booking to be returned
     * @return The first Booking with the specified fields
     */
    Booking findByIdAndDate(Long hotelId, Date bookingDate) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_ID_AND_DATE, Booking.class);
        query = query.setParameter("hotelId", hotelId);
        query = query.setParameter("bookingDate", bookingDate);
        return query.getSingleResult();
    }

    /**
     * <p>Returns a single Booking object, specified by a String firstName.<p/>
     *
     * <p>If there is more then one, only the first will be returned.<p/>
     *
     * @param firstName The firstName field of the Booking to be returned
     * @return The first Booking with the specified firstName
     */
    List<Booking> findByCustomerId(long customerId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new feature in JPA 2.0.
        // criteria.select(booking).where(cb.equal(booking.get(Booking_.firstName), firstName));
        criteria.select(booking).where(cb.equal(booking.get("customerId"), customerId));
        return em.createQuery(criteria).getResultList();
    }

    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * <p>{@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking create(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.create() - Creating " + booking.getCustomerId() + " customer booking hotel: " + booking.getHotelId());
        
        // Write the booking to the database.
        em.persist(booking);
        
        return booking;
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.</p>
     * 
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     * 
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     * 
     * @param booking The Booking object to be merged with an existing Booking
     * @return The Booking that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking update(Booking booking) throws ConstraintViolationException, ValidationException, Exception {
        log.info("BookingRepository.update() - Updating booking number " + booking.getId());
        
        // Either update the booking or add it if it can't be found.
        em.merge(booking);
        
        return booking;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Booking delete(Booking booking) throws Exception {
        log.info("BookingRepository.delete() - Deleting booking number " + booking.getId());
        
        if (booking.getId() != null) {
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
            em.remove(em.merge(booking));
            
        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }
        
        return booking;
    }

}

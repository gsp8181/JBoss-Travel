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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.jboss.quickstarts.wfk.customer.Customer;
import org.jboss.quickstarts.wfk.customer.CustomerRepository;
import org.jboss.quickstarts.wfk.hotel.Hotel;
import org.jboss.quickstarts.wfk.hotel.HotelRepository;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 * 
 * @author Geoffrey Prytherch
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
public class BookingValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud;
    
    @Inject 
    private CustomerRepository custRep;
    
    @Inject 
    private HotelRepository hotelRep;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing booking with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If booking with the same email already exists
     */
    void validateBooking(Booking booking) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        if (bookingAlreadyExists(booking.getHotelId(), booking.getBookingDate(), booking.getId())) {
            throw new ValidationException("Date/Hotel combination already exists");
        }
        
        // Make sure there is an associated Customer to the provided customerId
        if(!customerExists(booking.getCustomerId()))
        {
        	throw new ValidationException("Customer ID does not exist in the database");
        }
        
        // Make sure there is an associated Hotel to the provided hotelId
        if(!hotelExists(booking.getHotelId()))
        {
        	throw new ValidationException("Hotel ID does not exist in the database");
        }
    }

    /**
     * <p>Checks if a booking with the same hotelId and bookingDate is already registered.</p>
     * 
     * @param hotelId The hotelId of the new Booking
     * @param bookingDate The date of the new Booking
     * @param id The user id to check the parameters against if it was found
     * @return boolean which represents whether the combination of parameters was found, and if so if it belongs to the user with id
     */
    boolean bookingAlreadyExists(Long hotelId, Date bookingDate, Long id) {
        Booking booking = null;
        Booking bookingWithID = null;
        try {
            booking = crud.findByIdAndDate(hotelId, bookingDate);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
                bookingWithID = crud.findById(id);
                if (bookingWithID != null && bookingWithID.getHotelId().equals(hotelId) && bookingWithID.getBookingDate().equals(bookingDate)) {
                    booking = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
    
    /**
     *
     * <p>Checks if a booking with the same customerId is already registered. </p>
     * 
     * @param customerId The customerId to check is unique
     * @return boolean which represents whether the booking was already found, and if so, whether it belongs to the user with the given ID
     */
    boolean customerExists(Long customerId) {
        Customer booking = null;
        try {
            booking = custRep.findById(customerId);
        } catch (NoResultException e) {
            // ignore
        }
        return booking != null;
    }
    
    /**
    *
    * <p>Checks if a booking with the same hotelId is already registered. </p>
    * 
    * @param customerId The hotelId to check is unique
    * @return boolean which represents whether the booking was already found, and if so, whether it belongs to the user with the given ID
    */
    boolean hotelExists(Long hotelId) {
        Hotel booking = null;
        try {
            booking = hotelRep.findById(hotelId);
        } catch (NoResultException e) {
            // ignore
        }
        return booking != null;
    }
}

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
package org.jboss.quickstarts.wfk.booking;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 * 
 * @author Joshua Wilson
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
public class BookingValidator {
    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud;

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
        if (emailAlreadyExists(booking.getEmail(), booking.getId())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    /**
     * <p>Checks if a booking with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Booking class.</p>
     * 
     * <p>Since Update will being using an email that is already in the database we need to make sure that it is the email
     * from the record being updated.</p>
     * 
     * @param email The email to check is unique
     * @param id The user id to check the email against if it was found
     * @return boolean which represents whether the email was found, and if so if it belongs to the user with id
     */
    boolean emailAlreadyExists(String email, Long id) {
        Booking booking = null;
        Booking bookingWithID = null;
        try {
            booking = crud.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
                bookingWithID = crud.findById(id);
                if (bookingWithID != null && bookingWithID.getEmail().equals(email)) {
                    booking = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
}

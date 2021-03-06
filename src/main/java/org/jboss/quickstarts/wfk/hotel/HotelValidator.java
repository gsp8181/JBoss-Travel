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
package org.jboss.quickstarts.wfk.hotel;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

/**
 * <p>This class provides methods to check Hotel objects against arbitrary requirements.</p>
 * 
 * @author Geoffrey Prytherch
 * @see Hotel
 * @see HotelRepository
 * @see javax.validation.Validator
 */
public class HotelValidator {
    @Inject
    private Validator validator;

    @Inject
    private HotelRepository crud;

    /**
     * <p>Validates the given Hotel object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing Hotel with the same phone number is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param Hotel The Hotel object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If Hotel with the same phone number already exists
     */
    void validateHotel(Hotel Hotel) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Hotel>> violations = validator.validate(Hotel);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the phone number
        if (phoneNumberAlreadyExists(Hotel.getPhoneNumber(), Hotel.getId())) {
            throw new ValidationException("Unique Phone Number Violation");
        }
    }

    /**
     * <p>Checks if a Hotel with the same phone number is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "phoneNumber")" constraint from the Hotel class.</p>
     * 
     * <p>Since Update will being using a phone number that is already in the database we need to make sure that it is the phone number
     * from the record being updated.</p>
     * 
     * @param phoneNumber The phone number to check is unique
     * @param id The user id to check the phone number against if it was found
     * @return boolean which represents whether the phone number was found, and if so if it belongs to the user with id
     */
    boolean phoneNumberAlreadyExists(String phoneNumber, Long id) {
        Hotel Hotel = null;
        Hotel HotelWithID = null;
        try {
            Hotel = crud.findByPhoneNumber(phoneNumber);
        } catch (NoResultException e) {
            // ignore
        }

        if (Hotel != null && id != null) {
            try {
                HotelWithID = crud.findById(id);
                if (HotelWithID != null && HotelWithID.getPhoneNumber().equals(phoneNumber)) {
                    Hotel = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return Hotel != null;
    }
}

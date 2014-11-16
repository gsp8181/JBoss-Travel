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
package org.jboss.quickstarts.wfk.travelagent.travelplan;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;

/**
 * <p>This class provides methods to check TravelPlan objects against arbitrary requirements.</p>
 * 
 * @author Geoffrey Prytherch
 * @see TravelPlan
 * @see TravelPlanRepository
 * @see javax.validation.Validator
 */
public class TravelPlanValidator {
    @Inject
    private Validator validator;

    @Inject
    private TravelPlanRepository crud;

    /**
     * <p>Validates the given TravelPlan object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing travelPlan with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     * 
     * @param travelPlan The TravelPlan object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If travelPlan with the same email already exists
     */
    void validateTravelPlan(TravelPlan travelPlan) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<TravelPlan>> violations = validator.validate(travelPlan);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Check the uniqueness of the email address
        //if (emailAlreadyExists(travelPlan.getEmail(), travelPlan.getId())) {
        //    throw new ValidationException("Unique Email Violation");
        //}
    }

    /**
     * <p>Checks if a travelPlan with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the TravelPlan class.</p>
     * 
     * <p>Since Update will being using an email that is already in the database we need to make sure that it is the email
     * from the record being updated.</p>
     * 
     * @param email The email to check is unique
     * @param id The user id to check the email against if it was found
     * @return boolean which represents whether the email was found, and if so if it belongs to the user with id
     */
    /*boolean emailAlreadyExists(String email, Long id) {
        TravelPlan travelPlan = null;
        TravelPlan travelPlanWithID = null;
        try {
            travelPlan = crud.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }

        if (travelPlan != null && id != null) {
            try {
                travelPlanWithID = crud.findById(id);
                if (travelPlanWithID != null && travelPlanWithID.getEmail().equals(email)) {
                    travelPlan = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return travelPlan != null;
    	return false;
    }*/
}

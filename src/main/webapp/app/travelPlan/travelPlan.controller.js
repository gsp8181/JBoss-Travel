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
(function() {
    'use strict';
    angular
        .module('app.travelPlan')
        .controller('TravelPlanController', TravelPlanController);

    TravelPlanController.$inject = ['$scope', '$routeParams', '$location', 'TravelPlan', 'messageBag'];

    function TravelPlanController($scope, $routeParams, $location, TravelPlan, messageBag) {
        //Assign TravelPlan service to $scope variable
        $scope.travelPlans = TravelPlan;
        //Assign messageBag service to $scope variable
        $scope.messages = messageBag;

        //Get today's date for the birthDate form value max
        $scope.date = Date.now();

        $scope.travelPlan = {};
        $scope.create = true;

        //If $routeParams has :travelPlanId then load the specified travelPlan, and display edit controls on travelPlanForm
        if($routeParams.hasOwnProperty('travelPlanId')) {
            $scope.travelPlan = $scope.travelPlans.get({travelPlanId: $routeParams.travelPlanId});
            $scope.create = false;
        }


        // Define a reset function, that clears the prototype new TravelPlan object, and
        // consequently, the form
        $scope.reset = function() {
            // Sets the form to it's pristine state
            if($scope.travelPlanForm) {
                $scope.travelPlanForm.$setPristine();
            }

            // Clear input fields. If $scope.travelPlan was set to an empty object {},
            // then invalid form values would not be reset.
            // By specifying all properties, input fields with invalid values are also reset.
            $scope.travelPlan = {customerId: "", hotelId: "", flightId: "", taxiId: "", bookingDate: ""};

            // clear messages
            $scope.messages.clear();
        };

        // Define an addTravelPlan() function, which creates a new travelPlan via the REST service,
        // using those details provided and displaying any error messages
        $scope.addTravelPlan = function() {
            $scope.messages.clear();

            $scope.travelPlans.save($scope.travelPlan,
                //Successful query
                function(data) {

                    // Update the list of travelPlans
                    $scope.travelPlans.data.push(data);

                    // Clear the form
                    $scope.reset();

                    //Add success message
                    $scope.messages.push('success', 'TravelPlan added');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };

        // Define a saveTravelPlan() function, which saves the current travelPlan using the REST service
        // and displays any error messages
        $scope.saveTravelPlan = function() {
            $scope.messages.clear();
            $scope.travelPlan.$update(
                //Successful query
                function(data) {
                    //Find the travelPlan locally by id and update it
                    var idx = _.findIndex($scope.travelPlans.data, {'id': $scope.travelPlan.id});
                    $scope.travelPlans.data[idx] = data;
                    //Add success message
                    $scope.messages.push('success', 'TravelPlan saved');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            )
        };

        // Define a deleteTravelPlan() function, which saves the current travelPlan using the REST service
        // and displays any error messages
        $scope.deleteTravelPlan = function() {
            $scope.messages.clear();

            //Send the DELETE request
            $scope.travelPlan.$delete(
                //Successful query
                function() {
                    //TODO: Fix the wonky imitation of a cache by replacing with a proper $cacheFactory cache.
                    //Find the travelPlan locally by id and remove it
                    var idx = _.findIndex($scope.travelPlans.data, {'id': $scope.travelPlan.id});
                    $scope.travelPlans.data.splice(idx, 1);
                    //Mark success on the editTravelPlan form
                    $scope.messages.push('success', 'TravelPlan removed');
                    //Redirect back to /home
                    $location.path('/agent');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };
    }
})();
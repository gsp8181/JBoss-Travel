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
        .module('app.hotel')
        .controller('HotelController', HotelController);

    HotelController.$inject = ['$scope', '$routeParams', '$location', 'Hotel', 'messageBag'];

    function HotelController($scope, $routeParams, $location, Hotel, messageBag) {
        //Assign Hotel service to $scope variable
        $scope.hotels = Hotel;
        //Assign messageBag service to $scope variable
        $scope.messages = messageBag;

        //Get today's date for the birthDate form value max
        $scope.date = Date.now();

        $scope.hotel = {};
        $scope.create = true;

        //If $routeParams has :hotelId then load the specified hotel, and display edit controls on hotelForm
        if($routeParams.hasOwnProperty('hotelId')) {
            $scope.hotel = $scope.hotels.get({hotelId: $routeParams.hotelId});
            $scope.create = false;
        }


        // Define a reset function, that clears the prototype new Hotel object, and
        // consequently, the form
        $scope.reset = function() {
            // Sets the form to it's pristine state
            if($scope.hotelForm) {
                $scope.hotelForm.$setPristine();
            }

            // Clear input fields. If $scope.hotel was set to an empty object {},
            // then invalid form values would not be reset.
            // By specifying all properties, input fields with invalid values are also reset.
            $scope.hotel = {name: "", postcode: "", phoneNumber: ""};

            // clear messages
            $scope.messages.clear();
        };

        // Define an addHotel() function, which creates a new hotel via the REST service,
        // using those details provided and displaying any error messages
        $scope.addHotel = function() {
            $scope.messages.clear();

            $scope.hotels.save($scope.hotel,
                //Successful query
                function(data) {

                    // Update the list of hotels
                    $scope.hotels.data.push(data);

                    // Clear the form
                    $scope.reset();

                    //Add success message
                    $scope.messages.push('success', 'Hotel added');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };

        // Define a saveHotel() function, which saves the current hotel using the REST service
        // and displays any error messages
        $scope.saveHotel = function() {
            $scope.messages.clear();
            $scope.hotel.$update(
                //Successful query
                function(data) {
                    //Find the hotel locally by id and update it
                    var idx = _.findIndex($scope.hotels.data, {'id': $scope.hotel.id});
                    $scope.hotels.data[idx] = data;
                    //Add success message
                    $scope.messages.push('success', 'Hotel saved');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            )
        };
    }
})();
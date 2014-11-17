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
        .module('app.bookingList')
        .controller('BookingListController', BookingListController);

    BookingListController.$inject = ['$scope', '$filter', 'Booking', 'messageBag'];

    function BookingListController($scope, $filter, Booking, messageBag) {
        //Assign Booking service to $scope variable
        $scope.bookings = Booking;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide booking list into several sub lists according to the first character of their name property
        var getHeadings = function(bookings) {
            var headings = {};
            for(var i = 0; i<bookings.length; i++) {
                //Get the first letter of a customer's firstName
                /*var startsWithLetter = bookings[i].name.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the booking to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(bookings[i]);
                } else {
                    headings[startsWithLetter] = [bookings[i]];
                }*/
            	//Get the customer ID
            	var customerId = bookings[i].customer.id + "  " + bookings[i].customer.name;
                //If we have encountered that customer id before then add the booking to that list, else create it
                if(headings.hasOwnProperty(customerId)) {
                    headings[customerId].push(bookings[i]);
                } else {
                    headings[customerId] = [bookings[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of Bookings and their letter headings
        $scope.bookings.data = $scope.bookings.query(
            //Successful query
            function(data) {
                $scope.bookings.data = data;
                $scope.bookingsList = getHeadings($scope.bookings.data);
                //Keep the bookings list headings in sync with the underlying bookings
                $scope.$watchCollection('bookings.data', function(newBookings, oldBookings) {
                    $scope.bookingsList = getHeadings(newBookings);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the bookings are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the bookings list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.bookingsList = getHeadings($filter('filter')($scope.bookings.data, $scope.search));
        });
    }
})();
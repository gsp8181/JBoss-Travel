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
        .module('app.travelPlanList')
        .controller('TravelPlanListController', TravelPlanListController);

    TravelPlanListController.$inject = ['$scope', '$filter', 'TravelPlan', 'messageBag'];

    function TravelPlanListController($scope, $filter, TravelPlan, messageBag) {
        //Assign TravelPlan service to $scope variable
        $scope.travelPlans = TravelPlan;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide travelPlan list into several sub lists according to the first character of their name property
        var getHeadings = function(travelPlans) {
            var headings = {};
            for(var i = 0; i<travelPlans.length; i++) {
                //Get the first letter of a customer's firstName
                /*var startsWithLetter = travelPlans[i].name.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the travelPlan to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(travelPlans[i]);
                } else {
                    headings[startsWithLetter] = [travelPlans[i]];
                }*/
            	//Get the customer ID
            	var customerId = travelPlans[i].customer.id + "  " + travelPlans[i].customer.name;
                //If we have encountered that customer id before then add the travelPlan to that list, else create it
                if(headings.hasOwnProperty(customerId)) {
                    headings[customerId].push(travelPlans[i]);
                } else {
                    headings[customerId] = [travelPlans[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of TravelPlans and their letter headings
        $scope.travelPlans.data = $scope.travelPlans.query(
            //Successful query
            function(data) {
                $scope.travelPlans.data = data;
                $scope.travelPlansList = getHeadings($scope.travelPlans.data);
                //Keep the travelPlans list headings in sync with the underlying travelPlans
                $scope.$watchCollection('travelPlans.data', function(newTravelPlans, oldTravelPlans) {
                    $scope.travelPlansList = getHeadings(newTravelPlans);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the travelPlans are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the travelPlans list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.travelPlansList = getHeadings($filter('filter')($scope.travelPlans.data, $scope.search));
        });
    }
})();
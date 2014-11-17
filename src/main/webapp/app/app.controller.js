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
        .module('app')
        .controller('AppController', AppController);

    AppController.$inject = ['$scope', '$filter', 'Customer', 'messageBag'];

    function AppController($scope, $filter, Customer, messageBag) {
        //Assign Customer service to $scope variable
        $scope.customers = Customer;
        //Assign Messages service to $scope variable
        $scope.messages = messageBag;

        //Divide customer list into several sub lists according to the first character of their firstName property
        var getHeadings = function(customers) {
            var headings = {};
            for(var i = 0; i<customers.length; i++) {
                //Get the first letter of a customer's firstName
                var startsWithLetter = customers[i].name.charAt(0).toUpperCase();
                //If we have encountered that first letter before then add the customer to that list, else create it
                if(headings.hasOwnProperty(startsWithLetter)) {
                    headings[startsWithLetter].push(customers[i]);
                } else {
                    headings[startsWithLetter] = [customers[i]];
                }
            }
            return headings;
        };

        //Upon initial loading of the controller, populate a list of Customers and their letter headings
        $scope.customers.data = $scope.customers.query(
            //Successful query
            function(data) {
                $scope.customers.data = data;
                $scope.customersList = getHeadings($scope.customers.data);
                //Keep the customers list headings in sync with the underlying customers
                $scope.$watchCollection('customers.data', function(newCustomers, oldCustomers) {
                    $scope.customersList = getHeadings(newCustomers);
                });
            },
            //Error
            function(result) {
                for(var error in result.data){
                    $scope.messages.push('danger', result.data[error]);
                }
            }
        );

        //Boolean flag representing whether the details of the customers are expanded inline
        $scope.details = false;

        //Default search string
        $scope.search = "";

        //Continuously filter the content of the customers list according to the contents of $scope.search
        $scope.$watch('search', function(newValue, oldValue) {
            $scope.customersList = getHeadings($filter('filter')($scope.customers.data, $scope.search));
        });
    }
})();
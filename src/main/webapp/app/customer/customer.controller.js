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
(function() {
    'use strict';
    angular
        .module('app.customer')
        .controller('CustomerController', CustomerController);

    CustomerController.$inject = ['$scope', '$routeParams', '$location', 'Customer', 'messageBag'];

    function CustomerController($scope, $routeParams, $location, Customer, messageBag) {
        //Assign Customer service to $scope variable
        $scope.customers = Customer;
        //Assign messageBag service to $scope variable
        $scope.messages = messageBag;

        //Get today's date for the birthDate form value max
        $scope.date = Date.now();

        $scope.customer = {};
        $scope.create = true;

        //If $routeParams has :customerId then load the specified customer, and display edit controls on customerForm
        if($routeParams.hasOwnProperty('customerId')) {
            $scope.customer = $scope.customers.get({customerId: $routeParams.customerId});
            $scope.create = false;
        }


        // Define a reset function, that clears the prototype new Customer object, and
        // consequently, the form
        $scope.reset = function() {
            // Sets the form to it's pristine state
            if($scope.customerForm) {
                $scope.customerForm.$setPristine();
            }

            // Clear input fields. If $scope.customer was set to an empty object {},
            // then invalid form values would not be reset.
            // By specifying all properties, input fields with invalid values are also reset.
            $scope.customer = {firstName: "", lastName: "", phoneNumber: "", email: "", birthDate: ""};

            // clear messages
            $scope.messages.clear();
        };

        // Define an addCustomer() function, which creates a new customer via the REST service,
        // using those details provided and displaying any error messages
        $scope.addCustomer = function() {
            $scope.messages.clear();

            $scope.customers.save($scope.customer,
                //Successful query
                function(data) {

                    // Update the list of customers
                    $scope.customers.data.push(data);

                    // Clear the form
                    $scope.reset();

                    //Add success message
                    $scope.messages.push('success', 'Customer added');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            );

        };

        // Define a saveCustomer() function, which saves the current customer using the REST service
        // and displays any error messages
        $scope.saveCustomer = function() {
            $scope.messages.clear();
            $scope.customer.$update(
                //Successful query
                function(data) {
                    //Find the customer locally by id and update it
                    var idx = _.findIndex($scope.customers.data, {'id': $scope.customer.id});
                    $scope.customers.data[idx] = data;
                    //Add success message
                    $scope.messages.push('success', 'Customer saved');
                    //Error
                }, function(result) {
                    for(var error in result.data){
                        $scope.messages.push('danger', result.data[error]);
                    }
                }
            )
        };

        // Define a deleteCustomer() function, which saves the current customer using the REST service
        // and displays any error messages
        $scope.deleteCustomer = function() {
            $scope.messages.clear();

            //Send the DELETE request
            $scope.customer.$delete(
                //Successful query
                function() {
                    //TODO: Fix the wonky imitation of a cache by replacing with a proper $cacheFactory cache.
                    //Find the customer locally by id and remove it
                    var idx = _.findIndex($scope.customers.data, {'id': $scope.customer.id});
                    $scope.customers.data.splice(idx, 1);
                    //Mark success on the editCustomer form
                    $scope.messages.push('success', 'Customer removed');
                    //Redirect back to /home
                    $location.path('/home');
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
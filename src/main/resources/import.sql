--
-- JBoss, Home of Professional Open Source
-- Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
-- contributors by the @authors tag. See the copyright.txt in the
-- distribution for a full listing of individual contributors.
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- You can use this file to load seed data into the database using SQL statements
-- Since the database doesn't know to increase the Sequence to match what is manually loaded here it starts at 1 and tries
--  to enter a record with the same PK and create an error.  If we use a high we don't interfere with the sequencing (at least until later).
-- NOTE: this file should be removed for production systems. 
insert into Contact (id, first_name, last_name, email, phone_number, birth_date, state) values (10001, 'John', 'Smith', 'john.smith@mailinator.com', '(212) 555-1212', '1963-06-03', 'NY')
insert into Contact (id, first_name, last_name, email, phone_number, birth_date, state) values (10002, 'Davey', 'Jones', 'davey.jones@locker.com', '(212) 555-3333', '1996-08-07', 'NY')
insert into Customer (id, name, email, phone_number) values (10001, 'John Smith', 'john.smith@cust.com', '(212) 555-1212')
insert into Customer (id, name, email, phone_number) values (10002, 'Davey Jones', 'davey.jones@cust.com', '(212) 555-3333')
insert into Hotel (id, name, postcode, phoneNumber) values (1027, 'MGM Grand', 'SW4 5AX', '02074450192') 
insert into Hotel (id, name, postcode, phoneNumber) values (1099, 'Marriot Liverpool', 'L1 9AZ', '01916662903')
insert into Booking (id, customerId, hotelId, bookingDate) values (1001, 10001, 1027, '2015-10-20')
insert into Booking (id, customerId, hotelId, bookingDate) values (1002, 10002, 1099, '2015-10-20')
insert into Booking (id, customerId, hotelId, bookingDate) values (1003, 10002, 1099, '2015-10-21')
--insert into TravelPlan (id, customerId, flightBookingId, hotelBookingId, taxiBookingId) values (501, 10002, 49, 5, 2)

-- TRAVEL AGENTS GO HERE DO NOT CHANGE!
insert into Customer (id, name, email, phone_number) values (10000, 'Don', 'd.daubaras@ncl.ac.uk', '07123456789')
insert into Customer (id, name, email, phone_number) values (18181, 'Geoffs Travel', 'g.prytherch@ncl.ac.uk', '01914960142')
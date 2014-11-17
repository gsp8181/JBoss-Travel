travel: JAX-RS Services Documentation 
=======================================================
Author: Geoffrey Prytherch

These services support various RESTFul end points which also includes JSONP support for cross domain requests.

By default the base URL for services is `/travel/rest`.

CustomerService End Points
------------------------
##CREATE
### Create a new customer

#### /rest/customers

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{"name": "John Smith", "phoneNumber": "01915552092", "email": "john.smith@newcastle.co.uk"}
```

* Response example:
* Success: 201 Created
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"email":"That email is already used, please use a unique email"}
```


##READ
### List all contacts
#### /rest/customers

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{"id": 14, "name": "John Smith", "phoneNumber": "01915552092", "email": "john.smith@newcastle.co.uk"},
 {"id": 15, "name": "Jane Smith", "phoneNumber": "01512235655", "email": "jane.smith@liverpool.co.uk"}]
```

### Find a contact by it's ID.
#### /rest/customers/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{"id": 15, "name": "Jane Smith", "phoneNumber": "01512235655", "email": "jane.smith@liverpool.co.uk"}
```


##UPDATE
### Edit one contact
#### /rest/customers/\<id>

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{"id": 15, "name": "Jane Smith", "phoneNumber": "01512235655", "email": "jane.smith@liverpool.co.uk"}
```


HotelService End Points
------------------------
##CREATE
### Create a new hotel

#### /rest/hotels

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{"name":"MGM Grand","postcode":"SW4 5AX","phoneNumber":"02074450192"}
```

* Response example:
* Success: 201 Created
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"phoneNumber":"That phone number is already used, please use a unique phone number"}
```


##READ
### List all hotels
#### /rest/hotels

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{"id":27,"name":"MGM Grand","postcode":"SW4 5AX","phoneNumber":"02074450192"},{"id":99,"name":"Marriot Liverpool","postcode":"L1 9AZ","phoneNumber":"01916662903"}]
```

### Find a hotel by it's ID.
#### /rest/hotels/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{"id":99,"name":"Marriot Liverpool","postcode":"L1 9AZ","phoneNumber":"01916662903"}
```


##UPDATE
### Edit one hotel
#### /rest/hotels

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{"id":99,"name":"Marriot Liverpool","postcode":"L1 9AZ","phoneNumber":"01916662903"}
```

BookingService End Points
------------------------
##CREATE
### Create a new booking

#### /rest/bookings

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{"customer":{"id":10001},"hotel":{"id":1027},"bookingDate":"2015-10-20"}
```

* Response example:
* Success: 201 Created
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"HotelID/Date":"That Hotel and Date combination is already used, please use a unique combination"}
```


##READ
### List all bookings
#### /rest/bookings

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{"id":1001,"customer":{"id":10001,"name":"John Smith","email":"john.smith@cust.com","phoneNumber":"(212) 555-1212"},"hotel":{"id":1027,"name":"MGM Grand","postcode":"SW4 5AX","phoneNumber":"02074450192"},"bookingDate":"2015-10-20"},{"id":1002,"customer":{"id":10002,"name":"Davey Jones","email":"davey.jones@cust.com","phoneNumber":"(212) 555-3333"},"hotel":{"id":1099,"name":"Marriot Liverpool","postcode":"L1 9AZ","phoneNumber":"01916662903"},"bookingDate":"2015-10-20"},{"id":1003,"customer":{"id":10002,"name":"Davey Jones","email":"davey.jones@cust.com","phoneNumber":"(212) 555-3333"},"hotel":{"id":1099,"name":"Marriot Liverpool","postcode":"L1 9AZ","phoneNumber":"01916662903"},"bookingDate":"2015-10-21"}]
```

### Find a list of bookings by it's associated customer ID.
#### /rest/bookings/customer/\<customerId>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{"id":1001,"customer":{"id":10001,"name":"John Smith","email":"john.smith@cust.com","phoneNumber":"(212) 555-1212"},"hotel":{"id":1027,"name":"MGM Grand","postcode":"SW4 5AX","phoneNumber":"02074450192"},"bookingDate":"2015-10-20"}
```


##UPDATE
### Edit one booking
#### /rest/bookings

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{"id":1001,"customer":{"id":10001,"name":"John Smith","email":"john.smith@cust.com","phoneNumber":"(212) 555-1212"},"hotel":{"id":1027,"name":"MGM Grand","postcode":"SW4 5AX","phoneNumber":"02074450192"},"bookingDate":"2015-10-20"}
```


##DELETE
### Delete one booking
#### /rest/bookings/\<id>

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{}
```

TravelPlanService End Points
------------------------
##CREATE
### Create a new booking

#### /rest/travelagent/travelplans

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{"customerId":10002, "flightId":10000, "hotelId":1027, "taxiId":5, "bookingDate": "2016-10-20"}
```

* Response example:
* Success: 201 Created
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"error":"Failed to create a hotel booking"}
```


##READ
### List all bookings
#### /rest/travelagent/travelplans

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{"id":2,"customerId":10002,"flightBookingId":3,"hotelBookingId":1,"taxiBookingId":7},{"id":4,"customerId":10002,"flightBookingId":4,"hotelBookingId":3,"taxiBookingId":8}]
```


##DELETE
### Cancel one booking
#### /rest/travelagent/travelplans/\<id>

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{"id":2,"customerId":10002,"flightBookingId":3,"hotelBookingId":1,"taxiBookingId":7}
```

FlightService End Points
------------------------
##READ
### List all bookings
#### /rest/travelagent/flights

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{"id":10001,"departurePoint":"DUB","flightNumber":"12345","destinationPoint":"NCL"},{"id":10000,"departurePoint":"VLN","flightNumber":"ABCDE","destinationPoint":"NCL"}]
```

### Find a flight by it's associated flight ID.
#### /rest/travelagent/flights/\<flightId>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{"id":10001,"departurePoint":"DUB","flightNumber":"12345","destinationPoint":"NCL"}
```

ContactService End Points (Depreciated)
------------------------
##CREATE
### Create a new contact

#### /rest/contacts

* Request type: POST
* Request type: JSON
* Return type: JSON
* Request example:

```JavaScript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```

* Response example:
* Success: 200 OK
* Validation error: Collection of `<field name>:<error msg>` for each error

```JavaScript
{"email":"That email is already used, please use a unique email"}
```


##READ
### List all contacts
#### /rest/contacts

* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'},
 {email: "john.doe@company.com", id: 15, firstName: "John", lastName: 'Doe', phoneNumber: "212-555-1212", birthDate:'1978-02-23'}]
```

### Find a contact by it's ID.
#### /rest/contacts/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```


##UPDATE
### Edit one contact
#### /rest/contacts

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```


##DELETE
### Delete one contact
#### /rest/contacts

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{email: "jane.doe@company.com", id: 14, firstName: "Jane", lastName: 'Doe', phoneNumber: "223-223-1231", birthDate:'1966-01-03'}
```



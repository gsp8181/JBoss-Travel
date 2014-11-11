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
{"firstName": "John", "lastName": "Smith", "phoneNumber": "01915552092", "email": "john.smith@newcastle.co.uk"}
```

* Response example:
* Success: 200 OK
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
[{"id": 14, "firstName": "John", "lastName": 'Smith', "phoneNumber": "01915552092", "email": "john.smith@newcastle.co.uk"},
 {"id": 15, "firstName": "Jane", "lastName": "Smith", "phoneNumber": "01512235655", "email": "jane.smith@liverpool.co.uk"}]
```

### Find a contact by it's ID.
#### /rest/customers/\<id>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
{"id": 15, "firstName": "Jane", "lastName": "Smith", "phoneNumber": "01512235655", "email": "jane.smith@liverpool.co.uk"}
```


##UPDATE
### Edit one contact
#### /rest/customers/\<id>

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{"id": 15, "firstName": "Jane", "lastName": "Smith", "phoneNumber": "01512235655", "email": "jane.smith@liverpool.co.uk"}
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
* Success: 200 OK
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
{"customerId":10001,"hotelId":27,"bookingDate":"2015-10-20"}
```

* Response example:
* Success: 200 OK
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
[{"id":1001,"customerId":10001,"hotelId":27,"bookingDate":"2015-10-20"},{"id":1002,"customerId":10002,"hotelId":99,"bookingDate":"2015-10-20"},{"id":1003,"customerId":10002,"hotelId":99,"bookingDate":"2015-10-21"},{"id":1,"customerId":10012,"hotelId":99,"bookingDate":"2015-10-22"}]
```

### Find a list of bookings by it's associated customer ID.
#### /rest/bookings/\<customerId>
* Request type: GET
* Return type: JSON
* Response example:

```javascript
[{"id":1002,"customerId":10002,"hotelId":99,"bookingDate":"2015-10-20"},{"id":1003,"customerId":10002,"hotelId":99,"bookingDate":"2015-10-21"}]
```


##UPDATE
### Edit one booking
#### /rest/bookings

* Request type: PUT
* Return type: JSON
* Response example:

```javascript
{"id":1003,"customerId":10002,"hotelId":99,"bookingDate":"2015-10-21"}
```


##DELETE
### Delete one booking
#### /rest/bookings

* Request type: DELETE
* Return type: JSON
* Response example:

```javascript
{"id":1003,"customerId":10002,"hotelId":99,"bookingDate":"2015-10-21"}
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



# ClickBus Exam
Basic CRUD API written in **python** with Flask for registering, listing and editing Places. It requires a JWT token header which can be obtained on the `/auth` endpoint with the user `test/test`.
A running instance can be found on [Heroku](https://clickbus-api.herokuapp.com) `https://clickbus-api.herokuapp.com`

## Index
-  [Requirements](#requirements)
-  [Installation](#installation)
-  [Running](#running)
-  [Tests](#tests)
-  [Endpoints](#endpoints)
-  [Error Handling](#error-handling)
-  [Improvements](#improvements)

## Requirements
- python3
- pip
- Environment variables: **CLICKBUS_SECRET_KEY**,  **CLICKBUS_DB_URI**
- Dependencies are listed on **requirements.txt** file.

## Installation
```bash
pip install -r requirements.txt
export CLICKBUS_SECRET_KEY=yoursecretkey
export CLICKBUS_DB_URI=db_uri #example: postgresql://dbusr:dbpwd@dbhost/database
```

## Running
- `python run.py`

or

- `gunicorn wsgi:app`

## Tests
Requires **CICKBUS_TEST_DB_URI** environment variable
```bash
export CLICKBUS_TEST_DB_URI=sqlite:///$(pwd)/testdb.db #example
pytest --cov=places tests/
```

## Endpoints
- `/auth`- Used to obtain an access token

**Method:** POST

**Entry:** json

**Entry Format:**
```
{
    "username": "test", // only user available
    "password": "test"  // only user available
}
```
**Returns:** json

**Return Format:**
```
{
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzY4MjI0ODUsImlhdCI6MTU3NjgyMDY4NSwibmJmIjoxNTc2ODIwNjg1LCJpZGVudGl0eSI6OTk5OTk5fQ.oOR_Y9KcWcIy4ddY4B-NfdX-y54d3HZMmICjhVgsFkw"
}
```
---
-  `/api/v1.0/places/` - Lists all registered places

**Method:** GET

**Entry:** none

**Requires: access_token from */auth* on *header***
```json
{"Authorization": "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzY4MjI0ODUsImlhdCI6MTU3NjgyMDY4NSwibmJmIjoxNTc2ODIwNjg1LCJpZGVudGl0eSI6OTk5OTk5fQ.oOR_Y9KcWcIy4ddY4B-NfdX-y54d3HZMmICjhVgsFkw"}
```
**Returns:** json

**Return Format:**
```
{
    "places": [
        {
	    "id": example_id, 				    //as int
	    "name": "Example Name",
	    "slug": "Example Slug",
            "city": "Example City",
            "state": "Example State",
            "created_at": "Thu, 19 Dec 2019 10:35:57 GMT", // as timestamp, should be formatted as "mm/dd/yyyy hh:ii:ss"?,
            "updated_at": ""				   // same as created_at
        },
        {
            ...
        },
    ]
}
```
---
-  `/api/v1.0/places/new` - Register a new Place

**Method:** POST

**Entry:** json

**Entry Format:**
```
{
    "name": "Example Name",  //required
    "slug": "example_slug",  //required, spaces not allowed
    "city": "Example City",  //required
    "state": "Example State" //required
}
```
**Requires: access_token from */auth* on *header***
```json
{"Authorization": "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzY4MjI0ODUsImlhdCI6MTU3NjgyMDY4NSwibmJmIjoxNTc2ODIwNjg1LCJpZGVudGl0eSI6OTk5OTk5fQ.oOR_Y9KcWcIy4ddY4B-NfdX-y54d3HZMmICjhVgsFkw"}
```
**Returns:** json

**Return Format:**
```
{
    "place": {
	"id": example_id, 				// as int
        "name": "Example Name",
	"slug": "Example Slug",
        "city": "Example City",
        "state": "Example State",
        "created_at": "Thu, 19 Dec 2019 10:35:57 GMT", //as timestamp, should be formatted as "mm/dd/yyyy hh:ii:ss"?,
        "updated_at": "" 			       // same as created_at
    }
}
```
---
-  `/api/v1.0/places/edit` - Changes data in specific place

**Method:** PUT

**Entry:** json

**Entry Format:**
```
{
    "id": place_id_to_change, 			      // as int
    "fields": {
        "field_to_change": { 			      //name, slug, city or state
           "current_value": "current_value_of_field", //current_value to ensure correct model instance
           "new_value": "new_value_of_field"
        },
        "other_field": {
            ...
        }
    }
}
```
**Requires: access_token from */auth* on *header***
```json
{"Authorization": "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzY4MjI0ODUsImlhdCI6MTU3NjgyMDY4NSwibmJmIjoxNTc2ODIwNjg1LCJpZGVudGl0eSI6OTk5OTk5fQ.oOR_Y9KcWcIy4ddY4B-NfdX-y54d3HZMmICjhVgsFkw"}
```
**Returns:** json

**Return Format:**
```
{
    "place": {
	"id": example_id, 				// as int
        "name": "Example Name",
	"slug": "Example Slug",
        "city": "Example City",
        "state": "Example State",
        "created_at": "Thu, 19 Dec 2019 10:35:57 GMT", // as timestamp, should be formatted as "mm/dd/yyyy hh:ii:ss"?,
        "updated_at": "time of change" 		       // same as created_at
    }
}
```
-  `/api/v1.0/places/_slug` - Fetch specific Place by slug

**Method:** GET

**Entry:** change _slug with the required slug on URL

**Requires: access_token from */auth* on *header***
```json
{"Authorization": "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzY4MjI0ODUsImlhdCI6MTU3NjgyMDY4NSwibmJmIjoxNTc2ODIwNjg1LCJpZGVudGl0eSI6OTk5OTk5fQ.oOR_Y9KcWcIy4ddY4B-NfdX-y54d3HZMmICjhVgsFkw"}
```
**Returns:** json

**Return Format:**
```
{
    "place": [
        {
	    "id": example_id, 				   //as int
	    "name": "Example Name",
	    "slug": "Example Slug",
            "city": "Example City",
            "state": "Example State",
            "created_at": "Thu, 19 Dec 2019 10:35:57 GMT", //as timestamp, should be formatted as "mm/dd/yyyy hh:ii:ss"?,
            "updated_at": "" 				   // same as created_at
        }
    ]
}
```
---
-  `/api/v1.0/places/search/_name` - Search for Places with name like _name

**Method:** GET

**Entry:** change _name with the required name on URL

**Requires: access_token from */auth* on *header***
```json
{"Authorization": "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NzY4MjI0ODUsImlhdCI6MTU3NjgyMDY4NSwibmJmIjoxNTc2ODIwNjg1LCJpZGVudGl0eSI6OTk5OTk5fQ.oOR_Y9KcWcIy4ddY4B-NfdX-y54d3HZMmICjhVgsFkw"}
```
**Returns:** json

**Return Format:**
```
{
    "places": [
        {
	    "id": example_id, 				   // as int
	    "name": "Example Name",
	    "slug": "Example Slug",
            "city": "Example City",
            "state": "Example State",
            "created_at": "Thu, 19 Dec 2019 10:35:57 GMT", // as timestamp, should be formatted as "mm/dd/yyyy hh:ii:ss"?,
            "updated_at": "" 				   // same as created_at
        },
        {
            ...
        },
    ]
}
```
---
## Error Handling
All possible errors will be returned with the appropriate status_code on response, and all of them will be on the following format:
```json
{
    "error_message": "Error Description"
}
```

## Improvements
I tried to keep it the simplest I could, but of course there is alot of room for improvements.
Some of them are:

- Implement Place deletion
- Implement proper Logging
- Implement User management
- Implement access_token refreshing and rotation
- Implement access_token management
- Setup CORS if needed (for now it accepts requests from any origin)

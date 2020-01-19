# ClickBus Exam
Pretty much the exact same implementation of [places_api](https://github.com/morphsx/quero-ser-clickbus/tree/master/testes/backend-developer/solution/places_api) but in **Java** with Spring Boot, with some minor differences.
A running instance can be found on [Heroku](https://clickbus-api-java.herokuapp.com) `https://clickbus-api-java.herokuapp.com`
I named some variables with snake_casing instead of camel case just so it has the same result as the python implementation.

## Requirements
- Java 8+
- Environment variables: **SPRING_DATASOURCE_URL**,  **SPRING_DATASOURCE_USERNAME**, **SPRING_DATASOURCE_PASSWORD**
-                        **SPRING_DATASOURCE_TEST_URL**, **SPRING_DATASOURCE_TEST_USERNAME**, **SPRING_DATASOURCE_TEST_PASSWORD**

## Changes from [places_api](https://github.com/morphsx/quero-ser-clickbus/tree/master/testes/backend-developer/solution/places_api)
- There is a new endpoint Register `/auth/register` to create Users to use with `/auth` endpoint and get JWT token.

## Building
- `./mvnw package`

## Running
```bash
export SPRING_DATASOURCE_URL=db_url #example: jdbc:postgresql://dbhost:dbport/dbname
export SPRING_DATASOURCE_USERNAME=db_usr
export SPRING_DATASOURCE_PASSWORD=db_pwd
java -jar target/*.jar
```

## Tests
```bash
export SPRING_DATASOURCE_TEST_URL=db_url #example: jdbc:postgresql://dbhost:dbport/dbname
export SPRING_DATASOURCE_TEST_USERNAME=db_usr
export SPRING_DATASOURCE_TEST_PASSWORD=db_pwd
./mvnw test
```

## Endpoints
- `/auth/register`- Used to create an User to use on `/auth`
**Method:** POST
**Entry:** json
**Entry Format:**
```json
{
    "username": "test",
    "password": "testpwd"
}
```
**Returns:** json
**Return Format:**
```json
{
    "id": 1,             // as int
    "username": "test"
}
```
---
The rest is exactly the same as from [places_api](https://github.com/morphsx/quero-ser-clickbus/tree/master/testes/backend-developer/solution/places_api)

## Improvements

- "Override" Whitelabel error pages
- Better handling for exception responses. I wasn't able to catch some exceptions and route them through my custom ErrorResponse object without generalizing too much and losing control over http_status
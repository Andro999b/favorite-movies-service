# Favorite Movies Service [![Build Status](https://travis-ci.org/Andro999b/favorite-movies-service.svg?branch=master)](https://travis-ci.org/Andro999b/favorite-movies-service)

[API](https://favorite-movies-list.herokuapp.com/)
[Swagger JSON](https://favorite-movies-list.herokuapp.com/swagger.json)

# Introduction

As an avid moviegoer you love movies. 
To show your favorite movies your friends you develop a HATEAOS compliant web application service which you can use to look for a movies in the database. And your favorite movies you want to save to a favorites lists.

# Functionality

* Pagination support (page size specified in the application.conf and equal to 5)
* User can create any amount of favourite lists with unique names.
* User can add movie details to the favourite list. The same movie could be in different lists, but cannot be duplicated within one list.
* User can rename list
* User can move movie from one list to another
* User can delete movie or list. If list is not empty - related movies also should be deleted.

# Language and technologies

Java 8
Hibernate ORM
Play Framework 2.6
In-memory database (e.g. H2)

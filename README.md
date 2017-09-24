# Favorite Movies Service [![Build Status](https://travis-ci.org/Andro999b/favorite-movies-service.svg?branch=master)](https://travis-ci.org/Andro999b/favorite-movies-service)

[API](https://favorite-movies-list.herokuapp.com/)

[Swagger JSON](https://favorite-movies-list.herokuapp.com/swagger.json)

# Introduction

Simple that allows to manage list of favorites movies.

# Functionality

* Pagination support (page size specified in the application.conf and equal to 5)
* User can create any amount of favourite lists with unique names and release date.
* User can add movie details to the favourite list. The same movie could be in different lists, but cannot be duplicated within one list.
* User can rename list
* User can delete movie or list. If list is not empty - related movies also should be deleted.

# Language and technologies

* Java 8
* Hibernate ORM
* Play Framework 2.6
* In-memory database (e.g. H2)

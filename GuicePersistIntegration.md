## About ##

Guice Persist provides abstractions for working with datastores and persistence providers in your Guice applications. It works in a normal Java desktop or server application, inside a plain Servlet environment, or even a Java EE container.

Unfortunately, Guice Persist can be a little finicky when running in a concurrent application. This is due to the fact that JDBC and some JPA providers may not be thread safe.

## Problem ##

The problem with Guice Persist and concurrent applications is that it creates an instance of EntityManager for every thread. This is to avoid the complications related to the possibility of underlying services not being thread safe.  The transaction process is as follows:

  1. poll the EntityManager for the current thread
  1. create a transaction
  1. run the transaction (you're transactional method)
  1. commit or roll-back depending on if the operation was successful

The problem comes from the fact that you injected an EntityManager into your class from a different thread. This implies that when you enter the transactional block, the transaction is for a different EntityManager.

## Solution ##

The solution is to, instead of injection the EntityManager, inject the EntityManager's provider. Within the transactional methods, you can poll the EntityManager for the current thread.
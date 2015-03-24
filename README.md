# MercuryDB

[![Join the chat at https://gitter.im/colestew/mercurydb](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/colestew/mercurydb?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A non-persistent single-schema main-memory database, written in Java.

For the most up-to-date documentation, see [the MercuryDB Wiki](https://github.com/colestew/mercurydb/wiki/).

The goal is to provide a framework and interface for developers to interact with objects in their program through concepts and vocabulary inspired by a traditional, persistent NoSQL database, without having to configure and maintain a database solution, which is typically heavy-weight and difficult to deploy and maintain. With _MercuryDB_, a developer gains all the benefits of interacting with data in their programs without any of the complexity of maintaining a persistent datastore. 

Additionally, applications which are built using _MercuryDB_ are fully self-contained and do not require a database deployment or any similar setup on the client-side, thus opening up a lot of possibilities for developers who are looking to do complicated operations on data within their applications which would normally require a database.

## Building HgDB and the Weborders Example ##

There is one example in the weborders folder with a set of small tests. This project is built using Gradle. You can build HgDB and the example project by running gradle build from the root. You can test the example project by running `gradle test` inside the weborders project folder. The `build.gradle` file for the weborders example project can serve as a template for building other projects.

## Integrating _MercuryDB_ into your application

When developing Java applications, developers typically make use of a number of Java __packages__ which are developed and maintained by a third party (which may be the same as the application developer). These packages are called __dependencies__ of a Java application, and for the purposes of _MercuryDB_ will be referred to as __source packages__.

As you are developing your application and you realize that you need the functionality offered by _MercuryDB_, adding it into your project will require some modifications to your build process and source code, but we intend to keep these as minimal as possible. These modifications are described below.

_MercuryDB_ is provided to developers as an application, `hgdb`, which, given a __source package__, produces a __mercury package__ (a modified version of the __source package__), and a __mercury schema__, which provides the API for interacting with objects from that package as database objects.

As part of the build process for an application using _MercuryDB_, `hgdb` produces __mercury packages__ which have an altered namespace to prevent linking against the __source package__ when a developer intends to link against the __mercury package__ instead. The developer will have to update the relevant namespaces as described below in order to take advantage of the new functionality offered by _MercuryDB_.

A __schema__ is produced by _MercuryDB_ after running `hgdb` on a __package__ and producing a __mercury package__. In practice this __schema__ is a separate library which defines the API that can be used to interact with objects in the __mercury package__.

#### Mercury Packages

A library __source package__ is typically given a namespace like `com.developer.library`. `org.mercurydb.Main` is the executable Java file responsible for the creation of the files in an HgDB database. 

#### Mercury Database API

In order to interact with the database functionality provided by the modifications to the __packages__, we also provide an API in the namespace:

`org.mercurydb.queryutils`

## Performance

Because _MercuryDB_ performs static code generation based on the contents of a __source package__ we benefit from a static schema -- that is, a schema which does not change during the runtime of an application. With a static schema we can highly optimize operations on objects (which function like rows in a database table) in a way that a traditional database can not do.

Because the database remains fully resident in memory while the application is running, we never have to go to disk to query objects in the database. This makes _MercuryDB_ orders of magnitude faster than traditional databases whose objects remain resident on disk.

### Performance Analysis

[coming soon]

## Compatibility

Unfortunately, not all libraries may be compatible with _MercuryDB_ as written. We assume a certain code structure and some restrictions on how classes must be written in order to benefit from _MercuryDB_'s features and achieve full correctness.

For instance, in order for database updates to propagate correctly, all fields which are intended to be queried against must be made public strictly through the use of public getters and setters, such that the return value of a getter is exactly what was changed by the corresponding setter. Additionally, all fields set by a constructor which are exposed as queryable fields must be set using these setters rather than setting the fields directly with `this`.

For open-source libraries, we will be able to patch and maintain modifications to their source code in order to make them compatible with _MercuryDB_, however for commercial libraries without source code available, this may be much more difficult. We are currently working on ways around these limitations and input from the community is welcome.

## FAQ

### I don't really understand what MercuryDB is...

In a certain context, you can think of _MercuryDB_ as a kind of jQuery for Java applications, where all objects created in the application are like the HTML entities on a web page. You can query objects by their properties and operate on them, just as you would do with JavaScript on a web page.

### How do you make the changes to the packages?

Given `.class` files, we use a utility library called `javassist` to update the class files and make certain kinds of changes. Not all modifications to the bytecode are possible, but one thing we really count on is inserting instructions at the beginning and end of methods and constructors.

## Wiki

See our [wiki on GitHub](https://github.com/colestew/mercurydb/wiki) to find out more about the technical workings of _MercuryDB_.

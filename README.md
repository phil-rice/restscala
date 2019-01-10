# This project is an exploratory project
 
It is a 'proof of concept' and not production ready code. It is written 'as fast as possible'
to allow a new idea to be tested and cuts and pastes from a lot of places.

This project probably won't be maintained: like all good 'quest' or 'spike' projects it 
should be thrown away and the learning used to make production code

# Presentations

* [This explains the idea](https://docs.google.com/presentation/d/e/2PACX-1vT1PZ55Mdk-UmI5JZfQqcDPPTezFLr08FcuavETo5anihzLgFH-Bv0avkVa6jWvWn-Gyz5SyRyzETpf/pub?start=false&loop=false&delayms=3000)


# Running

* Run the only application in one of the modules backend1 or backend2 or backend3 (only run one at once)
  * You can use `sbt run/backend1` but it will never return, so use a terminal or use & if on linux
* Run the only application in the module website
  * You can use `sbt run/website` but it will never return, so use a terminal or use & if on linux
* Goto http://localhost:9000/person/someName/edit
* edit and look at the recorded calls. Especially look at the structure of the JSON
* stop the backend, replace it with a different one
* Rerun and see that the website continues to work


# Goals

## Reduce Coupling between microservices 
This is the primary goal. Currently most microservices I see are actually RPC and the 
network of Microservices are a monolith. This is because they communicate using a schema.

The schema holds a lot of things. It holds the description of 'this is the data you want' which
but it also holds 'here is the data you are not interested in' and 'this is the exact layout of the data'.

### Easy to change with a Schema

* Add a new attribute
* Add a new object

### Hard to change with a schema

* Rename an attribute
* Rename an object
* Move attributes into a child object
* Change a single object into a list
* Change a list into a single object
* Change data representation (JSON/XML/SOAP/xData/funkyData)

This project makes all of the above easy to change. All the changes are at the server and
deployed clients don't need to be rebuilt, recompiled or even redeployed. 

It isn't a perfect solution, but it makes at least 90% of the common changes trivial.

## Boost runtime performance 
In many (perhaps most) services, data is requested from a client and the client gives 'here is everything'.
It is of course possible to provide filters in an adhoc way, and to provide multiple
representations (schemas) of the data with content types, but this is usually adhoc and
hard to maintain

This approach encourages the server to tell the client what it needs, and the 
server is able to (without work from the developers) optimise the data it sends.

This hasn't been implemented yet, but the approach supports it: the intention is to have
the server accessed through interfaces, and the capabilities required by those interfaces
sent to the backend using the accept header. (Most of this is implemented) the backend can 
then use that knowledge (handled by the library not manually) to optimise the sent data.

## Boost developer performance
Currently developers spend a lot of time trying not to change the data representation
and managing the lifecycle of things they do change. For example when a field is renamed
the old field is kept for a while, and then eventually deleted. They worry about the 
impact of change of their data structures ('will they break their client'). They spend time
and effort coding up the requests to the client and processing the responses

If there are a hundred clients to a microservice, each will need to code up 'how to call', 
'how to get my data', and perhaps 'how to change the data'. In this project almost all 
of the work is handled by the server (once) and servers program against interfaces in 
maven/artifactory who are implemented by the server. (but in a very very losely coupled way: 
this is the 'secret sauce' of this project)

## Remove the need for contract tests

While I am very enthusiastic about contract tests for microservices (I generally hold
it to be true that if you aren't doing them, you are doing it wrong) this project
mostly negates the need for them. It basically 'shift left's the problem they
are designed to solve moving a lot of the work down to the compiler / library designers,
and moving the responsibility of verifying that the interfaces work to the server team 
(who only need to do it once not once per client)

It is of course necessary to have smoke tests, but these are much fewer in number than
the contract tests.
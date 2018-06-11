# Ticket Service Coding Challenge

I treated this assignment like it was a feature request ticket.  I kept the scope of the implementation to the specification in the PDF. 

The data stores I have implemented are lacking some significant features that a production system would require.  For instance, I am not keeping track of the confirmations because the interface described in the PDF does not require retrieving them.  In the spirit of YAGNI, I've left those features out and only implemented what is required in the PDF.

If this was a feature ticket, I'd expect the missing features would be specified in future tickets.  I like to adhere to a mantra of "You can't predict the future" and try not to speculate on future designs that are outside the scope of a ticket.

There are some unspecified things in the PDF.  I have placed TODO comments where appropriate.  If I had 2-way communication with the team, we could work together to come up with a desired solution for those open questions.

## Design

I designed this system to separate the IO from the data in the system.  This approach enables a strict definition of the data as immutable values. 

This design also allows interfaces to access that data. With these interfaces, the implementation details can change without leaking the implementation details.

### Stores

The concept of stores are interfaces that resemble common data types.  These are stateful entities within the system. These stores only superficially resemble their datatypes and do not implement interfaces from the collections framework. I wanted to make the store APIs as simple as possible to met the requirements. 

I chose a Queue-like interface for the available seats.  I implemented a simple Memory based SeatQueue using a PriorityQueue which keeps the Seats sorted by the distance from the most desirable seat. 

The definition of "desirability" was not specified in the PDF. I had to guess the definition of seat desirability. I abstracted out the meaning of desirability as a class called SeatDesire. This absctraction will allow us to redefine that meaning without significantly effecting the code base.

The storage of the SeatHold objects is a simple CRUD interface. The memory based solution is a simple HashMap with an auto-incrementing integer key.

The most confusing store is the TimestampStore.  This exists to make testing easier.  

### Records

Records are simple immutable values that the stores provide to their clients. Most of these records are simple classes (SeatHold, SeatReservation). The one record that is not a class is the Seat interface. 

The Seat interface allows us to create an implementation that is specific to the type of room the seat is in. The Seat interface is also where we define the desirability of the seat.  

The score() method is the unsigned distance from 0. Where 0 is the most desirable seat and negative and positive infinity are the least desirable.  

In the GridSeat implementation, I used our friend the pythagorean theorem to calculate the distance from the most desirable seat.

Please note that the records that I've implemented are not strictly immutable. I did not have time to research Java libraries that provide an immutable record feature.

## Testing

I used simple unit-testing to signal that the implementations are correct.  To verify the entire service I used  "property based testing".  Property testing is a useful technique for testing stateful systems. This type of testing is popular in Erlang to verify the properties of services and systems.

In property based testing, we defined a set of invariants that cannot be violated by the system.  For our system the set of invariants are:

 * No seat can be double booked
 * No expired hold can be redeemed
 * No hold can hold a seat that is already held by an active hold
 * No hold can have the same key as another hold
 * All seats must eventually be redeemed

These are the rules of our system.  If any of them are violated, our system is broken.  To test these I created a JUnit test that randomly executes valid operations on the system, keeps track of the state of the world in a model, and then checks the invariants against the model.

The nice thing about property-based testing is that it can be used to validate any implementation of the service. If we change the stores to be SQL or NoSQL, we can rerun the property tests against those implementations.

## Conclusion

This may look like a significant body of work but it did not take me more than an afternoon on Saturday to implement.

I pride myself in working smarter not harder.  The property test allowed me to quickly discover bugs in my implementation that would have been released into production.  These are bugs that would have been difficult to discover with unit-tests. 

Enjoy.
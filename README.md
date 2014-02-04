jen_fsm
=======

A tiny Finite State Machine for Java, heavily inspired by
[Erlang/OTP's gen_fsm](http://www.erlang.org/doc/man/gen_fsm.html).


Probably the biggest functional difference between gen_fsm and jen_fsm
is the latter's ability to register and use dynamic state methods in
addition to gen_fsm-similar static state methods and global
handlers. This allows for configuring FSMs from DSLs or simply
manipulating them at runtime.


Internally, jen_fsm works with a registry of state methods per
class. Once used, the state method is registered for direct lookup
instead of inspecting the class again. As opposed to this, dynamic state
methods are registered on a per instance basis. jen_fsm always first
checks the FSM for dynamic state methods, and falls back to static ones
in the class (static isn't in terms of Java, but in terms of statically
defined and annotated) only if no dynamic one was found.

## Usage

Snapshot versions can be obtained from
[http://oss.sonatype.org/content/repositories/releases](http://oss.sonatype.org/content/repositories/releases):


```xml
<dependency>
  <groupId>org.pbit</groupId>
  <artifactId>jen_fsm</artifactId>
  <version>0.3.1</version>
</dependency>
```

The class `FSMTest` contains every possible usage scenario und should
explain itself.

Concerning classes and interfaces: the interface `FSM` can, but doesn't
have to be used. There is a class called `AbstractFSM` which implements
it, and from which FSM's can derive.

When going with `AbstractFSM`, it is absolutely necessary to run
`JenFSM.start` before the first usage. Also, after such an FSM once has
been terminated, it can't be used anymore except some information calls.

When using "all-handlers" - such that aren't named, but are getting
every single state transition call or every event, you have to decide
between the synchronous and asynchronous ones. There is one interface
for every such use case - async `EventHandler` and sync
`SyncEventHandler`. Asynchronous functionality hasn't been implemented
in the `AbstractFSM` though, since it would involve user specific
middleware or libraries, so the core of jen_fsm doesn't come up with a
default implementation at all.

## Examples

The following assumes the class derives from AbstractFSM for simplicity
and default basic implementations. Example code was taken from the unit
test:

```java
final class LocalTestFSM extends AbstractFSM
````
	
First of all, it is necessary to understand some secondary classes and
why they are around. There are classes such as `Return` and `From` that
are more or less tuples. Erlang has tuples as built-in data type, Java
hasn't. Instead of going with a generalisation of tuples through some
generic classes, I've implemented very specific tuple classes similar to
tuples invloved in gen_fsm's lifecycle. They have a tuple describing a
Return of a state function, so I've implemented something similar -
`Return`. Also, `From` just describes a caller that can get informed
about the progress of the state machine.

Another important thing to understand is also a big difference between
gen_fsm and jen_fsm: state. In OTP, being build on a functional language
(Erlang), state gets externalised and attached with every single state
function call. The function can return a different state, and this state
will again be externalised, so the function doesn't need to care about
it.

I have decided to encapsulate the state itself in the FSM object. I'm
aware this can be dangerous, but this is the path I took with
`AbstractFSM`. It anytime can be changed by implementing own FSM classes
that just implement the `FSM` interface.

Concerning the lifecycle, it is similar in jen_fsm as it is in
gen_fsm. By calling `JenFSM.start(fsm)` one officially starts a state
machine, which is followed by the call to the `init()` method of the FSM
class. Here is an example:

```java
@Override
public void init() {
  stateData = new TestStateData(PLUS);
}
```  	

As can be seen, the `init()` method is the right place to create the
encapsulated state described above. Test data class itself needs to
derive from `StateData`:


```java
final class TestStateData extends StateData {

  public int count = 10;

  public TestStateData(String currentState) {
    super(currentState);
  }
}
```

The rest of the state is implementation's own logic - there are no
constraints from jen_fsm.

When an FSM is about to get terminated, its method `terminate(reason, state)` will be called.

Implementing a simple "static" state method is as simple as this:

```java
@StateMethod
public Return plus(Object event, From from) {
  if (event instanceof Integer) {
    ((TestStateData)stateData).count += (Integer)event;

    return new Return(REPLY, ((TestStateData)stateData).count, MINUS);
  } else {
    throw new IllegalStateException("Integer expected, but " + 
      event.getClass().getSimpleName() + " received");
  }
}
```

The annotation `@StateMethod` marks the method as state method. This
method just increments an internal counter in the state by the given
number and returns a `REPLY`-tagged Return-tuple, which contains the new
counter value and the next step's name - hiding behind the MINUST static
string.

Before we can look at the rest of the lifecycle, here is how the client call talks to this FSM:

```java
LocalTestFSM fsm = new LocalTestFSM();
JenFSM.start(fsm);
JenFSM.syncSendEvent(fsm, 5, new From(new ReplyHandler() {
    @Override
    public void reply(Object reply, String tag) {
      assertEquals((Integer)15, (Integer)reply);
    }
  }, null));

JenFSM.syncSendEvent(fsm, 1, new From(new ReplyHandler() {
    @Override
    public void reply(Object reply, String tag) {
      assertEquals((Integer)14, (Integer)reply);
    }
  }, null));
```

First, an FSM object gets instantiated. Then, the state machine gets
started. Then, we call the annotated state method `plus` by calling
`JenFSM.syncSendEvent`. We use an integer (5) as event to the state
method, plus we declare an anonymous `From` tuple built around a
`ReplyHandler`. Its method `reply` will be called through the lifecycle
with the values provided with the `Return` tuple in the state method.

After calling the first state transition of the state machine, it's in
the step `MINUS` now. This is eventually the biggest shift in
understanding finite state machine OTP style: it doesn't expect you to
provide the next step from outside, it does transitions internally, so
the whole logic is encapsulated. The only thing that can be provided
from outside of the FSM and thus influence state transition is an event.


So, by this time, the state method `plus` has moved the FSM into the state `MINUS`, so the second call to `JenFSM.syncSendEvent` in the example above simply calles the `minus` method in the FSM class:

```java
@StateMethod
public Return minus(Object event, From from) {
  if (event instanceof Integer) {
    ((TestStateData)stateData).count -= (Integer)event;

    return new Return(STOP, ((TestStateData)stateData).count);
  } else {
    throw new IllegalStateException("Integer expected, but " +
                                    event.getClass().getSimpleName() + " received");
  }
}
```
This method simply decrements the counter in the state, so the
mathematical part of this simple plus/minus computation is working
just logic. What this method also does: it terminates the FSM, by
returning the Return tuple with the tag `STOP` and the current
value. Internally, this is a signal for the FSM to terminate, so now
only a few information methods can be called on this FSM, for example:


```java
assertEquals(true, fsm.isTerminated());
assertEquals((Integer)14, (Integer)fsm.getTerminationReason().getPayload());
assertEquals(TerminationReason.NORMAL, fsm.getTerminationReason().getTag());
assertEquals(LocalTestFSM.MINUS, fsm.getCurrentState());
```

We can check if the FSM is terminated. We can check the final value. We
can check for the termination reason and the very last state the FSM was
in before termination.

"Static" state methods are not the only possible ones. There are two
further ways to introduce a state method or to handle states and their
transitions. First one is the danamic way:

```java
LocalTestFSM fsm = new LocalTestFSM();
JenFSM.start(fsm);
fsm.registerStateHandler(LocalTestFSM.PLUS, new DynamicStateHandler() {
    @Override
    public Return handle(Object event, From from) {
      if (event instanceof Integer) {
        return new Return(REPLY, (Integer)event, LocalTestFSM.PLUS);
      } else {
        throw new IllegalStateException("Integer expected, but " +
                                        event.getClass().getSimpleName() + " received");
      }
    }
  });
```    

By registering a dynamic state method, one can even overwrite an
existing static one, since the order of method check is first dynamic,
then static. Essentially, the anonymous state handler in the code above
is similar to the static one, though it implements the method `handle`
that will be called to handle the state `PLUS`.
    
```java
JenFSM.syncSendEvent(fsm, 5, new From(new ReplyHandler() {
    @Override
    public void reply(Object reply, String tag) {
      assertEquals((Integer)5, (Integer)reply);
    }
  }, null));
```

Now, we've called the known client code method again, with the result
that since the counter in the state didn't get change, we get the
original value back. This proves that the annotated state method `plus`
hasn't been called at all.

And now we just revert to the default implementation, unregistering the
dynamic handler:

    
```java
fsm.unregisterStateHandler(LocalTestFSM.PLUS);

JenFSM.syncSendEvent(fsm, 1, new From(new ReplyHandler() {
    @Override
    public void reply(Object reply, String tag) {
      assertEquals((Integer)11, (Integer)reply);
    }
  }, null));
```

And now the original `plus` method is being called, incrementing the
counter in the state.


The very last was to define state handlers is the most general one:
instead of tagging state methods with state tags, we can have a general
state handler like this:


```java
public Return handleSyncEvent(Object event, From from, String stateName) {
  if (event instanceof Integer) {
    ((TestStateData)stateData).count *= (Integer)event;
    JenFSM.reply(from, ((TestStateData)stateData).count);

    return new Return(NEXT_STATE, DUMMY);
  } else {
    throw new IllegalStateException("Integer expected, but " +
                                    event.getClass().getSimpleName() + " received");
  }
}
```
  	
When the client code calls the FSM with this method:

```java
JenFSM.syncSendAllStateEvent(fsm, 2, new From(new ReplyHandler() {
    @Override
    public void reply(Object reply, String tag) {
      assertEquals((Integer)20, (Integer)reply);
    }
  }, null));
```

the general state handler will be called, with parameters already known
from the named state methods, plus the current state name as the third
parameter. Here, it's up to the handling code to `switch{...}` between
different states, make map lookups or whatever fantasy might find. In
the case above, it just multiplies the counter and returns a Return
tuple indicating transition to the state `DUMMY`.

## ToDo

The async parts are provided as template, so one can override methods
and implement own async logic on top of whatever library or
middleware. Probably, they will never make it into the project for that
there are so many different potential transports for async that any
decision I make will be the wrong one for somebody else.

Also, timers aren't implemented yet, but will be soon.

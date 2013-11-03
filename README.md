jen_fsm
=======

A tiny Finite State Machine for Java, partially inspired by [Erlang/OTP's gen_fsm](http://www.erlang.org/doc/man/gen_fsm.html). Inspired means not dogmatically reimplemented (which would be quite a challenge on the JVM), but as far as it is possible and reasonable on the JVM or in the "standard" Java at all.

Probably the biggest functional difference between gen_fsm and jen_fsm is the latter's ability to register and use dynamic state methods in addition to gen_fsm-similar static state methods and global handlers. This allows for configuring FSMs from DSLs or simply manipulating them at runtime.

Internally, jen_fsm works with a registry of state methods per class. Once used, the state method is registered for direct lookup instead of inspecting the class again. As opposed to this, dynamic state methods are registered on a per instance basis. jen_fsm always first checks the FSM for dynamic state methods, and falls back to static ones in the class (static isn't in terms of Java, but in terms of statically defined and annotated) only if no dynamic one was found.

## Usage

Snapshot versions can be obtained from [https://oss.sonatype.org/content/repositories/snapshots/](https://oss.sonatype.org/content/repositories/snapshots/):

    <dependency>
	    <groupId>org.pbit</groupId>
	    <artifactId>jen_fsm</artifactId>
	    <version>0.1-SNAPSHOT</version>
    </dependency>

The class `FSMTest` contains every possible usage scenario und should explain itself.

Concerning classes and interfaces: the interface `FSM` can, but doesn't have to be used. There is a class called `AbstractFSM` which implements it, and from which FSM's can derive. Only if you plan to use a completely different class within your class hierarchy as FSM, you will have to go with the interface and to implement everything on your own.

When going with `AbstractFSM`, it is absolutely necessary to run `JenFSM.start` before the first usage. Also, after such an FSM once has been terminated, it can't be used anymore except some information calls. Again, when going with the interface, you'd have to implement this again, but the interface allows you to go around Java's inability of multiple inheritance.

When using "all-handlers" - such that aren't named, but are getting every single state transition call or every event, you have to decide between the synchronous and asynchronous ones. There is one interface for every such use case - async `EventHandler` and sync `SyncEventHandler`.

The rest can be easily found in the test case and would be just redundant here. But please let me know if some of the usage aspects isn't clear.

## ToDo

The async parts are provided as template, so one can override methods and implement own async logic on top of whatever library or middleware. Probably, they will never make it into the project for that there are so many different potential transports for async that any decision I make will be the wrong one for somebody else.

Also, timers aren't implemented yet, but will be soon.
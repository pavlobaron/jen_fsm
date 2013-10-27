jen_fsm
=======

A tiny Finite State Machine for Java, partially inspired by [Erlang/OTP's gen_fsm](http://www.erlang.org/doc/man/gen_fsm.html). Inspired means not dogmatically reimplemented (which would be quite a challenge on the JVM), but as far as it is possible and reasonable on the JVM or in the "standard" Java at all.

Probably the biggest functional difference between gen_fsm and jen_fsm is the latter's ability to register and use dynamic state methods in addition to gen_fsm-similar static state methods and global handlers. This allows for configuring FSMs from DSLs or simply manipulating them at runtime.

Internally, jen_fsm works with a registry of state methods per class. Once used, the state method is registered for direct lookup instead of inspecting the class again. As opposed to this, dynamic state methods are registered on a per instance basis. jen_fsm always first checks the FSM for dynamic state methods, and falls back to static ones in the class (static isn't in terms of Java, but in terms of statically defined and annotated) only if no dynamic one was found.

The async parts are provided as template, so one can override methods and implement own async logic on top of whatever library or middleware.

Work in progress, feedback welcome.
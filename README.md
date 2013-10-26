jen_fsm
=======

A tiny Finite State Machine for Java, partially inspired by [Erlang/OTP's gen_fsm](http://www.erlang.org/doc/man/gen_fsm.html). Inspired means not dogmatically reimplemented (which would be quite a challenge on the JVM), but as far as it is possible and reasonable on the JVM or in the "standard" Java at all. 

The async parts are provided as template, so one can override methods and implement own async logic on top of whatever library or middleware.

Work in progress, feedback welcome.
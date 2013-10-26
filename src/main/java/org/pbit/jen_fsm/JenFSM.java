package org.pbit.jen_fsm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JenFSM {

  public static String REPLY = "reply";
  public static String NEXT_STATE = "next_state";
  public static String STOP = "stop";

  public static void start(FSM fsm) {
    fsm.init();
  }

  public static Tuple syncSendEvent(FSM fsm, Object event)
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    for (Method method : fsm.getClass().getMethods()) {
      if (method.isAnnotationPresent(StateMethod.class)) {
        if (method.getName().equals(fsm.getCurrentStateData().getCurrentState())) {
          return (Tuple)method.invoke(fsm, event);
        }
      }
    }
    
    throw new IllegalStateException("state method '" +
        fsm.getCurrentStateData().getCurrentState() + "' isn't implemented or annotated.");
  }

  public static Tuple syncSendAllStateEvent(SyncEventHandler fsm, Object event, From from) {
    return fsm.handleSyncEvent(event, from, ((FSM)fsm).getCurrentStateData().getCurrentState());
  }
  
  public static void sendEvent(FSM fsm, Object event) {
    throw new UnsupportedOperationException("Override to implement async functionality.");
  }
  
  public static void sendAllStateEvent(EventHandler fsm, Object event, StateData stateData) {
    throw new UnsupportedOperationException("Override to implement async functionality.");
  }
  
  public static void reply(From from, Tuple reply) {
    from.getCaller().call(reply);
  }
}

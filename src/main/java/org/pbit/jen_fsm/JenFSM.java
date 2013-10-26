package org.pbit.jen_fsm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JenFSM {

  public static String REPLY = "reply";
  public static String NEXT_STATE = "next_state";
  public static String STOP = "stop";
  
  protected static Map<Class<? extends FSM>, Map<String, Method>> cache = new HashMap<>();

  public static void start(FSM fsm) {
    fsm.init();
  }

  public static Tuple syncSendEvent(FSM fsm, Object event)
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    if (cache.containsKey(fsm.getClass()) &&
        cache.get(fsm.getClass()).containsKey(fsm.getCurrentStateData().getCurrentState())) {
      return (Tuple)cache.get(fsm.getClass()).get(fsm.getCurrentStateData().getCurrentState()).invoke(fsm, event);
    }
    
    for (Method method : fsm.getClass().getMethods()) {
      if (method.isAnnotationPresent(StateMethod.class)) {
        if (method.getName().equals(fsm.getCurrentStateData().getCurrentState())) {
          if (!cache.containsKey(fsm.getClass())) {
            cache.put(fsm.getClass(), new HashMap<String, Method>());
          }
          
          if (!cache.get(fsm.getClass()).containsKey(method.getName())) {
            cache.get(fsm.getClass()).put(method.getName(), method);
          }
          
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

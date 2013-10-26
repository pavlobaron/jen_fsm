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

  public static Object syncSendEvent(FSM fsm, Object event)
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    return syncSendEvent(fsm, event, new From(new ReplyHandler() {
      @Override
      public void reply(Object reply, String tag) {
      }
    }, null));
  }
  
  public static Object syncSendEvent(FSM fsm, Object event, From from)
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    Map<String, Method> methods = cache.get(fsm.getClass());
    if (methods != null) {
      Method method = methods.get(fsm.getCurrentStateData().getCurrentState());
      if (method != null) {
        return syncSendWithMethod(fsm, event, from, method);
      }
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
          
          return syncSendWithMethod(fsm, event, from, method);
        }
      }
    }
    
    throw new IllegalStateException("state method '" +
        fsm.getCurrentStateData().getCurrentState() + "' isn't implemented or annotated.");
  }
  
  private static Object processSyncEvent(FSM fsm, Object event, From from, Return ret)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    fsm.getCurrentStateData().setCurrentState(ret.getNextStateName());
    if (ret.getTag() == REPLY) {
      reply(from, ret.getPayload());
      
      return from.getMostRecentReply();
    } else if (ret.getTag() == NEXT_STATE) {
      //nothing to do here
    } else {
      reply(from, ret.getPayload());
      fsm.terminate();
      
      return from.getMostRecentReply();
    }
    
    return null;
  }

  private static Object syncSendWithMethod(FSM fsm, Object event, From from,
      Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Return ret = (Return)method.invoke(fsm, event, from);
    
    return processSyncEvent(fsm, event, from, ret);
  }
  
  private static Object syncSendWithHandler(SyncEventHandler fsm, Object event, From from)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Return ret = fsm.handleSyncEvent(event, from, ((FSM)fsm).getCurrentStateData().getCurrentState());
    
    return processSyncEvent((FSM)fsm, event, from, ret);
  }
  
  public static Object syncSendAllStateEvent(SyncEventHandler fsm, Object event)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return syncSendAllStateEvent(fsm, event, new From(new ReplyHandler() {
      @Override
      public void reply(Object reply, String tag) {
      }
    }, null));
  }
  
  public static Object syncSendAllStateEvent(SyncEventHandler fsm, Object event, From from)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    return syncSendWithHandler(fsm, event, from);
  }
  
  public static void sendEvent(FSM fsm, Object event) {
    throw new UnsupportedOperationException("Override to implement async functionality.");
  }
  
  public static void sendAllStateEvent(EventHandler fsm, Object event, StateData stateData) {
    throw new UnsupportedOperationException("Override to implement async functionality.");
  }
  
  public static void reply(From from, Object reply) {
    from.reply(reply);
  }
}

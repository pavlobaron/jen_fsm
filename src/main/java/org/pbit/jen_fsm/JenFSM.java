package org.pbit.jen_fsm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JenFSM {

  public static int REPLY = 1;
  public static int NEXT_STATE = 2;
  public static int STOP = 3;
  
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
    
    // check FSM's registered dynamic handlers
    if (fsm instanceof DynamicStateHandler) {
      DynamicStateHandler handler = ((DynamicFSM)fsm).findStateHandler(fsm.getCurrentState());
      if (handler != null) {
        Return ret = handler.handle(event, from);
      
        return processSyncEvent(fsm, from, ret);
      }
    }
    
    // check static handlers cache
    Map<String, Method> methods = cache.get(fsm.getClass());
    if (methods != null) {
      Method method = methods.get(fsm.getCurrentState());
      if (method != null) {
        return syncSendWithMethod(fsm, event, from, method);
      }
    }
    
    // find a static handler
    for (Method method : fsm.getClass().getMethods()) {
      if (method.isAnnotationPresent(StateMethod.class)) {
        if (method.getName().equals(fsm.getCurrentState())) {
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
        fsm.getCurrentState() + "' isn't implemented or annotated.");
  }
  
  private static Object processSyncEvent(FSM fsm, From from, Return ret)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (ret.getTag() == REPLY) {
      fsm.setCurrentState(ret.getNextStateName());
      reply(from, ret.getPayload());
      
      return from.getMostRecentReply();
    } else if (ret.getTag() == NEXT_STATE) {
      fsm.setCurrentState(ret.getNextStateName());
      //nothing to do here
    } else if (ret.getTag() == STOP) {
      reply(from, ret.getPayload());
      fsm.terminate(new TerminationReason(TerminationReason.NORMAL, ret.getPayload()), fsm.getCurrentState());
      
      return from.getMostRecentReply();
    }
    
    return null; //probably better to have a special Return for ok/error
  }

  private static Object syncSendWithMethod(FSM fsm, Object event, From from,
      Method method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Return ret = (Return)method.invoke(fsm, event, from);
    
    return processSyncEvent(fsm, from, ret);
  }
  
  private static Object syncSendWithHandler(SyncEventHandler fsm, Object event, From from)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Return ret = fsm.handleSyncEvent(event, from, ((FSM)fsm).getCurrentState());
    
    if (fsm instanceof FSM) {
      return processSyncEvent((FSM)fsm, from, ret);
    } else {
      throw new IllegalStateException("Implementation of the FSM interface expected.");
    }
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

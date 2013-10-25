package org.pbit.jen_fsm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JenFSM {

  public static String REPLY = "reply";
  public static String NEXT_STATE = "next_state";
  public static String STOP = "stop";

  public static void sendEvent(Object fsm, Tuple event)
      throws IllegalAccessException, IllegalArgumentException,
      InvocationTargetException {
    Method handleEvent = null;
    for (Method method : fsm.getClass().getMethods()) {
      if (method.isAnnotationPresent(StateMethod.class)) {
        if (method.getName().equals(event.getTag())) {
          method.invoke(fsm, event.getPayload());
          break;
        }
      } else {
        if (method.getName() == "handleEvent") {
          handleEvent = method;
        }
      }
    }
    
    if (handleEvent != null) {
      handleEvent.invoke(fsm, event);
    }
  }
}

package org.pbit.jen_fsm;

import static org.junit.Assert.assertEquals;
import static org.pbit.jen_fsm.JenFSM.REPLY;
import static org.pbit.jen_fsm.JenFSM.STOP;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

final class StaticFSM implements EventHandler {
  public int count = 10;
  @StateMethod
  public Tuple plus(Object payload) {
    if (payload instanceof Integer) {
      count += (Integer)payload;
      
      return new Tuple(REPLY, count);
    } else {
      throw new IllegalStateException("Integer expected, but " + payload.getClass().getSimpleName() + " received");
    }
  }
  
  public Tuple handleEvent(Tuple event) {
    if (event.getPayload() instanceof Integer) {
      count += (Integer)event.getPayload();
      
      return new Tuple(STOP, count);
    } else {
      throw new IllegalStateException("Integer expected, but " + event.getPayload().getClass().getSimpleName() + " received");
    }
  }
}

public class FSMTest
{
  @Test
  public void testPlusStatic() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    StaticFSM fsm = new StaticFSM();
    JenFSM.sendEvent(fsm, new Tuple("plus", 5));
    
    assertEquals(fsm.count, 15);
  }
  
  @Test
  public void testHandleEventStatic() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    StaticFSM fsm = new StaticFSM();
    JenFSM.sendEvent(fsm, new Tuple("dummy", 2));
    
    assertEquals(fsm.count, 12);
  }
}

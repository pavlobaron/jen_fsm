package org.pbit.jen_fsm;

import static org.junit.Assert.assertEquals;
import static org.pbit.jen_fsm.JenFSM.REPLY;
import static org.pbit.jen_fsm.JenFSM.STOP;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

final class TestStateData extends StateData {
  
  public int count = 10;
  
  public TestStateData(String currentState) {
    super(currentState);
  }
}

final class StaticFSM extends AbstractFSM implements SyncEventHandler {
  
  @Override
  public void init() {
    stateData = new TestStateData("plus");
  }
  
  @StateMethod
  public Tuple plus(Object event) {
    if (event instanceof Integer) {
      ((TestStateData)stateData).count += (Integer)event;
      
      return new Tuple(REPLY, ((TestStateData)stateData).count);
    } else {
      throw new IllegalStateException("Integer expected, but " + event.getClass().getSimpleName() + " received");
    }
  }
  
  public Tuple handleSyncEvent(Object event, From from, String stateName) {
    if (event instanceof Integer) {
      ((TestStateData)stateData).count *= (Integer)event;
      
      return new Tuple(STOP, ((TestStateData)stateData).count);
    } else {
      throw new IllegalStateException("Integer expected, but " + event.getClass().getSimpleName() + " received");
    }
  }
}

public class FSMTest
{
  @Test
  public void testPlusStatic() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    StaticFSM fsm = new StaticFSM();
    JenFSM.start(fsm);
    Tuple reply = JenFSM.syncSendEvent(fsm, 5);
    
    assertEquals(JenFSM.REPLY, reply.getTag());
    assertEquals(new Integer(15), (Integer)reply.getPayload());
  }
  
  @Test
  public void testHandleEventStatic() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    StaticFSM fsm = new StaticFSM();
    JenFSM.start(fsm);
    JenFSM.syncSendAllStateEvent(fsm, 2, new From(new Callback() {

      @Override
      public void call(Tuple reply) {
        System.out.println("callback called");
      }      
    }, "dummy_tag") {
      
    });
    
    assertEquals(20, ((TestStateData)fsm.getCurrentStateData()).count);
  }
}

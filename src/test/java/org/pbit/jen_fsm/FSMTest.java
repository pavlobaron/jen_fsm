package org.pbit.jen_fsm;

import static org.junit.Assert.assertEquals;
import static org.pbit.jen_fsm.JenFSM.REPLY;
import static org.pbit.jen_fsm.JenFSM.STOP;
import static org.pbit.jen_fsm.JenFSM.NEXT_STATE;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

final class TestStateData extends StateData {
  
  public int count = 10;
  
  public TestStateData(String currentState) {
    super(currentState);
  }
}

final class StaticFSM extends AbstractFSM implements SyncEventHandler {
  
  public static String PLUS = "plus";
  public static String MINUS = "minus";
  public static String DUMMY = "dummy";
  
  @Override
  public void init() {
    stateData = new TestStateData(PLUS);
  }
  
  @StateMethod
  public Return plus(Object event, From from) {
    if (event instanceof Integer) {
      ((TestStateData)stateData).count += (Integer)event;

      return new Return(REPLY, ((TestStateData)stateData).count, MINUS);
    } else {
      throw new IllegalStateException("Integer expected, but " + event.getClass().getSimpleName() + " received");
    }
  }
  
  @StateMethod
  public Return minus(Object event, From from) {
    if (event instanceof Integer) {
      ((TestStateData)stateData).count -= (Integer)event;
      
      return new Return(STOP, ((TestStateData)stateData).count);
    } else {
      throw new IllegalStateException("Integer expected, but " + event.getClass().getSimpleName() + " received");
    }
  }
  
  public Return handleSyncEvent(Object event, From from, String stateName) {
    if (event instanceof Integer) {
      ((TestStateData)stateData).count *= (Integer)event;
      JenFSM.reply(from, ((TestStateData)stateData).count);
      
      return new Return(NEXT_STATE, DUMMY);
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
    JenFSM.syncSendEvent(fsm, 5, new From(new ReplyHandler() {
      @Override
      public void reply(Object reply, String tag) {
        assertEquals((Integer)15, (Integer)reply);
      }
    }, null));
    
    JenFSM.syncSendEvent(fsm, 1, new From(new ReplyHandler() {
      @Override
      public void reply(Object reply, String tag) {
        assertEquals((Integer)14, (Integer)reply);
      }
    }, null));
    
    assertEquals(true, fsm.isTerminated());
    assertEquals((Integer)14, (Integer)fsm.getTerminationReason().getPayload());
    assertEquals(TerminationReason.NORMAL, fsm.getTerminationReason().getTag());
    assertEquals(StaticFSM.MINUS, fsm.getCurrentState());
  }
  
  @Test
  public void testHandleEventStatic() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    StaticFSM fsm = new StaticFSM();
    JenFSM.start(fsm);
    JenFSM.syncSendAllStateEvent(fsm, 2, new From(new ReplyHandler() {
      @Override
      public void reply(Object reply, String tag) {
        assertEquals((Integer)20, (Integer)reply);
      }
    }, null));
    assertEquals(StaticFSM.DUMMY, fsm.getCurrentState());
  }
}

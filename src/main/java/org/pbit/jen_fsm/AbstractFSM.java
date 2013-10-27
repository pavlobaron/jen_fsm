package org.pbit.jen_fsm;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFSM
  implements FSM, DynamicFSM {

  /*
   * FIXME: encapsulating state here feels very wrong, even
   * if it might be the more typical Java-way.
   * Instead, state should be left to the outside scope, but
   * forced through method signatures.
   */
  protected StateData stateData;
  protected TerminationReason reason;
  protected Map<String, DynamicStateHandler> dynamicHandlers = new HashMap<>();
  
  public abstract void init();

  public String getCurrentState() {
    if (stateData == null) {
      throw new IllegalStateException("FSM isn't properly set up");
    }
    
    return stateData.getCurrentState();
  }
  
  public void setCurrentState(String state) {
    if (isTerminated()) {
      throw new IllegalStateException("FSM is already terminated");
    }
    
    if (stateData == null) {
      throw new IllegalStateException("FSM isn't properly set up");
    }
    
    stateData.setCurrentState(state);
  }
  
  public void terminate(TerminationReason reason, String stateName) {
    if (stateData == null) {
      throw new IllegalStateException("FSM isn't properly set up");
    }
    
    setCurrentState(stateName);
    this.reason = reason; // from here on, the FSM is dead
  }
  
  public boolean isTerminated() {
    if (stateData == null) {
      throw new IllegalStateException("FSM isn't properly set up");
    }
    
    return reason != null;
  }

  public TerminationReason getTerminationReason() {
    if (stateData == null) {
      throw new IllegalStateException("FSM isn't properly set up");
    }
    
    return reason;
  }
  
  public void registerStateHandler(String state, DynamicStateHandler handler) {
    if (stateData == null) {
      throw new IllegalStateException("FSM isn't properly set up");
    }
    
    dynamicHandlers.put(state, handler);
  }

  public void unregisterStateHandler(String state) {
    if (stateData == null) {
      throw new IllegalStateException("FSM isn't properly set up");
    }
    
    if (dynamicHandlers.containsKey(state)) {
      dynamicHandlers.remove(state);
    }
  }

  public DynamicStateHandler findStateHandler(String state) {
    return dynamicHandlers.get(state);
  }
}

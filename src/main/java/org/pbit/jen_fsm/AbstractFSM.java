package org.pbit.jen_fsm;

public abstract class AbstractFSM implements FSM {

  /*
   * FIXME: encapsulating state here feels very wrong, even
   * if it might be the more typical Java-way.
   * Instead, state should be left to the outside scope, but
   * forced through method signatures.
   */
  protected StateData stateData;
  protected TerminationReason reason;
  
  public abstract void init();

  public String getCurrentState() {
    return stateData.getCurrentState();
  }
  
  public void setCurrentState(String state) {
    if (isTerminated()) {
      throw new IllegalStateException("FSM is already terminated");
    }
    
    stateData.setCurrentState(state);
  }
  
  public void terminate(TerminationReason reason, String stateName) {
    setCurrentState(stateName);
    this.reason = reason; // from here on, the FSM is dead
  }
  
  public boolean isTerminated() {
    return reason != null;
  }

  public TerminationReason getTerminationReason() {
    return reason;
  }
}

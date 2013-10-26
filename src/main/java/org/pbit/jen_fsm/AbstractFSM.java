package org.pbit.jen_fsm;

public abstract class AbstractFSM implements FSM {

  protected StateData stateData;
  protected boolean terminated = false;
  
  public abstract void init();

  public StateData getCurrentStateData() {
    if (terminated) {
      throw new IllegalStateException("FSM is already terminated");
    }
    
    return stateData;
  }
  
  public void terminate() {
    terminated = true;
  }
  
  public boolean isTerminated() {
    return terminated;
  }
}

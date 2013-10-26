package org.pbit.jen_fsm;

public abstract class AbstractFSM implements FSM {

  protected StateData stateData;
  
  public abstract void init();

  public StateData getCurrentStateData() {
    return stateData;
  }
}

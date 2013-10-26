package org.pbit.jen_fsm;

public class StateData {

  protected String currentState;

  public StateData(String currentState) {
    super();
    this.currentState = currentState;
  }
  
  public String getCurrentState() {
    return currentState;
  }

  public void setCurrentState(String currentState) {
    this.currentState = currentState;
  }
}

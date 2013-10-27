package org.pbit.jen_fsm;

public interface FSM {

  public void init();
  public String getCurrentState();
  public void setCurrentState(String state);
  public void terminate(TerminationReason reason, String stateName);
  public boolean isTerminated();
  public TerminationReason getTerminationReason();
}

package org.pbit.jen_fsm;

public interface FSM {

  public void init();
  public StateData getCurrentStateData();
  public void terminate();
  public boolean isTerminated();
}

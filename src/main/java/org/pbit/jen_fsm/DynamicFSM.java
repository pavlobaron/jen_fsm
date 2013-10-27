package org.pbit.jen_fsm;

public interface DynamicFSM {

  public void registerStateHandler(String state, DynamicStateHandler handler);
  public void unregisterStateHandler(String state);
  public DynamicStateHandler findStateHandler(String state);
}

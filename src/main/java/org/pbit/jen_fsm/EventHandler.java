package org.pbit.jen_fsm;

public interface EventHandler {
  public Object handleEvent(Object event, String stateName);
}

package org.pbit.jen_fsm;

public interface EventHandler {
  public Tuple handleEvent(Object event, String stateName);
}

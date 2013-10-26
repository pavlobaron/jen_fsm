package org.pbit.jen_fsm;

public interface SyncEventHandler {
  public Tuple handleSyncEvent(Object event, From from, String stateName);
}

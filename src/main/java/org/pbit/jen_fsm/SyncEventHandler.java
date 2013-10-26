package org.pbit.jen_fsm;

public interface SyncEventHandler {
  public Return handleSyncEvent(Object event, From from, String stateName);
}

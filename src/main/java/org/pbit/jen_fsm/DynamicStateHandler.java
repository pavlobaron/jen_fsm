package org.pbit.jen_fsm;

public abstract class DynamicStateHandler {

  public abstract Return handle(Object event, From from);
}

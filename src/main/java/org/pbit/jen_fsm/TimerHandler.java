package org.pbit.jen_fsm;

import java.util.Timer;

public interface TimerHandler {

  public Timer sendEventAfter(int time, Object event);
  public Timer startTimer(int time, Object msg);
  public int cancelTimer(Timer timer);
}

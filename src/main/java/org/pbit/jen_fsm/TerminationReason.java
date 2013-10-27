package org.pbit.jen_fsm;

public final class TerminationReason {
  
  public static int NORMAL = 1;
  
  private int tag;
  private Object payload;
  
  public TerminationReason(int tag, Object payload) {
    super();
    this.tag = tag;
    this.payload = payload;
  }

  public int getTag() {
    return tag;
  }

  public Object getPayload() {
    return payload;
  }
}

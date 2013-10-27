package org.pbit.jen_fsm;

public final class Return {
  private int tag;
  private Object payload;
  private String nextStateName;
  
  public Return(int tag, Object payload) {
    super();
    this.tag = tag;
    this.payload = payload;
  }
  
  public Return(int tag, Object payload, String nextStateName) {
    super();
    this.tag = tag;
    this.payload = payload;
    this.nextStateName = nextStateName;
  }
  
  public Return(int tag, String nextStateName) {
    super();
    this.tag = tag;
    this.nextStateName = nextStateName;
  }
  
  public int getTag() {
    return tag;
  }
  
  public Object getPayload() {
    return payload;
  }
  
  public String getNextStateName() {
    return nextStateName;
  }
}

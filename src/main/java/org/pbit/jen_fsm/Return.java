package org.pbit.jen_fsm;

public final class Return {
  private String tag;
  private Object payload;
  private String nextStateName;
  
  public Return(String tag, Object payload) {
    super();
    this.tag = tag;
    this.payload = payload;
  }
  
  public Return(String tag, Object payload, String nextStateName) {
    super();
    this.tag = tag;
    this.payload = payload;
    this.nextStateName = nextStateName;
  }
  
  public Return(String tag, String nextStateName) {
    super();
    this.tag = tag;
    this.nextStateName = nextStateName;
  }
  
  public String getTag() {
    return tag;
  }
  
  public Object getPayload() {
    return payload;
  }
  
  public String getNextStateName() {
    return nextStateName;
  }
}

package org.pbit.jen_fsm;

public final class Tuple {
  private String tag;
  private Object payload;
  
  public Tuple(String tag, Object payload) {
    super();
    this.tag = tag;
    this.payload = payload;
  }
  
  public String getTag() {
    return tag;
  }
  public Object getPayload() {
    return payload;
  }
}

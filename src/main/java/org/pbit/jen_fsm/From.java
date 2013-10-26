package org.pbit.jen_fsm;

public class From {
  protected Callback caller;
  protected String tag;
  
  public From(Callback caller, String tag) {
    super();
    this.caller = caller;
    this.tag = tag;
  }

  public Callback getCaller() {
    return caller;
  }

  public String getTag() {
    return tag;
  }
}

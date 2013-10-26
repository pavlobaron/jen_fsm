package org.pbit.jen_fsm;

public final class From {
  private ReplyHandler caller;
  private String tag;
  private Object reply;
  
  public From(ReplyHandler caller, String tag) {
    super();
    this.caller = caller;
    this.tag = tag;
  }

  public void reply(Object reply) {
    this.reply = reply;
    caller.reply(reply, tag);
  }

  public Object getMostRecentReply() {
    return reply;
  }
}

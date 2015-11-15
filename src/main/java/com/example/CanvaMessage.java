package com.example;

import com.google.common.base.Joiner;

public class CanvaMessage {
  public static int VISIBILITY_TIMEOUT = 5000;

  private String content;
  private long timeout;

  public CanvaMessage(String content) {
    this.content = content;
  }

  public CanvaMessage(String content, long timeout) {
    this.content = content;
    this.timeout = timeout;
  }

  public String getContent() {
    return this.content;
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long receivedTime) {
    this.timeout = receivedTime + VISIBILITY_TIMEOUT;
  }

  public boolean isVisible() {
    return System.currentTimeMillis() - timeout > 0;
  }

  public String toString() {
    return Joiner.on(":").join(timeout, content);
  }

  @Override
  public boolean equals(Object other){
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof CanvaMessage))return false;
    CanvaMessage otherMessage = (CanvaMessage)other;
    return content.equals(otherMessage.getContent()) && timeout == otherMessage.getTimeout();
  }
}

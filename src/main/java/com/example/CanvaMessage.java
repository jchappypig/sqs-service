package com.example;

public class CanvaMessage {
  public static int VISIBILITY_TIMEOUT = 5000;

  private String content;
  private long timeout;

  public CanvaMessage(String content) {
    this.content = content;
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
}

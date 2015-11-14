package com.example;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class MessageTest {


  @Test
  public void isVisible_shouldReturn_true_IfOutOfVisibilityTimeOutPeriod() {
    Message message = new Message("Hello Canva");
    message.setTimeout(System.currentTimeMillis() - 100000);

    boolean isVisible = message.isVisible();

    assertEquals(true, isVisible);
  }

  @Test
  public void isVisible_shouldReturn_false_IfWithinVisibilityTimeOutPeriod() {
    Message message = new Message("Hello Canva");
    message.setTimeout(System.currentTimeMillis() + 100000);

    boolean isVisible = message.isVisible();

    assertEquals(false, isVisible);
  }
}

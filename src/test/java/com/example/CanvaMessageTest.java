package com.example;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CanvaMessageTest {


  @Test
  public void isVisible_shouldReturn_true_IfOutOfVisibilityTimeOutPeriod() {
    CanvaMessage message = new CanvaMessage("Hello Canva");
    message.setTimeout(System.currentTimeMillis() - 100000);

    boolean isVisible = message.isVisible();

    assertEquals(true, isVisible);
  }

  @Test
  public void isVisible_shouldReturn_false_IfWithinVisibilityTimeOutPeriod() {
    CanvaMessage message = new CanvaMessage("Hello Canva");
    message.setTimeout(System.currentTimeMillis() + 100000);

    boolean isVisible = message.isVisible();

    assertEquals(false, isVisible);
  }
}

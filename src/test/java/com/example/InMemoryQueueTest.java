package com.example;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class InMemoryQueueTest {
  InMemoryQueueService inMemoryQueueService;

  @Before
  public void setup() {
    inMemoryQueueService = new InMemoryQueueService();
  }

  @Test
  public void push_validMessageContent_shouldReturn_true() {
    boolean result = inMemoryQueueService.push("Hello Canva");

    assertEquals(true, result);
  }

  @Test
  public void push_invalidMessageContent_shouldReturn_false() {
    boolean result = inMemoryQueueService.push(null);

    assertEquals(false, result);
  }

  @Test
  public void pull_shouldReturn_pushedMessage() {
    inMemoryQueueService.push("Hello Canva");

    Message pulledMessage = inMemoryQueueService.pull();

    assertNotNull(pulledMessage);
    assertEquals("Hello Canva", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifQueueIsEmpty() {
    Message pulledMessage = inMemoryQueueService.pull();

    assertNull(pulledMessage);
  }

  @Test
  public void pull_shouldFollow_firstInFirstOut() {
    inMemoryQueueService.push("Hello World");
    inMemoryQueueService.push("Hello Canva");

    Message pulledMessage = inMemoryQueueService.pull();
    assertEquals("Hello World", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldAlwaysGetBack_theSameMessage_ifItHasNotBeenRemoved() {
    inMemoryQueueService.push("Hello World");
    inMemoryQueueService.push("Hello Canva");
    Message pulledMessageFirstTime = inMemoryQueueService.pull();

    Message pulledMessageSecondTime = inMemoryQueueService.pull();

    assertEquals("Hello World", pulledMessageSecondTime.getContent());
  }
}

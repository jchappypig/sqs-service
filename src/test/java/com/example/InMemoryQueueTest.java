package com.example;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

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
  public void pull_shouldGetBack_nextVisibleMessage() {
    inMemoryQueueService.push("Hello World");
    inMemoryQueueService.push("Hello Canva");
    Message pulledMessageFirstTime = inMemoryQueueService.pull();

    Message pulledMessageSecondTime = inMemoryQueueService.pull();

    assertEquals("Hello World", pulledMessageSecondTime.getContent());
  }

  @Test
  public void pull_shouldSet_messageVisibilityTimeout() {
    inMemoryQueueService.push("Hello World");

    Message pulledMessage = inMemoryQueueService.pull();
    assertTrue(pulledMessage.getTimeout() != 0);
  }

  @Test
  public void delete_shouldRemove_foundMessageFromQueue() {
    inMemoryQueueService.push("Hello World");
    Message messageToBeDeleted = inMemoryQueueService.pull();

    inMemoryQueueService.delete(messageToBeDeleted);

    assertNull(inMemoryQueueService.pull());
  }

  @Test
  public void delete_shouldNotRemove_anyMessage_IfNotFound() {
    inMemoryQueueService.push("Hello World");
    Message messageToBeDeleted = new Message("Hello Non exist");

    inMemoryQueueService.delete(messageToBeDeleted);

    assertNotNull(inMemoryQueueService.pull());
  }

  @Test
  public void delete_shouldReturn_true_IfMessageIsFound() {
    inMemoryQueueService.push("Hello World");
    Message messageToBeDeleted = inMemoryQueueService.pull();

    boolean result = inMemoryQueueService.delete(messageToBeDeleted);

    assertEquals(true, result);
  }

  @Test
  public void delete_shouldReturn_false_IfMessageNotFound() {
    inMemoryQueueService.push("Hello World");
    Message messageToBeDeleted = new Message("Hello Non exist");

    boolean result = inMemoryQueueService.delete(messageToBeDeleted);

    assertEquals(false, result);
  }

  @Test
  public void isVisible_shouldReturn_true_IfOutOfVisibilityTimeOutPeriod() {
    inMemoryQueueService.push("Hello World");
    long currentTime = System.currentTimeMillis();
    Message message = inMemoryQueueService.pull();
    message.setTimeout(currentTime - 100000);

    boolean isVisible = message.isVisible();

    assertEquals(true, isVisible);
  }

  @Test
  public void isVisible_shouldReturn_false_IfWithinVisibilityTimeOutPeriod() {
    inMemoryQueueService.push("Hello World");
    long currentTime = System.currentTimeMillis();
    Message message = inMemoryQueueService.pull();
    message.setTimeout(currentTime + 100000);

    boolean isVisible = message.isVisible();

    assertEquals(false, isVisible);
  }
}

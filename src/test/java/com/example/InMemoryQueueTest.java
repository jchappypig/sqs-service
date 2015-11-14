package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

import static junit.framework.Assert.*;

public class InMemoryQueueTest {
  private InMemoryQueueService inMemoryQueueService;
  private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();

  @Before
  public void setup() {
    inMemoryQueueService = new InMemoryQueueService();
  }

  @Test
  public void push_validMessageContent_shouldReturn_true() {
    boolean result = inMemoryQueueService.push("Hello Canva", queue);

    assertEquals(true, result);
  }

  @Test
  public void push_invalidMessageContent_shouldReturn_false() {
    boolean result = inMemoryQueueService.push(null, queue);

    assertEquals(false, result);
  }

  @Test
  public void pull_shouldReturn_pushedMessage() {
    inMemoryQueueService.push("Hello Canva", queue);

    Message pulledMessage = inMemoryQueueService.pull(queue);

    assertNotNull(pulledMessage);
    assertEquals("Hello Canva", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifPushedMessageIsInvisible() {
    inMemoryQueueService.push("Hello Canva", queue);
    inMemoryQueueService.pull(queue);

    Message pulledMessage = inMemoryQueueService.pull(queue);
    assertNull(pulledMessage);
  }

  @Test
  public void pull_shouldReturn_ThePushedMessage_IfItBecomeVisibleAgain() {
    inMemoryQueueService.push("Hello Canva", queue);
    Message message = inMemoryQueueService.pull(queue);
    message.setTimeout(System.currentTimeMillis() - 100000);

    Message pulledMessage = inMemoryQueueService.pull(queue);
    assertEquals("Hello Canva", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifQueueIsEmpty() {
    Message pulledMessage = inMemoryQueueService.pull(queue);

    assertNull(pulledMessage);
  }

  @Test
  public void pull_shouldFollow_firstInFirstOut() {
    inMemoryQueueService.push("Hello World", queue);
    inMemoryQueueService.push("Hello Canva", queue);

    Message pulledMessage = inMemoryQueueService.pull(queue);
    assertEquals("Hello World", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldSet_messageVisibilityTimeout() {
    inMemoryQueueService.push("Hello World", queue);

    Message pulledMessage = inMemoryQueueService.pull(queue);
    assertTrue(pulledMessage.getTimeout() != 0);
  }

  @Test
  public void delete_shouldRemove_foundMessageFromQueue() {
    inMemoryQueueService.push("Hello World", queue);
    Message messageToBeDeleted = inMemoryQueueService.pull(queue);

    inMemoryQueueService.delete(messageToBeDeleted, queue);

    assertNull(inMemoryQueueService.pull(queue));
  }

  @Test
  public void delete_shouldNotRemove_anyMessage_IfNotFound() {
    inMemoryQueueService.push("Hello World", queue);
    Message messageToBeDeleted = new Message("Hello Non exist");

    inMemoryQueueService.delete(messageToBeDeleted, queue);

    assertNotNull(inMemoryQueueService.pull(queue));
  }

  @Test
  public void delete_shouldReturn_true_IfMessageIsFound() {
    inMemoryQueueService.push("Hello World", queue);
    Message messageToBeDeleted = inMemoryQueueService.pull(queue);

    boolean result = inMemoryQueueService.delete(messageToBeDeleted, queue);

    assertEquals(true, result);
  }

  @Test
  public void delete_shouldReturn_false_IfMessageNotFound() {
    inMemoryQueueService.push("Hello World", queue);
    Message messageToBeDeleted = new Message("Hello Non exist");

    boolean result = inMemoryQueueService.delete(messageToBeDeleted, queue);

    assertEquals(false, result);
  }

  @Test
  public void isVisible_shouldReturn_true_IfOutOfVisibilityTimeOutPeriod() {
    inMemoryQueueService.push("Hello World", queue);
    long currentTime = System.currentTimeMillis();
    Message message = inMemoryQueueService.pull(queue);
    message.setTimeout(currentTime - 100000);

    boolean isVisible = message.isVisible();

    assertEquals(true, isVisible);
  }

  @Test
  public void isVisible_shouldReturn_false_IfWithinVisibilityTimeOutPeriod() {
    inMemoryQueueService.push("Hello World", queue);
    long currentTime = System.currentTimeMillis();
    Message message = inMemoryQueueService.pull(queue);
    message.setTimeout(currentTime + 100000);

    boolean isVisible = message.isVisible();

    assertEquals(false, isVisible);
  }
}

package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
  public void push_validMessageContent_shouldAddMessageToTheQueue() {
    queue.clear();

    inMemoryQueueService.push("Hello Canva", queue);

    assertEquals(1, queue.size());
  }

  @Test
  public void push_invalidMessageContent_shouldReturn_false() {
    boolean result = inMemoryQueueService.push(null, queue);

    assertEquals(false, result);
  }

  @Test
  public void push_invalidMessageContent_shouldNotAddMessageToTheQueue() {
    queue.clear();

    inMemoryQueueService.push(null, queue);

    assertEquals(0, queue.size());
  }

  @Test
  public void pull_shouldReturn_messageFromQueue_ifMessageFromQueueIsVisible() {
    Message message = mock(Message.class);
    when(message.isVisible()).thenReturn(true);
    queue.add(message);

    Message pulledMessage = inMemoryQueueService.pull(queue);

    assertEquals(message, pulledMessage);
  }

  @Test
  public void pull_shouldReturn_null_ifMessageFromQueueIsNotVisible() {
    Message message = mock(Message.class);
    when(message.isVisible()).thenReturn(false);
    queue.add(message);

    Message pulledMessage = inMemoryQueueService.pull(queue);
    assertNull(pulledMessage);
  }

  @Test
  public void pull_shouldReturn_null_ifQueueIsEmpty() {
    queue.clear();

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
}

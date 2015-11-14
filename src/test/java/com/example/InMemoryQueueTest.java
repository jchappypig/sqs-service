package com.example;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class InMemoryQueueTest {
  private InMemoryQueueService inMemoryQueueService;

  @Before
  public void setup() {
    inMemoryQueueService = new InMemoryQueueService();
    inMemoryQueueService.createQueue("canvaQueue");
  }

  @Test
  public void pull_shouldReturn_messageFromQueue_ifMessageFromQueueIsVisible() {
    inMemoryQueueService.push("canvaQueue", "Hello Canva");

    CanvaMessage pulledMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertEquals("Hello Canva", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifMessageFromQueueIsNotVisible() {
    inMemoryQueueService.push("canvaQueue", "Hello Canva");
    inMemoryQueueService.pull("canvaQueue");

    CanvaMessage pulledMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertNull(pulledMessage);
  }

  @Test
  public void pull_shouldReturn_messageFromQueue_ifMessageBecomeVisibleAgain() {
    inMemoryQueueService.push("canvaQueue", "Hello Canva");
    CanvaMessage message = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");
    message.setTimeout(System.currentTimeMillis() - 100000);

    CanvaMessage pulledMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertEquals("Hello Canva", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifQueueIsEmpty() {
    CanvaMessage pulledMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertNull(pulledMessage);
  }

  @Test
  public void pull_shouldFollow_firstInFirstOut() {
    inMemoryQueueService.push("canvaQueue", "Hello World");
    inMemoryQueueService.push("canvaQueue", "Hello Canva");

    CanvaMessage pulledMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");
    assertEquals("Hello World", pulledMessage.getContent());
  }

  @Test
  public void pull_shouldSet_messageVisibilityTimeout() {
    inMemoryQueueService.push("canvaQueue", "Hello World");

    CanvaMessage pulledMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");
    assertTrue(pulledMessage.getTimeout() != 0);
  }

  @Test
  public void delete_shouldRemove_foundMessageFromQueue() {
    inMemoryQueueService.push("canvaQueue", "Hello World");
    CanvaMessage messageToBeDeleted = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    inMemoryQueueService.delete("canvaQueue", messageToBeDeleted);

    assertNull(inMemoryQueueService.pull("canvaQueue"));
  }

  @Test
  public void delete_shouldNotRemove_anyMessage_IfNotFound() {
    inMemoryQueueService.push("canvaQueue", "Hello World");
    CanvaMessage messageToBeDeleted = new CanvaMessage("Hello Non exist");

    inMemoryQueueService.delete("canvaQueue", messageToBeDeleted);

    assertNotNull(inMemoryQueueService.pull("canvaQueue"));
  }
}

package com.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InMemoryQueueTest {
  private InMemoryQueueService inMemoryQueueService;

  @Before
  public void setup() {
    inMemoryQueueService = new InMemoryQueueService();
    inMemoryQueueService.createQueue("canvaQueue", "Hello Canva");
  }

  @Test
  public void pull_shouldReturn_messageFromQueue_ifMessageFromQueueIsVisible() {
    CanvaMessage receivedMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertEquals("Hello Canva", receivedMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifMessageFromQueueIsNotVisible() {
    inMemoryQueueService.pull("canvaQueue");

    CanvaMessage receivedMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertNull(receivedMessage);
  }

  @Test
  public void pull_shouldReturn_messageFromQueue_ifMessageBecomeVisibleAgain() {
    CanvaMessage message = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");
    message.setTimeout(System.currentTimeMillis() - 100000);

    CanvaMessage receivedMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertEquals("Hello Canva", receivedMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_nextVisibleMessage_ifFirstMessageIsNotVisible() {
    inMemoryQueueService.push("canvaQueue", "Hello World");
    inMemoryQueueService.pull("canvaQueue");

    CanvaMessage receivedMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertEquals("Hello World", receivedMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifQueueIsEmpty() {
    CanvaMessage message = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");
    inMemoryQueueService.delete("canvaQueue", message);
    CanvaMessage receivedMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    assertNull(receivedMessage);
  }

  @Test
  public void pull_shouldFollow_firstInFirstOut() {
    inMemoryQueueService.push("canvaQueue", "Hello World");

    CanvaMessage receivedMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");
    assertEquals("Hello Canva", receivedMessage.getContent());
  }

  @Test
  public void pull_shouldSet_messageVisibilityTimeout() {
    CanvaMessage receivedMessage = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");
    assertTrue(receivedMessage.getTimeout() != 0);
  }

  @Test
  public void delete_shouldRemove_foundMessageFromQueue() {
    CanvaMessage messageToBeDeleted = (CanvaMessage) inMemoryQueueService.pull("canvaQueue");

    inMemoryQueueService.delete("canvaQueue", messageToBeDeleted);

    assertNull(inMemoryQueueService.pull("canvaQueue"));
  }

  @Test
  public void delete_shouldNotRemove_anyMessage_IfNotFound() {
    CanvaMessage messageToBeDeleted = new CanvaMessage("Hello Non exist");

    inMemoryQueueService.delete("canvaQueue", messageToBeDeleted);

    assertNotNull(inMemoryQueueService.pull("canvaQueue"));
  }
}

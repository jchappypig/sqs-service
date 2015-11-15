package com.example;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class FileQueueTest {
  FileHandler fileHandler;
  FileQueueService fileQueueService;

  @Before
  public void setup() {
    fileHandler = mock(FileHandler.class);
    fileQueueService = new FileQueueService(fileHandler);
  }

  @Test
  public void createQueue_shouldSend_initialMessage() throws IOException, InterruptedException {
    fileQueueService.createQueue("canvaQueue", "Hello Canva");

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).createQueue("canvaQueue");
    verify(fileHandler).writeAllMessages(anyString(), any(List.class));
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void createQueue_shouldStillUnlock_File_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).createQueue("canvaQueue");

    fileQueueService.createQueue("canvaQueue", "Hello Canva");

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void createQueue_shouldReturn_true_ifSuccessful() throws IOException, InterruptedException {
    boolean result = fileQueueService.createQueue("canvaQueue", "Hello Canva");

    assertEquals(true, result);
  }

  @Test
  public void createQueue_shouldReturn_False_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).createQueue("canvaQueue");

    boolean result = fileQueueService.createQueue("canvaQueue", "Hello Canva");

    assertEquals(false, result);
  }

  @Test
  public void push_shouldSend_message() throws IOException, InterruptedException {
    fileQueueService.push("canvaQueue", "Hello Canva");

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).appendMessage("canvaQueue", "Hello Canva");
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void push_shouldStillUnlock_File_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).appendMessage("canvaQueue", "Hello Canva");

    fileQueueService.push("canvaQueue", "Hello Canva");

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void push_shouldReturn_true_ifSuccessful() throws IOException, InterruptedException {
    boolean result = fileQueueService.push("canvaQueue", "Hello Canva");

    assertEquals(true, result);
  }

  @Test
  public void push_shouldReturn_False_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).appendMessage("canvaQueue", "Hello Canva");

    boolean result = fileQueueService.push("canvaQueue", "Hello Canva");

    assertEquals(false, result);
  }

  @Test
  public void pull_shouldUpdate_messages() throws IOException, InterruptedException {
    fileQueueService.pull("canvaQueue");

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).readAllMessages("canvaQueue");
    verify(fileHandler).writeAllMessages(anyString(), any(List.class));
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void pull_shouldStillUnlock_File_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).writeAllMessages(anyString(), any(List.class));

    fileQueueService.pull("canvaQueue");

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void pull_shouldReturn_message_ifSuccessful() throws IOException, InterruptedException {
    when(fileHandler.readAllMessages("canvaQueue")).thenReturn(Lists.newArrayList(new CanvaMessage("Hello Canva")));
    CanvaMessage receivedMessage = (CanvaMessage) fileQueueService.pull("canvaQueue");

    assertEquals("Hello Canva", receivedMessage.getContent());
  }

  @Test
  public void pull_shouldReturn_null_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).createQueue("canvaQueue");

    CanvaMessage receivedMessage = (CanvaMessage) fileQueueService.pull("canvaQueue");

    assertNull(receivedMessage);
  }

  @Test
  public void delete_shouldUpdate_messages() throws IOException, InterruptedException {
    fileQueueService.delete("canvaQueue", new CanvaMessage("Hello Canva"));

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).readAllMessages("canvaQueue");
    verify(fileHandler).writeAllMessages(anyString(), any(List.class));
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void delete_shouldStillUnlock_File_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).createQueue("canvaQueue");

    fileQueueService.delete("canvaQueue", new CanvaMessage("Hello Canva"));

    verify(fileHandler).lock("canvaQueue");
    verify(fileHandler).unlock("canvaQueue");
  }

  @Test
  public void delete_shouldReturn_true_ifSuccessful() throws IOException, InterruptedException {
    boolean result = fileQueueService.delete("canvaQueue", new CanvaMessage("Hello Canva"));

    assertEquals(true, result);
  }

  @Test
  public void delete_shouldReturn_False_ifExceptionFound() throws IOException, InterruptedException {
    doThrow(new IOException()).when(fileHandler).readAllMessages("canvaQueue");

    boolean result = fileQueueService.delete("canvaQueue", new CanvaMessage("Hello Canva"));

    assertEquals(false, result);
  }

  @Test
  public void integration_flow() throws Exception {
    FileHandler fileHandler = new FileHandler("test");
    FileQueueService fileQueueService = new FileQueueService(fileHandler);

    // send three messages
    fileQueueService.createQueue("canvaQueue", "Hello Canva");
    fileQueueService.push("canvaQueue", "Hello World");
    fileQueueService.push("canvaQueue", "Hello Global");
    assertEquals(3, fileHandler.readAllMessages("canvaQueue").size());

    // pull, the first time.
    CanvaMessage firstMessage = (CanvaMessage) fileQueueService.pull("canvaQueue");
    assertEquals("Hello Canva", firstMessage.getContent());

    // pull, the second time.
    CanvaMessage secondMessage = (CanvaMessage) fileQueueService.pull("canvaQueue");
    assertEquals("Hello World", secondMessage.getContent());

    // delete message received when pull the second time
    fileQueueService.delete("canvaQueue", secondMessage);
    assertEquals(2, fileHandler.readAllMessages("canvaQueue").size());

    // cleanup
    fileHandler.deleteQueue("canvaQueue");
  }
}





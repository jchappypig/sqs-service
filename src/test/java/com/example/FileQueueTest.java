package com.example;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FileQueueTest {
  //
  // Implement me if you have time.
  //
  private FileQueueService fileQueueService;

  @Before
  public void setup() {
  }

  @Test
  public void createQueue_shouldWrite_messageToFile() throws Exception {
    fileQueueService = new FileQueueService();
    fileQueueService.createQueue("canvaQueue", "Hello Canva");
    fileQueueService.push("canvaQueue", "Hello World");
    fileQueueService.push("canvaQueue", "Hello Global");
    CanvaMessage message = (CanvaMessage) fileQueueService.pull("canvaQueue");
    assertEquals("Hello Canva", message.getContent());
    message = (CanvaMessage) fileQueueService.pull("canvaQueue");
    assertEquals("Hello World", message.getContent());
    fileQueueService.delete("canvaQueue", message);
  }
}





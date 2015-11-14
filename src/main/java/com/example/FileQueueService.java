package com.example;

import java.util.Queue;

public class FileQueueService implements QueueService {
  public Message pull(Queue<Message> queue) {
    return null;
  }

  public boolean push(String message, Queue<Message> queue) {
    return true;
  }

  public boolean delete(Message message, Queue<Message> queue) {
    return false;
  }

  //
  // Task 3: Implement me if you have time.
  //
}

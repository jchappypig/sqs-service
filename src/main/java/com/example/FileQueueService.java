package com.example;

public class FileQueueService implements QueueService {
  public Message pull() {
    return null;
  }

  public boolean push(String message) {
    return true;
  }

  public boolean delete(Message message) {
    return false;
  }

  //
  // Task 3: Implement me if you have time.
  //
}

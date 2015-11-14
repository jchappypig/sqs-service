package com.example;

import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryQueueService implements QueueService {

  private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();

  public Message pull() {
    Message message =  queue.peek();
    if(message != null && message.isVisible()) {
      message.setTimeout(System.currentTimeMillis());
      return message;
    }
    return null;
  }

  public boolean push(String messageContent) {
    if (messageContent == null) {
      return false;
    }
    queue.add(new Message(messageContent));
    return true;
  }

  public boolean delete(Message message) {
    return queue.remove(message);
  }
}

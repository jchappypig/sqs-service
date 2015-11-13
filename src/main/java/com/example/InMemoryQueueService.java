package com.example;


import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryQueueService implements QueueService {

  ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();

  public Message pull() {
    return queue.peek();
  }

  public boolean push(String messageContent) {
    if(messageContent == null) {
      return false;
    }
    queue.add(new Message(messageContent));
    return true;
  }
}

package com.example;

import java.util.Queue;

public class InMemoryQueueService implements QueueService {

  public Message pull(Queue<Message> queue) {
    Message message =  queue.peek();
    if(message != null && message.isVisible()) {
      message.setTimeout(System.currentTimeMillis());
      return message;
    }
    return null;
  }

  public boolean push(String messageContent, Queue<Message> queue) {
    if (messageContent == null) {
      return false;
    }
    queue.add(new Message(messageContent));
    return true;
  }

  public boolean delete(Message message, Queue<Message> queue) {
    return queue.remove(message);
  }
}

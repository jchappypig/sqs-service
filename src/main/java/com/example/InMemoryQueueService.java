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

  public void push(String messageContent, Queue<Message> queue) {
    queue.add(new Message(messageContent));
  }

  public void delete(Message message, Queue<Message> queue) {
    queue.remove(message);
  }
}

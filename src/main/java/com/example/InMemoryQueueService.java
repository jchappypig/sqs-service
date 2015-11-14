package com.example;

import java.util.*;

public class InMemoryQueueService implements QueueService {
  private Map<String, List<Message>> queues = new HashMap<String, List<Message>>();

  public Message pull(String queueName) {
    List<Message> myQueue = queues.get(queueName);
    Message message = myQueue.size() > 0 ? myQueue.get(0) : null;
    if (message != null && message.isVisible()) {
      message.setTimeout(System.currentTimeMillis());
      return message;
    }
    return null;
  }

  public void push(String queueName, String messageContent) {
    List<Message> myQueue = queues.get(queueName);
    myQueue.add(new Message(messageContent));
  }

  public void delete(String queueName, Message message) {
    List<Message> myQueue = queues.get(queueName);
    myQueue.remove(message);
  }

  public void createQueue(String queueName) {
    queues.put(queueName, new LinkedList<Message>());
  }
}

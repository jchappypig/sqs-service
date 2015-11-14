package com.example;

import java.util.*;

public class InMemoryQueueService implements QueueService {
  private Map<String, List<CanvaMessage>> queues = new HashMap<String, List<CanvaMessage>>();

  public Object pull(String queueName) {
    List<CanvaMessage> myQueue = queues.get(queueName);
    CanvaMessage message = myQueue.size() > 0 ? myQueue.get(0) : null;
    if (message != null && message.isVisible()) {
      message.setTimeout(System.currentTimeMillis());
      return message;
    }
    return null;
  }

  public void push(String queueName, String messageContent) {
    List<CanvaMessage> myQueue = queues.get(queueName);
    myQueue.add(new CanvaMessage(messageContent));
  }

  public void delete(String queueName, Object message) {
    List<CanvaMessage> myQueue = queues.get(queueName);
    myQueue.remove(message);
  }

  public void createQueue(String queueName) {
    queues.put(queueName, new LinkedList<CanvaMessage>());
  }
}

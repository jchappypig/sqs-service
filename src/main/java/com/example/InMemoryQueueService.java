package com.example;

import com.google.common.collect.*;

import java.util.*;

public class InMemoryQueueService implements QueueService {
  private Multimap<String, CanvaMessage> queues;

  public InMemoryQueueService() {
    LinkedListMultimap<String, CanvaMessage> linkedListMultimap = LinkedListMultimap.create();
    queues = Multimaps.synchronizedListMultimap(linkedListMultimap);
  }

  public Object pull(String queueName) {
    Collection<CanvaMessage> queue = queues.get(queueName);
    CanvaMessage message = Iterables.getFirst(queue, null);
    if (message != null && message.isVisible()) {
      message.setTimeout(System.currentTimeMillis());
      return message;
    }
    return null;
  }

  public void push(String queueName, String messageContent) {
    Collection<CanvaMessage> queue = queues.get(queueName);
    queue.add(new CanvaMessage(messageContent));
  }

  public void delete(String queueName, Object message) {
    Collection<CanvaMessage> queue = queues.get(queueName);
    queue.remove(message);
  }

  public void createQueue(String queueName, String messageContent) {
    queues.put(queueName, new CanvaMessage(messageContent));
  }
}

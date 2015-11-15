package com.example;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.NoSuchElementException;

public class InMemoryQueueService implements QueueService {
  private Multimap<String, CanvaMessage> queues;

  public InMemoryQueueService() {
    LinkedListMultimap<String, CanvaMessage> linkedListMultimap = LinkedListMultimap.create();
    queues = Multimaps.synchronizedListMultimap(linkedListMultimap);
  }

  public Object pull(String queueName) {
    Collection<CanvaMessage> queue = queues.get(queueName);
    return findFirstVisibleMessage(queue);
  }

  public boolean push(String queueName, String messageContent) {
    Collection<CanvaMessage> queue = queues.get(queueName);
    return queue.add(new CanvaMessage(messageContent));
  }

  public boolean delete(String queueName, Object message) {
    Collection<CanvaMessage> queue = queues.get(queueName);
    return queue.remove(message);
  }

  public boolean createQueue(String queueName, String messageContent) {
    return queues.put(queueName, new CanvaMessage(messageContent));
  }

  private CanvaMessage findFirstVisibleMessage(Collection<CanvaMessage> messages) {
    try {
      CanvaMessage message = Iterables.find(messages, msg -> msg.isVisible());
      message.setTimeout(System.currentTimeMillis());

      return message;

    } catch (NoSuchElementException e) {
      return null;
    }
  }
}

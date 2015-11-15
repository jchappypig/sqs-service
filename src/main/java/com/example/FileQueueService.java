package com.example;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.NoSuchElementException;

public class FileQueueService implements QueueService {
  private FileHandler fileHandler = new FileHandler();

  public Object pull(String queueName) {
    CanvaMessage message = null;
    try {
      fileHandler.lock(queueName);
      List<CanvaMessage> messages = fileHandler.readAllMessages(queueName);
      message = findFirstVisibleMessage(messages);
      fileHandler.writeAllMessages(queueName, messages);
    } catch (Exception e) {
      //do nothing for now
    } finally {
      fileHandler.unlock(queueName);
    }
    return message;
  }

  public void push(String queueName, String messageContent) {

    try {
      fileHandler.lock(queueName);
      fileHandler.appendMessage(queueName, messageContent);
    } catch (Exception e) {
    } finally {
      fileHandler.unlock(queueName);
    }
  }

  public void delete(String queueName, Object message) {
    try {
      fileHandler.lock(queueName);
      List<CanvaMessage> messages = fileHandler.readAllMessages(queueName);
      messages.remove(message);
      fileHandler.writeAllMessages(queueName, messages);
    } catch (Exception e) {
      //do nothing for now
    } finally {
      fileHandler.unlock(queueName);
    }
  }

  public void createQueue(String queueName, String messageContent) {
    try {
      fileHandler.lock(queueName);
      fileHandler.createMessagesFile(queueName);
      List<CanvaMessage> messages = Lists.newArrayList(new CanvaMessage(messageContent));
      fileHandler.writeAllMessages(queueName, messages);
    } catch (Exception e) {
      //do nothing for now
    } finally {
      fileHandler.unlock(queueName);
    }
  }

  private CanvaMessage findFirstVisibleMessage(List<CanvaMessage> messages) {
    try {
      CanvaMessage message = Iterables.find(messages, msg -> msg.isVisible());
      message.setTimeout(System.currentTimeMillis());

      return message;

    } catch (NoSuchElementException e) {
      return null;
    }
  }

  //
  // Task 3: Implement me if you have time.
  //
}

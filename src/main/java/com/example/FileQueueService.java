package com.example;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.NoSuchElementException;

public class FileQueueService implements QueueService {
  private FileHandler fileHandler = new FileHandler(null);

  public FileQueueService(FileHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  public Object pull(String queueName) {
    CanvaMessage message = null;

    try {
      fileHandler.lock(queueName);
      List<CanvaMessage> messages = fileHandler.readAllMessages(queueName);
      message = findFirstVisibleMessage(messages);
      fileHandler.writeAllMessages(queueName, messages);
    } catch (Exception e) {
      message = null;
    } finally {
      fileHandler.unlock(queueName);
    }

    return message;
  }

  public boolean push(String queueName, String messageContent) {
    boolean result = false;

    try {
      fileHandler.lock(queueName);
      fileHandler.appendMessage(queueName, messageContent);
      result = true;
    } catch (Exception e) {
      result = false;
    } finally {
      fileHandler.unlock(queueName);
    }

    return result;
  }

  public boolean delete(String queueName, Object message) {
    boolean result = false;

    try {
      fileHandler.lock(queueName);
      List<CanvaMessage> messages = fileHandler.readAllMessages(queueName);
      messages.remove(message);
      fileHandler.writeAllMessages(queueName, messages);
      result = true;
    } catch (Exception e) {
      result = false;
    } finally {
      fileHandler.unlock(queueName);
    }

    return result;
  }

  public boolean createQueue(String queueName, String messageContent) {
    boolean result = false;

    try {
      fileHandler.lock(queueName);
      fileHandler.createQueue(queueName);
      List<CanvaMessage> messages = Lists.newArrayList(new CanvaMessage(messageContent));
      fileHandler.writeAllMessages(queueName, messages);
      result = true;
    } catch (Exception e) {
      result = false;
    } finally {
      fileHandler.unlock(queueName);
    }

    return result;
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

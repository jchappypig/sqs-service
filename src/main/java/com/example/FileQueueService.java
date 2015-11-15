package com.example;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NoSuchElementException;

import static com.google.common.io.Files.touch;

public class FileQueueService implements QueueService {

  public static final Charset CHAR_SET = StandardCharsets.UTF_8;

  public Object pull(String queueName) {
    File lockFile = getLockFile(queueName);
    File messagesFile = getMessagesFile(queueName);
    CanvaMessage message = null;
    try {
      lock(lockFile);
      List<CanvaMessage> messages = readAllMessages(messagesFile);
      message = findFirstVisibleMessage(messages);
      writeAllMessages(messagesFile, messages);
    } catch (Exception e) {
      //do nothing for now
    } finally {
      unlock(lockFile);
    }
    return message;
  }

  private void writeAllMessages(File messagesFile, List<CanvaMessage> messages) throws IOException {
    String messagesToWrite = Joiner.on('\n').join(messages);
    Files.write(messagesToWrite, messagesFile, CHAR_SET);
  }

  private List<CanvaMessage> readAllMessages(File messagesFile) throws IOException {
    List<CanvaMessage> messages = Files.readLines(messagesFile, CHAR_SET, new LineProcessor<List<CanvaMessage>>() {
      final List<CanvaMessage> messages = Lists.newArrayList();

      @Override
      public boolean processLine(String line) throws IOException {
        CanvaMessage message = transformToMessage(line);
        messages.add(message);

        return true;
      }

      @Override
      public List<CanvaMessage> getResult() {
        return messages;
      }
    });

    return messages;
  }

  private CanvaMessage transformToMessage(String line) {
    Iterable<String> messageInfo = Splitter.on(':').split(line);
    String content = Iterables.get(messageInfo, 1);
    Long timeout =  Long.parseLong(Iterables.get(messageInfo, 0), 10) ;

    return new CanvaMessage(content, timeout);
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

  public void push(String queueName, String messageContent) {
    File lockFile = getLockFile(queueName);
    File messagesFile = getMessagesFile(queueName);

    try {
      lock(lockFile);
      appendMessage(messagesFile, messageContent);
    } catch (Exception e) {
    } finally {
      unlock(lockFile);
    }
  }

  public void delete(String queueName, Object message) {
    File lockFile = getLockFile(queueName);
    File messagesFile = getMessagesFile(queueName);
    try {
      lock(lockFile);
      List<CanvaMessage> messages = readAllMessages(messagesFile);
      messages.remove(message);
      writeAllMessages(messagesFile, messages);
    } catch (Exception e) {
      //do nothing for now
    } finally {
      unlock(lockFile);
    }
  }

  public void createQueue(String queueName, String messageContent) {
    File messagesFile = getMessagesFile(queueName);
    File lockFile = getLockFile(queueName);
    try {
      createFile(messagesFile);
      lock(lockFile);
      List<CanvaMessage> messages = Lists.newArrayList(new CanvaMessage(messageContent));
      writeAllMessages(messagesFile, messages);
    } catch (Exception e) {
      //do nothing for now
    } finally {
      unlock(lockFile);
    }
  }

  public void appendMessage(File messagesFile, String messageContent) throws IOException {
    String message =  '\n' + new CanvaMessage(messageContent).toString();
    java.nio.file.Files.write(messagesFile.toPath(), message.getBytes(), StandardOpenOption.APPEND);
  }

  public void createFile(File messagesFile) throws IOException {
    Files.createParentDirs(messagesFile);
    touch(messagesFile);
  }


  public void lock(File lock) throws InterruptedException {
    while (!lock.mkdir()) {
      Thread.sleep(50);
    }
  }

  public void unlock(File lock) {
    lock.delete();
  }

  private File getMessagesFile(String queueName) {
    return new File(queueName + "/messages");
  }

  private File getLockFile(String queueName) {
    return new File(queueName + "/.lock");
  }

  //
  // Task 3: Implement me if you have time.
  //
}

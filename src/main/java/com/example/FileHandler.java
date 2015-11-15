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

import static com.google.common.io.Files.touch;

public class FileHandler {
  public static final Charset CHAR_SET = StandardCharsets.UTF_8;

  public List<CanvaMessage> readAllMessages(String queueName) throws IOException {
    return Files.readLines(getMessagesFile(queueName), CHAR_SET, createMessageProcessor());
  }

  public void createMessagesFile(String queueName) throws IOException {
    File messagesFile = getMessagesFile(queueName);
    Files.createParentDirs(messagesFile);
    touch(messagesFile);
  }

  public void appendMessage(String queueName, String messageContent) throws IOException {
    String message =  '\n' + new CanvaMessage(messageContent).toString();
    java.nio.file.Files.write(getMessagesFile(queueName).toPath(), message.getBytes(), StandardOpenOption.APPEND);
  }

  public void writeAllMessages(String queueName, List<CanvaMessage> messages) throws IOException {
    String messagesToWrite = Joiner.on('\n').join(messages);
    Files.write(messagesToWrite, getMessagesFile(queueName), CHAR_SET);
  }

  public void lock(String queueName) throws InterruptedException, IOException {
    File lock = getLockFile(queueName);
    Files.createParentDirs(lock);
    while (!lock.mkdir()) {
      Thread.sleep(50);
    }
  }

  public void unlock(String queueName) {
    File lock = getLockFile(queueName);
    lock.delete();
  }

  public File getLockFile(String queueName) {
    return new File(queueName + "/.lock");
  }

  public File getMessagesFile(String queueName) {
    return new File(queueName + "/messages");
  }

  private LineProcessor<List<CanvaMessage>> createMessageProcessor() {
    return new LineProcessor<List<CanvaMessage>>() {
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
    };
  }

  private CanvaMessage transformToMessage(String line) {
    Iterable<String> messageInfo = Splitter.on(':').split(line);
    String content = Iterables.get(messageInfo, 1);
    Long timeout =  Long.parseLong(Iterables.get(messageInfo, 0), 10) ;

    return new CanvaMessage(content, timeout);
  }
}

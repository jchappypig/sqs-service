package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import java.util.List;

public class SqsQueueService implements QueueService {
  private AmazonSQSClient sqs;

  public SqsQueueService(AmazonSQSClient sqsClient) {
    this.sqs = sqsClient;
  }

  public Object pull(String queueName) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl.getQueueUrl());
    List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    return messages.get(0);
  }

  public boolean push(String queueName, String message) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    return sqs.sendMessage(new SendMessageRequest(queueUrl.getQueueUrl(), message)).getMessageId() != null;
  }

  public boolean delete(String queueName, Object message) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    Message sqsMessage = (Message)message;
    sqs.deleteMessage(new DeleteMessageRequest(queueUrl.getQueueUrl(), sqsMessage.getReceiptHandle()));
    return true;
  }

  public boolean createQueue(String queueName, String messageContent) {
    return sqs.createQueue(queueName).getQueueUrl() != null;
  }

}

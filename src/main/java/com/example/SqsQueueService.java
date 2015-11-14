package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import java.util.List;

public class SqsQueueService implements QueueService {
  private AmazonSQSClient sqs;
  //
  // Task 4: Optionally implement parts of me.
  //
  // This file is a placeholder for an AWS-backed implementation of QueueService.  It is included
  // primarily so you can quickly assess your choices for method signatures in QueueService in
  // terms of how well they map to the implementation intended for a production environment.
  //

  public SqsQueueService(AmazonSQSClient sqsClient) {
    this.sqs = sqsClient;
  }

  public Object pull(String queueName) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl.getQueueUrl());
    List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    return messages.get(0);
  }

  public void push(String queueName, String message) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    sqs.sendMessage(new SendMessageRequest(queueUrl.getQueueUrl(), message));
  }

  public void delete(String queueName, Object message) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    Message sqsMessage = (Message)message;
    sqs.deleteMessage(new DeleteMessageRequest(queueUrl.getQueueUrl(), sqsMessage.getReceiptHandle()));
  }

  public void createQueue(String queueName, String messageContent) {
    sqs.createQueue(queueName);
  }

}

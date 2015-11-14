package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

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

  public Message pull(String queueName) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl.getQueueUrl());
    List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    messages.get(0).getBody();
    return null;
  }

  public void push(String queueName, String message) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    sqs.sendMessage(new SendMessageRequest(queueUrl.getQueueUrl(), message));
  }

  public void delete(String queueName, Message message) {
    GetQueueUrlResult queueUrl = sqs.getQueueUrl(queueName);
    sqs.deleteMessage(new DeleteMessageRequest(queueUrl.getQueueUrl(), "messageRecieptHandle"));
  }

  public void createQueue(String queueName) {
    sqs.createQueue(queueName);
  }

}

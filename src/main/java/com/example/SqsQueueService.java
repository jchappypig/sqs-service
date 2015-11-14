package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.Queue;

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

  public Message pull(Queue<Message> queue) {
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest("myQueueUrl");
    List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    messages.get(0).getBody();
    return null;
  }

  public void push(String message, Queue<Message> queue) {
    sqs.sendMessage(new SendMessageRequest("myQueueUrl", message));
  }

  public void delete(Message message, Queue<Message> queue) {
    sqs.deleteMessage(new DeleteMessageRequest("myQueueUrl", "messageRecieptHandle"));
  }
}

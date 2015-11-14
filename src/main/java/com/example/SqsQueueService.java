package com.example;

import com.amazonaws.services.sqs.AmazonSQSClient;

import java.util.Queue;

public class SqsQueueService implements QueueService {
  //
  // Task 4: Optionally implement parts of me.
  //
  // This file is a placeholder for an AWS-backed implementation of QueueService.  It is included
  // primarily so you can quickly assess your choices for method signatures in QueueService in
  // terms of how well they map to the implementation intended for a production environment.
  //

  public SqsQueueService(AmazonSQSClient sqsClient) {
  }

  public Message pull(Queue<Message> queue) {
    return null;
  }

  public boolean push(String message, Queue<Message> queue) {
    return true;
  }

  public boolean delete(Message message, Queue<Message> queue) {
    return false;
  }
}

package me.brunocrt.gcp.functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublisherFunction implements HttpFunction {
    
  private static final Logger logger = Logger.getLogger(PublisherFunction.class.getName());

  // TODO<developer> set this environment variable
  private static final String PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");

  @Override
  public void service(HttpRequest request, HttpResponse response) throws IOException {

    Optional<String> maybeTopicName = request.getFirstQueryParameter("topic");
    Optional<String> maybeMessage = request.getFirstQueryParameter("message");

    BufferedWriter responseWriter = response.getWriter();

    if (maybeTopicName.isEmpty() || maybeMessage.isEmpty()) {
      response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
      responseWriter.write("No 'topic' and/or 'message' parameter(s).");
      return;
    }

    String topicName = maybeTopicName.get();
    
    logger.info("Publishing message to topic: " + topicName);

    // Create the PubsubMessage object
    ByteString byteStr = ByteString.copyFrom(maybeMessage.get(), StandardCharsets.UTF_8);
    PubsubMessage pubsubApiMessage = PubsubMessage.newBuilder().setData(byteStr).build();

    Publisher publisher = 
      Publisher.newBuilder(ProjectTopicName.of(PROJECT_ID, topicName)).build();

    // Attempt to publish the message
    String responseMessage;
    try {
      publisher.publish(pubsubApiMessage).get();
      responseMessage = "Message published.";
    } catch (InterruptedException | ExecutionException e) {
      logger.log(Level.SEVERE, "Error publishing Pub/Sub message: " + e.getMessage(), e);
      responseMessage = "Error publishing Pub/Sub message; see logs for more info.";
    }

    responseWriter.write(responseMessage);
  }
}
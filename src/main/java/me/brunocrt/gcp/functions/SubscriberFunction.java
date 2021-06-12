package me.brunocrt.gcp.functions;

import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.events.cloud.pubsub.v1.Message;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

public class SubscriberFunction implements BackgroundFunction<Message> {

    private static final Logger logger = Logger.getLogger(SubscriberFunction.class.getName());
  
    @Override
    public void accept(Message message, Context context) {

      String name = "world";
      if (message != null && message.getData() != null) {
        name = new String(
            Base64.getDecoder().decode(message.getData().getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8);
      }
      logger.info(String.format("Hello %s!", name));
      return;
    }
  }

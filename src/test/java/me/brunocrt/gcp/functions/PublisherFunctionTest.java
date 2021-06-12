package me.brunocrt.gcp.functions;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.common.testing.TestLogHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class PublisherFunctionTest {
    
  @Mock private HttpRequest request;
  @Mock private HttpResponse response;

  private static final String FUNCTIONS_TOPIC = System.getenv("FUNCTIONS_TOPIC");

  private static final Logger logger = Logger.getLogger(PublisherFunction.class.getName());
  private static final TestLogHandler logHandler = new TestLogHandler();

  private BufferedWriter writerOut;
  private StringWriter responseOut;

  @BeforeClass
  public static void beforeClass() {
    logger.addHandler(logHandler);
  }

  @Before
  public void beforeTest() throws IOException {
    MockitoAnnotations.initMocks(this);

    BufferedReader reader = new BufferedReader(new StringReader("{}"));
    when(request.getReader()).thenReturn(reader);

    responseOut = new StringWriter();
    writerOut = new BufferedWriter(responseOut);
    when(response.getWriter()).thenReturn(writerOut);

    logHandler.clear();
  }

  @Test
  public void functionsPubsubPublish_shouldFailWithoutParameters() throws IOException {
    new PublisherFunction().service(request, response);

    writerOut.flush();
    assertThat(responseOut.toString()).isEqualTo(
        "Missing 'topic' and/or 'message' parameter(s).");
  }

  @Test
  public void functionsPubsubPublish_shouldPublishMessage() throws Exception {
    
    when(request.getFirstQueryParameter("topic")).thenReturn(Optional.of(FUNCTIONS_TOPIC));
    when(request.getFirstQueryParameter("message")).thenReturn(Optional.of("hello"));

    new PublisherFunction().service(request, response);

    writerOut.flush();
    assertThat(logHandler.getStoredLogRecords().get(0).getMessage()).isEqualTo(
        "Publishing message to topic: " + FUNCTIONS_TOPIC);
    assertThat(responseOut.toString()).isEqualTo("Message published.");
  }
}
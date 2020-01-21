/*
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */

package io.dapr.examples.pubsub.http;

import io.dapr.client.DefaultObjectSerializer;
import io.dapr.client.domain.CloudEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * SpringBoot Controller to handle input binding.
 */
@RestController
public class SubscriberController {

  /**
   * Dapr's default serializer/deserializer.
   */
  private static final DefaultObjectSerializer SERIALIZER = new DefaultObjectSerializer();

  @GetMapping("/dapr/subscribe")
  public byte[] daprConfig() throws Exception {
    return SERIALIZER.serialize(new String[] { "message" });
  }

  @PostMapping(path = "/message")
  public Mono<Void> handleMessage(@RequestBody(required = false) byte[] body,
                                   @RequestHeader Map<String, String> headers) {
    return Mono.fromRunnable(() -> {
      try {
        // Dapr's event is compliant to CloudEvent.
        CloudEvent envelope = CloudEvent.deserialize(body);

        String message = envelope.getData() == null ? "" : new String(envelope.getData());
        System.out.println("Subscriber got message: " + message);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

}

package org.example;

import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;

import java.nio.charset.StandardCharsets;

public class AeronSubscriber {
    private static final String CHANNEL = "aeron:udp?endpoint=localhost:40123";
    private static final int STREAM_ID = 1;

    public static void main(String[] args) {
        Aeron.Context context = new Aeron.Context();
        try (Aeron aeron = Aeron.connect(context);
             Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID)) {

            FragmentHandler fragmentHandler = (buffer, offset, length, header) -> {
                byte[] messageBytes = new byte[length];
                buffer.getBytes(offset, messageBytes);
                String message = new String(messageBytes, StandardCharsets.UTF_8);
                System.out.println("Received message: " + message);
            };

            IdleStrategy idleStrategy = new SleepingIdleStrategy(1000);

            while (true) {
                int fragmentsRead = subscription.poll(fragmentHandler, 1);
                idleStrategy.idle(fragmentsRead);
            }
        }
    }
}
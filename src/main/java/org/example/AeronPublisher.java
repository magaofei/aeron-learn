package org.example;

import io.aeron.Aeron;
import io.aeron.Publication;
import org.agrona.BufferUtil;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.charset.StandardCharsets;

public class AeronPublisher {
    private static final String CHANNEL = "aeron:udp?endpoint=localhost:40123";
    private static final int STREAM_ID = 1;

    public static void main(String[] args) {
        Aeron.Context context = new Aeron.Context();
        try (Aeron aeron = Aeron.connect(context);
             Publication publication = aeron.addPublication(CHANNEL, STREAM_ID)) {

            UnsafeBuffer buffer = new UnsafeBuffer(BufferUtil.allocateDirectAligned(256, 64));
            String message = "Hello, Aeron!";
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            buffer.putBytes(0, messageBytes);

            while (true) {
                long result = publication.offer(buffer, 0, messageBytes.length);
                if (result > 0L) {
                    System.out.println("Message sent successfully.");
                } else if (result == Publication.BACK_PRESSURED) {
                    System.out.println("Back pressure detected.");
                } else {
                    System.out.println("Failed to send the message: " + result);
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
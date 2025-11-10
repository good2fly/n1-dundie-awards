package com.ninjaone.dundie_awards.pubsub;

public interface MessageProducer {

    /**
     * Send a message object to the specified destination.
     *
     * @param destination The name of the destination (e.g. a topic or a routing key, depending on the actual broker).
     * @param message The message object to be sent
     */
    void sendMessage(String destination, Object message);
}

package net.java.cargotracker.infrastructure.messaging.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Message;
import javax.jms.MessageListener;

//@MessageDriven(activationConfig = {
//    @ActivationConfigProperty(
//      propertyName = "destinationLookup",
//    propertyValue = "java:global/jms/DeliveredCargoQueue")
//})
public class SimpleLoggingConsumer implements MessageListener {

    private static final Logger logger = Logger.getLogger(
            SimpleLoggingConsumer.class.getName());

    @Override
    public void onMessage(Message message) {
        logger.log(Level.FINE, "Received JMS message: {0}", message);
    }
}
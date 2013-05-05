package net.java.cargotracker.infrastructure.messaging.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import net.java.cargotracker.application.HandlingEventService;
import net.java.cargotracker.domain.model.handling.CannotCreateHandlingEventException;
import net.java.cargotracker.interfaces.handling.HandlingEventRegistrationAttempt;

/**
 * Consumes handling event registration attempt messages and delegates to proper
 * registration.
 */
//@MessageDriven(activationConfig = {
//    @ActivationConfigProperty(
//      propertyName = "destinationLookup",
//    propertyValue = "java:global/jms/HandlingEventRegistrationAttemptQueue")
//})
@MessageDriven(mappedName = "java:global/jms/HandlingEventRegistrationAttemptQueue")
public class HandlingEventRegistrationAttemptConsumer implements MessageListener {

    @Inject
    private HandlingEventService handlingEventService;
    private static final Logger logger = Logger.getLogger(
            HandlingEventRegistrationAttemptConsumer.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            HandlingEventRegistrationAttempt attempt =
                    (HandlingEventRegistrationAttempt) objectMessage.getObject();
            handlingEventService.registerHandlingEvent(
                    attempt.getCompletionTime(),
                    attempt.getTrackingId(),
                    attempt.getVoyageNumber(),
                    attempt.getUnLocode(),
                    attempt.getType());
        } catch (JMSException | CannotCreateHandlingEventException e) {
//        } catch (JMSException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
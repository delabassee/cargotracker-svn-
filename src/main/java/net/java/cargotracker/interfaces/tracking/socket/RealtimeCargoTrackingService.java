package net.java.cargotracker.interfaces.tracking.socket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import net.java.cargotracker.application.CargoInspected;
import net.java.cargotracker.domain.model.cargo.Cargo;

/**
 * WebSocket service for tracking all cargoes in real time.
 *
 * @author Vijay Nair
 */
@Singleton
@ServerEndpoint("/tracking")
public class RealtimeCargoTrackingService {

    private static final Logger logger = Logger.getLogger(
            RealtimeCargoTrackingService.class.getName());

    private final Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void onOpen(final Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(final Session session) {
        sessions.remove(session);
    }

    public void onCargoInspected(@Observes @CargoInspected Cargo cargo) {
        JsonObject model = Json.createObjectBuilder()
                .add("trackingId", cargo.getTrackingId().getIdString())
                .add("origin", cargo.getOrigin().getName())
                .add("destination", cargo.getRouteSpecification().getDestination().getName())
                .add("lastKnownLocation", cargo.getDelivery().getLastKnownLocation().getName())
                .add("transportStatus", cargo.getDelivery().getTransportStatus().toString())
                .build();

        try {
            for (Session session : sessions) {
                session.getBasicRemote().sendText(model.toString());
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Unable to publish WebSocket message", ex);
        }
    }
}

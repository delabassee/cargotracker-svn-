package net.java.cargotracker.interfaces.tracking.socket;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import net.java.cargotracker.application.CargoInspected;
import net.java.cargotracker.domain.model.cargo.Cargo;


/**
 * This class acts as the socket end point for tracking all cargoes
 * @author vijaynair
 */

@Singleton
@ServerEndpoint("/tracking")
public class RealTimeCargoTracker {
    
    private static final Set<Session> sessions = 
                           Collections.synchronizedSet(new HashSet<Session>());
    
    private static final Logger logger = Logger.getLogger(
            RealTimeCargoTracker.class.getName());
    
    @OnOpen 
    public void onOpen(final Session session) { 
        sessions.add(session); 
    }
    
    @OnMessage 
    public void onMessage(final String message, final Session client){
    } 
    
    @OnClose
    public void onClose(final Session session) { 
        sessions.remove(session); 
    }
    
    public void onCargoInspected(@Observes @CargoInspected Cargo cargo){
        JsonObject model = Json.createObjectBuilder()
                .add("trackingId",cargo.getTrackingId().getIdString())
                .add("origin",cargo.getOrigin().getName())
                .add("destination",cargo.getRouteSpecification().getDestination().getName())
                .add("lastKnownLocation",cargo.getDelivery().getLastKnownLocation().getName())
                .add("transportStatus",cargo.getDelivery().getTransportStatus().toString())
                .build();
        StringWriter stWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
            jsonWriter.writeObject(model);
        }
        
        
        try{
            for(Session sess:sessions){
                sess.getBasicRemote().sendText(stWriter.toString());
            }
        }catch(IOException ex){
            logger.log(Level.WARNING, "Unable to publish socket messages");
        }
    }
}

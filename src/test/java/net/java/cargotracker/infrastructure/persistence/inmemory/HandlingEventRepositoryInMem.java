package net.java.cargotracker.infrastructure.persistence.inmemory;

import java.util.*;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.handling.HandlingHistory;

public class HandlingEventRepositoryInMem implements HandlingEventRepository {

  private Map<TrackingId, List<HandlingEvent>> eventMap = new HashMap<TrackingId, List<HandlingEvent>>();

  @Override
  public void store(HandlingEvent event) {
    TrackingId trackingId = event.getCargo().getTrackingId();
    List<HandlingEvent> list = eventMap.get(trackingId);
    if (list == null) {
      list = new ArrayList<HandlingEvent>();
      eventMap.put(trackingId, list);
    }
    list.add(event);
  }

  @Override
  public HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId) {
    List<HandlingEvent> events = eventMap.get(trackingId);
    if (events == null) events = Collections.emptyList();
    
    return new HandlingHistory(events);
  }

}

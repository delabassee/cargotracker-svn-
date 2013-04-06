package net.java.cargotracker.domain.model.handling;

import java.util.*;
import static java.util.Collections.sort;
import org.apache.commons.lang3.Validate;

public class HandlingHistory {

    private List<HandlingEvent> handlingEvents;
    public static final HandlingHistory EMPTY = new HandlingHistory(
            Collections.<HandlingEvent>emptyList());

    public HandlingHistory(Collection<HandlingEvent> handlingEvents) {
        Validate.notNull(handlingEvents, "Handling events are required");

        this.handlingEvents = new ArrayList<HandlingEvent>(handlingEvents);
    }

    /**
     * @return A distinct list (no duplicate registrations) of handling events,
     * ordered by completion time.
     */
    public List<HandlingEvent> getDistinctEventsByCompletionTime() {
        List<HandlingEvent> ordered = new ArrayList<HandlingEvent>(
                new HashSet<HandlingEvent>(handlingEvents));
        sort(ordered, BY_COMPLETION_TIME_COMPARATOR);

        return Collections.unmodifiableList(ordered);
    }

    /**
     * @return Most recently completed event, or null if the delivery history is
     * empty.
     */
    public HandlingEvent getMostRecentlyCompletedEvent() {
        List<HandlingEvent> distinctEvents = getDistinctEventsByCompletionTime();

        if (distinctEvents.isEmpty()) {
            return null;
        } else {
            return distinctEvents.get(distinctEvents.size() - 1);
        }
    }

    private boolean sameValueAs(HandlingHistory other) {
        return other != null && this.handlingEvents.equals(other.handlingEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HandlingHistory other = (HandlingHistory) o;
        return sameValueAs(other);
    }

    @Override
    public int hashCode() {
        return handlingEvents.hashCode();
    }
    private static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR =
            new Comparator<HandlingEvent>() {
                @Override
                public int compare(HandlingEvent he1, HandlingEvent he2) {
                    return he1.getCompletionTime().compareTo(he2.getCompletionTime());
                }
            };
}

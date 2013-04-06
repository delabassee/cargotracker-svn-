package net.java.cargotracker.domain.model.voyage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A voyage schedule.
 */
@Embeddable
public class Schedule implements Serializable {

    public static final Schedule EMPTY = new Schedule();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "voyage_id") // TODO Index as cm_index
    private List<CarrierMovement> carrierMovements = Collections.EMPTY_LIST;

    public Schedule() {
    }

    Schedule(List<CarrierMovement> carrierMovements) {
        Validate.notNull(carrierMovements);
        Validate.noNullElements(carrierMovements);
        Validate.notEmpty(carrierMovements);

        this.carrierMovements = carrierMovements;
    }

    public List<CarrierMovement> getCarrierMovements() {
        return Collections.unmodifiableList(carrierMovements);
    }

    private boolean sameValueAs(Schedule other) {
        return other != null && this.carrierMovements.equals(
                other.carrierMovements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Schedule that = (Schedule) o;

        return sameValueAs(that);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.carrierMovements).toHashCode();
    }
}

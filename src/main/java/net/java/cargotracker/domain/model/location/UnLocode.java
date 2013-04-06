package net.java.cargotracker.domain.model.location;

import java.io.Serializable;
import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.Validate;

/**
 * United nations location code.
 * <p/>
 * http://www.unece.org/cefact/locode/
 * http://www.unece.org/cefact/locode/DocColumnDescription.htm#LOCODE
 */
@Embeddable
public class UnLocode implements Serializable {

    @NotNull
    private String unlocode;
    // Country code is exactly two letters.
    // Location code is usually three letters, but may contain the numbers 2-9 as well
    private static final Pattern VALID_PATTERN =
            Pattern.compile("[a-zA-Z]{2}[a-zA-Z2-9]{3}");

    public UnLocode() {
    }

    /**
     * Constructor.
     *
     * @param countryAndLocation Location string.
     */
    public UnLocode(String countryAndLocation) {
        Validate.notNull(countryAndLocation, "Country and location may not be null");
        Validate.isTrue(VALID_PATTERN.matcher(countryAndLocation).matches(),
                countryAndLocation + " is not a valid UN/LOCODE (does not match pattern)");

        this.unlocode = countryAndLocation.toUpperCase();
    }

    /**
     * @return country code and location code concatenated, always upper case.
     */
    public String getIdString() {
        return unlocode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UnLocode other = (UnLocode) o;

        return sameValueAs(other);
    }

    @Override
    public int hashCode() {
        return unlocode.hashCode();
    }

    boolean sameValueAs(UnLocode other) {
        return other != null && this.unlocode.equals(other.unlocode);
    }

    @Override
    public String toString() {
        return getIdString();
    }
}

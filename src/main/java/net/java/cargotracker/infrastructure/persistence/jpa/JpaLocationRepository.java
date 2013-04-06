package net.java.cargotracker.infrastructure.persistence.jpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.UnLocode;

public class JpaLocationRepository implements LocationRepository, Serializable {

    private static final long serialVersionUID = 1L;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Location find(UnLocode unLocode) {
        try {
            return entityManager.createNamedQuery("Location.findByUnLocode",
                    Location.class).setParameter("unLocode", unLocode)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.err.println("No location found for UN LOCODE: " + unLocode);
            e.printStackTrace(); // TODO Use a logger with a level.
            return null;
        }
    }

    @Override
    public List<Location> findAll() {
        return entityManager.createNamedQuery("Location.findAll", Location.class)
                .getResultList();
    }
}
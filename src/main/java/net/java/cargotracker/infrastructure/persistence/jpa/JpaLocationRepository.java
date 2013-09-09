package net.java.cargotracker.infrastructure.persistence.jpa;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.UnLocode;

@ApplicationScoped
public class JpaLocationRepository implements LocationRepository, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(
            JpaLocationRepository.class.getName());
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Location find(UnLocode unLocode) {
        try {
            return entityManager.createNamedQuery("Location.findByUnLocode",
                    Location.class).setParameter("unLocode", unLocode)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.FINE, "No location found for UN LOCODE: {0}", unLocode);
            return null;
        }
    }

    @Override
    public List<Location> findAll() {
        return entityManager.createNamedQuery("Location.findAll", Location.class)
                .getResultList();
    }
}

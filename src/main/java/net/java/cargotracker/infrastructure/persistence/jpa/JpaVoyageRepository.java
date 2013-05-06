package net.java.cargotracker.infrastructure.persistence.jpa;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;

@ApplicationScoped
public class JpaVoyageRepository implements VoyageRepository,
        Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(
            JpaVoyageRepository.class.getName());
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Voyage find(VoyageNumber voyageNumber) {
        try {
            return entityManager.createNamedQuery("Voyage.findByVoyageNumber",
                    Voyage.class)
                    .setParameter("voyageNumber", voyageNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.FINE, "No voyage found for voyage number: {0}",
                    voyageNumber.getIdString());
            return null;
        }
    }
}
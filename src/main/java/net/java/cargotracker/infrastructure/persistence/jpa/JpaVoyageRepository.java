package net.java.cargotracker.infrastructure.persistence.jpa;

import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;

// TODO Change this to a singleton?
public class JpaVoyageRepository implements VoyageRepository,
        Serializable {

    private static final long serialVersionUID = 1L;
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
            System.err.println("No voyage found for voyage number: "
                    + voyageNumber.getIdString());
            e.printStackTrace(); // TODO Use a logger with a level.
            return null;
        }
    }
}
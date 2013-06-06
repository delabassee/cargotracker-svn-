package net.java.cargotracker.application.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.CannotCreateHandlingEventException;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingEventFactory;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.handling.HandlingHistory;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;

/**
 * Loads sample data for demo.
 */
@Singleton
@Startup
public class SampleDataGenerator {

    // TODO See if the logger can be injected.
    private static final Logger logger = Logger.getLogger(
            SampleDataGenerator.class.getName());
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private HandlingEventFactory handlingEventFactory;
    @Inject
    private HandlingEventRepository handlingEventRepository;

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void loadSampleData() {
        logger.info("Loading sample data.");
        unLoadAll(); //  Fail-safe in case of application restart that does not trigger a JPA schema drop.
        loadSampleLocations();
        loadSampleVoyages();
        loadSampleCargos();
    }

    private void unLoadAll() {
        logger.info("Unloading all existing data.");
        // In order to remove handling events, must remove refrences in cargo.
        // Dropping cargo first won't work since handling events have references
        // to it.
        // TODO See if there is a better way to do this.
        List<Cargo> cargos = entityManager.createQuery("Select c from Cargo c",
                Cargo.class).getResultList();
        for (Cargo cargo : cargos) {
            cargo.getDelivery().setLastEvent(null);
            entityManager.merge(cargo);
        }

        // Delete all entities
        // TODO See why cascade delete is not working.
        entityManager.createQuery("Delete from HandlingEvent").executeUpdate();
        entityManager.createQuery("Delete from Leg").executeUpdate();
        entityManager.createQuery("Delete from Cargo").executeUpdate();
        entityManager.createQuery("Delete from CarrierMovement").executeUpdate();
        entityManager.createQuery("Delete from Voyage").executeUpdate();
        entityManager.createQuery("Delete from Location").executeUpdate();
    }

    private void loadSampleLocations() {
        logger.info("Loading sample locations.");

        entityManager.persist(SampleLocations.HONGKONG);
        entityManager.persist(SampleLocations.MELBOURNE);
        entityManager.persist(SampleLocations.STOCKHOLM);
        entityManager.persist(SampleLocations.HELSINKI);
        entityManager.persist(SampleLocations.CHICAGO);
        entityManager.persist(SampleLocations.TOKYO);
        entityManager.persist(SampleLocations.HAMBURG);
        entityManager.persist(SampleLocations.SHANGHAI);
        entityManager.persist(SampleLocations.ROTTERDAM);
        entityManager.persist(SampleLocations.GOTHENBURG);
        entityManager.persist(SampleLocations.HANGZOU);
        entityManager.persist(SampleLocations.NEWYORK);
        entityManager.persist(SampleLocations.DALLAS);
    }

    private void loadSampleVoyages() {
        logger.info("Loading sample voyages.");

        entityManager.persist(SampleVoyages.HONGKONG_TO_NEW_YORK);
        entityManager.persist(SampleVoyages.NEW_YORK_TO_DALLAS);
        entityManager.persist(SampleVoyages.DALLAS_TO_HELSINKI);
        entityManager.persist(SampleVoyages.HELSINKI_TO_HONGKONG);
        entityManager.persist(SampleVoyages.DALLAS_TO_HELSINKI_ALT);
    }

    private void loadSampleCargos() {
        logger.info("Loading sample cargo data.");
        
        // Cargo ABC123
        TrackingId trackingId1 = new TrackingId("ABC123");

        RouteSpecification routeSpecification1 = new RouteSpecification(
                SampleLocations.HONGKONG, SampleLocations.HELSINKI,
                DateUtil.toDate("2009-03-15"));
        Cargo abc123 = new Cargo(trackingId1, routeSpecification1);

        Itinerary itinerary1 = new Itinerary(Arrays.asList(
                new Leg(SampleVoyages.HONGKONG_TO_NEW_YORK,
                SampleLocations.HONGKONG, SampleLocations.NEWYORK,
                DateUtil.toDate("2009-03-02"),
                DateUtil.toDate("2009-03-05")),
                new Leg(SampleVoyages.NEW_YORK_TO_DALLAS,
                SampleLocations.NEWYORK,
                SampleLocations.DALLAS,
                DateUtil.toDate("2009-03-06"),
                DateUtil.toDate("2009-03-08")),
                new Leg(SampleVoyages.DALLAS_TO_HELSINKI,
                SampleLocations.DALLAS,
                SampleLocations.HELSINKI,
                DateUtil.toDate("2009-03-09"),
                DateUtil.toDate("2009-03-12"))));
        abc123.assignToRoute(itinerary1);

        entityManager.persist(abc123);

        try {
            HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
                    new Date(), DateUtil.toDate("2009-03-01"), trackingId1, null,
                    SampleLocations.HONGKONG.getUnLocode(),
                    HandlingEvent.Type.RECEIVE);
            entityManager.persist(event1);

            HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
                    new Date(), DateUtil.toDate("2009-03-02"), trackingId1,
                    SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                    SampleLocations.HONGKONG.getUnLocode(),
                    HandlingEvent.Type.LOAD);
            entityManager.persist(event2);

            HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
                    new Date(), DateUtil.toDate("2009-03-05"), trackingId1,
                    SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                    SampleLocations.NEWYORK.getUnLocode(),
                    HandlingEvent.Type.UNLOAD);
            entityManager.persist(event3);
        } catch (CannotCreateHandlingEventException e) {
            throw new RuntimeException(e);
        }

        HandlingHistory handlingHistory1 =
                handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId1);
        abc123.deriveDeliveryProgress(handlingHistory1);

        entityManager.persist(abc123);

        // Cargo JKL567
        TrackingId trackingId2 = new TrackingId("JKL567");

        RouteSpecification routeSpecification2 = new RouteSpecification(
                SampleLocations.HANGZOU, SampleLocations.STOCKHOLM,
                DateUtil.toDate("2009-03-18"));
        Cargo jkl567 = new Cargo(trackingId2, routeSpecification2);

        Itinerary itinerary2 = new Itinerary(Arrays.asList(
                new Leg(SampleVoyages.HONGKONG_TO_NEW_YORK,
                SampleLocations.HANGZOU, SampleLocations.NEWYORK,
                DateUtil.toDate("2009-03-03"),
                DateUtil.toDate("2009-03-05")),
                new Leg(SampleVoyages.NEW_YORK_TO_DALLAS,
                SampleLocations.NEWYORK, SampleLocations.DALLAS,
                DateUtil.toDate("2009-03-06"),
                DateUtil.toDate("2009-03-08")),
                new Leg(SampleVoyages.DALLAS_TO_HELSINKI, SampleLocations.DALLAS,
                SampleLocations.STOCKHOLM, DateUtil.toDate("2009-03-09"),
                DateUtil.toDate("2009-03-11"))));
        jkl567.assignToRoute(itinerary2);

        entityManager.persist(jkl567);

        try {
            HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
                    new Date(), DateUtil.toDate("2009-03-01"), trackingId2, null,
                    SampleLocations.HANGZOU.getUnLocode(),
                    HandlingEvent.Type.RECEIVE);
            entityManager.persist(event1);

            HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
                    new Date(), DateUtil.toDate("2009-03-03"), trackingId2,
                    SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                    SampleLocations.HANGZOU.getUnLocode(),
                    HandlingEvent.Type.LOAD);
            entityManager.persist(event2);

            HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
                    new Date(), DateUtil.toDate("2009-03-05"), trackingId2,
                    SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                    SampleLocations.NEWYORK.getUnLocode(),
                    HandlingEvent.Type.UNLOAD);
            entityManager.persist(event3);

            HandlingEvent event4 = handlingEventFactory.createHandlingEvent(
                    new Date(), DateUtil.toDate("2009-03-06"), trackingId2,
                    SampleVoyages.HONGKONG_TO_NEW_YORK.getVoyageNumber(),
                    SampleLocations.NEWYORK.getUnLocode(),
                    HandlingEvent.Type.LOAD);
            entityManager.persist(event4);
        } catch (CannotCreateHandlingEventException e) {
            throw new RuntimeException(e);
        }

        HandlingHistory handlingHistory2 =
                handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId2);
        jkl567.deriveDeliveryProgress(handlingHistory2);

        entityManager.persist(jkl567);
    }
}
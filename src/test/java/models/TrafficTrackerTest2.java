package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrafficTrackerTest2 {
    private final static String VAULT_NAME = "/test2";

    TrafficTracker trafficTracker;

    @BeforeEach
    private void setup() {
        Locale.setDefault(Locale.ENGLISH);
        trafficTracker.importDetectionsFromVault(VAULT_NAME + "/detections");
    }

    @Test
    public void importCarsCheck() {
        TrafficTracker tracker = new TrafficTracker();
        tracker.importCarsFromVault(VAULT_NAME + "/cars.txt");

        // Check that proper OrderedLists have been created with a specified ordening
        assertNotNull(tracker.getCars().getOrdening());

        // Check that the imports sustained the representation invariants
        CarsListTest.checkRepresentationInvariant(tracker.getCars());

        // Check that all content was properly loaded
        assertEquals(10, tracker.getCars().size());
    }

    @Test
    public void importVaultCheck() {
        // Check that proper OrderedLists have been created with a specified ordening
        assertNotNull(trafficTracker.calculateTotalFines());
        assertNotNull(trafficTracker.topViolationsByCar(5));
        assertNotNull(trafficTracker.topViolationsByCity(7));


        // Check that the imports sustained the representation invariants
        CarsListTest.checkRepresentationInvariant(trafficTracker.getCars());
        CarsListTest.checkRepresentationInvariant(trafficTracker.getViolations());


        assertEquals(2, trafficTracker.getViolations().size(),
                "Did not find the right number of Violation instances for different cars in different cities");
        assertEquals(7, trafficTracker.getViolations().stream().mapToInt(Violation::getOffencesCount).sum(),
                "Total number of offences across all Violation instances did not match.");
        assertEquals(7, trafficTracker.getViolations().stream().mapToInt(Violation::getOffencesCount).sum(),
                "Total number of offences across all Violation instances did not match.");
    }
}
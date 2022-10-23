package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class TrafficTrackerTest2 {
    private final static String VAULT_NAME = "/test2";
    TrafficTracker trafficTracker;
    @BeforeEach
    private void setup() {
        Locale.setDefault(Locale.ENGLISH);
        trafficTracker = new TrafficTracker();
        trafficTracker.importCarsFromVault(VAULT_NAME + "/cars.txt");
        trafficTracker.importDetectionsFromVault(VAULT_NAME + "/detections");
    }

    @Test
    public void calculateTotalFines() {
        //Check if calculateTotalFines is not null
        assertNotNull(trafficTracker.calculateTotalFines());
    }

    @Test
    public void TopPerCity() {
        //Check top 2 city Violations
        assertEquals(1, trafficTracker.topViolationsByCar(1).size(),
                "Top 2 City Violations");
    }

    @Test
    public void TopPerCar() {
        //Check top 2 Car Violations
        assertEquals(1, trafficTracker.topViolationsByCar(1).size(),
                "Top 2 car Violations");
    }
}

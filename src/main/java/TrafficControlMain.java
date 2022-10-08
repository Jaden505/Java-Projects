import models.Car;
import models.TrafficTracker;
import models.Violation;

import java.util.List;
import java.util.Locale;

public class TrafficControlMain {
    private final static String VAULT_NAME = "/2022-09";
    //private final static String VAULT_NAME = "/test1";

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        System.out.println("Welcome to the HvA Traffic Control processor\n");

        TrafficTracker trafficTracker = new TrafficTracker();

        // import all known cars from the data vault
        trafficTracker.importCarsFromVault(VAULT_NAME + "/cars.txt");
        System.out.println("Imported cars:\n" +
                trafficTracker.getCars().subList(0,Integer.min(10,trafficTracker.getCars().size())) + "...\n");

        // import and process all detections at the city entry points of environmental zones from the data vault
        trafficTracker.importDetectionsFromVault(VAULT_NAME + "/detections");
        System.out.println("Aggregated offending detections:\n" +
                trafficTracker.getViolations().subList(0,Integer.min(10,trafficTracker.getViolations().size())) + "... \n");

        // calculate potential revenues from multiple fine schemes for violations
        System.out.printf("Total fines à €25 per offence for trucks and €35 per offence for coaches would amount to: €%.0f\n",
                trafficTracker.calculateTotalFines());

        // report top-5 violations from different aggregation criteria
        System.out.printf("Top 5 cars with largest total number of offences are:\n%s\n", trafficTracker.topViolationsByCar(5));
        System.out.printf("Top 5 cities with largest total number of offences are:\n%s\n", trafficTracker.topViolationsByCity(5));

    }


}

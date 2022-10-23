package models;

import models.Comparators.CarCityOrder;
import models.Comparators.LicenseplateOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.logging.XMLFormatter;
import java.util.stream.Collectors;

public class TrafficTracker {
    private final String TRAFFIC_FILE_EXTENSION = ".txt";
    private final String TRAFFIC_FILE_PATTERN = ".+\\" + TRAFFIC_FILE_EXTENSION;

    private OrderedList<Car> cars;                  // the reference list of all known Cars registered by the RDW
    private OrderedList<Violation> violations;      // the accumulation of all offences by car and by city

    public TrafficTracker() {
        this.cars = new OrderedArrayList<>(new LicenseplateOrder());
        this.violations = new OrderedArrayList<>(new CarCityOrder());
    }

    /**
     * imports all registered cars from a resource file that has been provided by the RDW
     * @param resourceName
     */
    public void importCarsFromVault(String resourceName) {
        this.cars.clear();

        // load all cars from the text file
        int numberOfLines = importItemsFromFile(this.cars,
                createFileFromURL(TrafficTracker.class.getResource(resourceName)),
                Car::fromLine);

        // sort the cars for efficient later retrieval
        this.cars.sort();

        System.out.printf("Imported %d cars from %d lines in %s.\n", this.cars.size(), numberOfLines, resourceName);
    }

    /**
     * imports and merges all raw detection data of all entry gates of all cities from the hierarchical file structure of the vault
     * accumulates any offences against purple rules into this.violations
     * @param resourceName
     */
    public void importDetectionsFromVault(String resourceName) {
        this.violations.clear();

        int totalNumberOfOffences =
            this.mergeDetectionsFromVaultRecursively(
                    createFileFromURL(TrafficTracker.class.getResource(resourceName)));

        System.out.printf("Found %d offences among detections imported from files in %s.\n",
                totalNumberOfOffences, resourceName);
    }

    /**
     * traverses the detections vault recursively and processes every data file that it finds
     * @param file
     */
    private int mergeDetectionsFromVaultRecursively(File file) {
        int totalNumberOfOffences = 0;

        if (file.isDirectory()) {
            // the file is a folder (a.k.a. directory)
            //  retrieve a list of all files and sub folders in this directory
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);

            for (File f : filesInDirectory) {
                if (f.isDirectory()) {totalNumberOfOffences += mergeDetectionsFromVaultRecursively(f);}

                else {
                    totalNumberOfOffences += this.mergeDetectionsFromFile(f);
                }
            }



        } else if (file.getName().matches(TRAFFIC_FILE_PATTERN)) {
            // the file is a regular file that matches the target pattern for raw detection files
            // process the content of this file and merge the offences found into this.violations
            totalNumberOfOffences += this.mergeDetectionsFromFile(file);
        }

        return totalNumberOfOffences;
    }

    /**
     * imports another batch detection data from the filePath text file
     * and merges the offences into the earlier imported and accumulated violations
     * @param file
     * @return total number of offences
     */
    private int mergeDetectionsFromFile(File file) {
        this.violations.sort();

        List<Detection> newDetections = new ArrayList<>();

        // Total number of offences
        Function<String, Detection> convert = s -> Detection.fromLine(s, this.cars);
        TrafficTracker.importItemsFromFile(newDetections, file, convert);

        // Check for violations
        for (Detection detection : newDetections) {
            Violation violation = detection.validatePurple();

            if (violation != null) {
                this.violations.add(violation);
            }
        }

        return importItemsFromFile(newDetections, file,convert);
    }

    private double calculateFine(Violation v) {
        if (v.getCar().getCarType() == Car.CarType.Truck) {
            return 25;
        }
        else if (v.getCar().getCarType() == Car.CarType.Coach) {
            return 35;
        }

        return 0;
    }

    public double calculateTotalFines() {
        Function<Violation, Double> calculateFine = this::calculateFine;

        return this.violations.aggregate(calculateFine);
    }

    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by car across all cities.
     * @param topNumber     the requested top number of violations in the result list
     * @return              a list of topNum items that provides the top aggregated violations


    /**
     * Prepares a list of topNumber of violations that show the highest offencesCount
     * when this.violations are aggregated by city across all cars.
     * @param topNumber     the requested top number of violations in the result list
     * @return              a list of topNum items that provides the top aggregated violations
     */
    public List<Violation> topViolationsByCar(int topNumber) {
        OrderedArrayList<Violation> topViolationsByCarArrayList = new OrderedArrayList<>(TrafficTracker::orderCarList);

        for (Violation violation: this.violations) {
            topViolationsByCarArrayList.merge(violation, Violation::combineOffencesCounts);
        }

        Comparator<Violation> violationOffencesComparator = Comparator.comparing(Violation::getOffencesCount);
        Comparator<Violation> violationReversedComparator = violationOffencesComparator.reversed();
        topViolationsByCarArrayList.sort(violationReversedComparator);

        return topViolationsByCarArrayList.subList(0, topNumber);
    }


    private static int orderCarList(Violation v1, Violation v2) {
        int result = v1.getCar().compareTo(v2.getCar());
        if (result == 0)
            v1.combineOffencesCounts(v2);
        return result;
    }

    private static int orderCityList(Violation v1, Violation v2) {
        int result = v1.getCity().compareTo(v2.getCity());
        if (result == 0)
            v1.combineOffencesCounts(v2);
        return result;
    }

    public List<Violation> topViolationsByCity(int topNumber) {
        OrderedArrayList<Violation> topViolationsByCityArrayList = new OrderedArrayList<>(TrafficTracker::orderCityList);

        for (Violation violation: this.violations) {
            topViolationsByCityArrayList.merge(violation, Violation::combineOffencesCounts);
        }

        Comparator<Violation> violationOffencesComparator = Comparator.comparing(Violation::getOffencesCount).reversed();

        topViolationsByCityArrayList.sort(violationOffencesComparator);

        return topViolationsByCityArrayList.subList(0, topNumber);
    }



    /**
     * imports a collection of items from a text file which provides one line for each item
     * @param items         the list to which imported items shall be added
     * @param file          the source text file
     * @param converter     a function that can convert a text line into a new item instance
     * @param <E>           the (generic) type of each item
     */
    public static <E> int importItemsFromFile(List<E> items, File file, Function<String,E> converter) {
        int numberOfLines = 0;

        Scanner scanner = createFileScanner(file);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            numberOfLines++;

            E instance = converter.apply(line);

            items.add(instance);
        }

        System.out.printf("Imported %d lines from %s.\n", numberOfLines, file.getPath());

        return numberOfLines;
    }

    /**
     * helper method to create a scanner on a file and handle the exception
     * @param file
     * @return
     */
    private static Scanner createFileScanner(File file) {
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + file.getPath());
        }
    }
    private static File createFileFromURL(URL url) {
        try {
            return new File(url.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax error found on URL: " + url.getPath());
        }
    }

    public OrderedList<Car> getCars() {
        return this.cars;
    }

    public OrderedList<Violation> getViolations() {
        return this.violations;
    }
}

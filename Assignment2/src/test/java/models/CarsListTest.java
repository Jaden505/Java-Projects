package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class CarsListTest {

    Car scoda, audi, bmw, mercedes, icova, volvo1, volvo2, daf1, daf2, daf3, kamaz;

    OrderedArrayList<Car> cars;
    List<Car> initialCars;

    @BeforeEach
    private void setup() {
        Locale.setDefault(Locale.ENGLISH);
        scoda = new Car("1-AAA-02", 6, Car.CarType.Car, Car.FuelType.Gasoline, LocalDate.of(2014,1,31));
        audi = new Car("AA-11-BB", 4, Car.CarType.Car, Car.FuelType.Diesel, LocalDate.of(1998,1,31));
        mercedes = new Car("VV-11-BB", 4, Car.CarType.Van, Car.FuelType.Diesel, LocalDate.of(1998,1,31));
        bmw = new Car("A-123-BB", 4, Car.CarType.Car, Car.FuelType.Gasoline, LocalDate.of(2019,1,31));
        icova = new Car("1-TTT-99", 5, Car.CarType.Truck, Car.FuelType.Lpg, LocalDate.of(2011,1,31));
        volvo1 = new Car("1-TTT-01", 5, Car.CarType.Truck, Car.FuelType.Diesel, LocalDate.of(2009,1,31));
        volvo2 = new Car("1-TTT-02", 6, Car.CarType.Truck, Car.FuelType.Diesel, LocalDate.of(2011,1,31));
        daf1 = new Car("1-CCC-01", 5, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2009,1,31));
        daf2 = new Car("1-CCC-02", 6, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2011,1,31));
        daf3 = new Car("1-CCC-03", 5, Car.CarType.Coach, Car.FuelType.Lpg, LocalDate.of(2006,1,31));
        kamaz = new Car("1-AAAA-0000");

        // Using your comparator for cars here as you have specified it in the TrafficTracker
        TrafficTracker trafficTracker = new TrafficTracker();
        cars = new OrderedArrayList<>(trafficTracker.getCars().getOrdening());
        initialCars = List.of(scoda,audi,mercedes,bmw,icova,volvo1,daf1,kamaz);
        cars.addAll(initialCars);
    }

    @Test void carOrdeningByLicensePlateNumber() {
        // Using the ordening as you have specified in the constructor of TrafficTracker; fix that first
        Comparator<? super Car> ordening = cars.getOrdening();
        assertEquals(0, ordening.compare(scoda, scoda),
                "TrafficTracker should orden its cars by license plate number");
        Car scoda2 = new Car(scoda.getLicensePlate());
        assertEquals(0, ordening.compare(scoda, scoda2),
                "TrafficTracker should orden its cars by license plate number only");
        assertThat("cars should be ordened by increasing license plate number",
                ordening.compare(volvo1, volvo2), lessThan(0));
        assertThat("cars should be ordened by increasing license plate number",
                ordening.compare(volvo2, volvo1), greaterThan(0));

    }

    @Test
    public void sortOrdersByLicensePlateNumber() {
        assertEquals(initialCars.size(), cars.size(),
                "addAll in the setup did not properly add all cars, maybe because add is not working properly");

        // first three items of cars appear to be sorted, so any implementation that finds nSorted == 3 is technically
        // correct; but we do not encourage you to waste computation time to figure that out.
        // the best solution is that nSorted is still 0 after only adding items.
        assertThat("addAll should not change the order of items, but grow the unsorted section instead",
                cars.nSorted, lessThanOrEqualTo(3));

        cars.sort();
        assertEquals(initialCars.size(), cars.size());
        assertEquals(initialCars.size(), cars.nSorted,
                "Sorting should update nSorted, such that searching goes faster hereafter...");
        this.checkSortedInitialCars();
    }
    private void checkSortedInitialCars() {
        assertSame(scoda, cars.get(0));
        assertSame(kamaz, cars.get(1));
        assertSame(daf1, cars.get(2));
        assertSame(volvo1, cars.get(3));
    }

    @Test
    public void indexOfByIterativeBinarySearchFindsAllItems() {
        cars.sort();

        // check whether iterative binary search can find all
        testSearchForAll(cars, cars::indexOfByIterativeBinarySearch);

        cars.add(volvo2);
        assertEquals(initialCars.size(), cars.nSorted);
        assertSame(volvo2, cars.get(cars.size()-1),
                "a new car shall be added into the unsorted section at the end");
        // check whether iterative binary search can find all
        testSearchForAll(cars, cars::indexOfByIterativeBinarySearch);

        assertEquals(-1, cars.indexOfByIterativeBinarySearch(daf2),
                "should not be able to find a missing item");

        cars.add(daf2);
        assertEquals(initialCars.size(), cars.nSorted);
        // check whether iterative binary search can find all
        testSearchForAll(cars, cars::indexOfByIterativeBinarySearch);

        cars.clear();
        cars.sort();
        assertEquals(-1, cars.indexOfByIterativeBinarySearch(scoda),
                "should not be able to find an item in an empty list");
        cars.addAll(initialCars);
        // check whether iterative binary search can find all
        testSearchForAll(cars, cars::indexOfByIterativeBinarySearch);
    }

    @Test
    public void indexOfByRecursiveBinarySearchFindsAllItems() {
        cars.sort();

        // check whether recursive binary search can find all
        testSearchForAll(cars, cars::indexOfByRecursiveBinarySearch);

        cars.add(volvo2);
        assertEquals(initialCars.size(), cars.nSorted);
        assertSame(volvo2, cars.get(cars.size()-1),
                "a new car shall be added into the unsorted section at the end");
        // check whether recursive binary search can find all
        testSearchForAll(cars, cars::indexOfByRecursiveBinarySearch);

        assertEquals(-1, cars.indexOfByRecursiveBinarySearch(daf2),
                "should not be able to find a missing item");

        cars.add(daf2);
        assertEquals(initialCars.size(), cars.nSorted);
        // check whether recursive binary search can find all
        testSearchForAll(cars, cars::indexOfByRecursiveBinarySearch);

        cars.clear();
        cars.sort();
        assertEquals(-1, cars.indexOfByRecursiveBinarySearch(scoda),
                "should not be able to find an item in an empty list");
        cars.addAll(initialCars);
        assertEquals(2, cars.indexOfByRecursiveBinarySearch(initialCars.get(2)),
                "should not be able to find the item in a completely unsorted list");
    }

    @Test
    public void excludeDuplicatesByMerge() {
        BinaryOperator<Car> keepLast = (c1,c2) -> c2;
        cars.sort();
        cars.merge(volvo2, keepLast);
        assertEquals(initialCars.size()+1, cars.size(),
                "merge should add a new car to the unsorted section at the end");
        assertEquals(initialCars.size(), cars.nSorted,
                "merging should not change the ordening of items in the list");
        assertSame(volvo2, cars.get(cars.size()-1),
                "merge should add a new car to the unsorted section at the end");

        for (Car car : initialCars) {
            cars.merge(car, keepLast);
        }
        assertEquals(initialCars.size()+1, cars.size(),
                "merge should add a new car to the unsorted section at the end");
        assertEquals(initialCars.size(), cars.nSorted,
                "merging should not change the ordening of items in the list");
        this.checkSortedInitialCars();
    }

    @Test
    public void insertSustainsRepresentationInvariant() {
        cars.sort();
        cars.add(cars.size()-1, volvo2);
        assertEquals(cars.size()-2, cars.indexOf(volvo2),
                "add(index, item) shall insert the item at the specified index");
        assertNotEquals(cars.size(), cars.nSorted,
                "add(index, item) shall update nSorted to repair the representation invariant");
        cars.add(cars.size()-4, daf2);
        assertEquals(cars.size()-5, cars.indexOf(daf2),
                "add(index, item) shall insert the item at the specified index");
        checkRepresentationInvariant(this.cars);
        cars.add(cars.size()-2, daf3);
        assertEquals(cars.size()-3, cars.indexOf(daf3),
                "add(index, item) shall insert the item at the specified index");
        checkRepresentationInvariant(this.cars);

        // check whether the mixed search still works
        testSearchForAll(cars, cars::indexOf);
    }

    @Test
    public void removeSustainsRepresentationInvariant() {
        cars.sort();
        cars.add(daf2);
        assertEquals(cars.size()-1, cars.indexOf(daf2),
                "add(item) shall add the item at the end in the unsorted section");
        cars.add(daf3);
        assertEquals(cars.size()-1, cars.indexOf(daf3),
                "add(item) shall add the item at the end in the unsorted section");
        cars.remove(5);
        assertEquals(cars.size()-1, cars.indexOf(daf3),
                "remove(index) shall shall not change the relative order of remaining items");
        checkRepresentationInvariant(this.cars);
        cars.remove(initialCars.get(0));
        assertEquals(cars.size()-1, cars.indexOf(daf3),
                "remove(item) shall shall not change the relative order of remaining items");
        checkRepresentationInvariant(this.cars);
        cars.remove(daf2);
        assertEquals(cars.size()-1, cars.indexOf(daf3),
                "remove(item) shall shall not change the relative order of remaining items");
        checkRepresentationInvariant(this.cars);

        // check whether the mixed search still works
        testSearchForAll(cars, cars::indexOf);
    }

    /**
     * checks whether a searcher can find all items in its list
     * this should work both for OrderedLists and regular Lists
     * this should work with and without duplicates in the list
     * @param list          the list with the items to be found
     * @param searcher      the searcher, shall operate on the list argument and be compatible with indexOf
     * @param <E>
     */
    public static <E> void testSearchForAll(List<E> list, Function<E, Integer> searcher) {
        for (int i = 0; i < list.size(); i++) {
            // find the item via the searcher
            E item = list.get(i);
            int index = searcher.apply(item);

            // for reporting only
            int nSorted = 0;

            // handle finding of duplicates
            if (list instanceof OrderedArrayList<E>) {
                nSorted = ((OrderedArrayList<E>) list).nSorted;
                if (index != i && index >= 0 && ((OrderedArrayList<E>) list).getOrdening().compare(item, list.get(index)) == 0) {
                    // accept the situation when there are duplicates in the OrderedList
                    index = i;
                }
            } else if (index != i && index >= 0 && item.equals(list.get(index))) {
                // accept the situation when there are duplicates in the regular List
                index = i;
            }

            assertEquals(index, i,
                    String.format("Existing item[%d]='%s' was not found at its position, but returned index=%d. (nSorted=%d)",
                            i, item, index, nSorted));
        }
    }

    public static <E> void checkRepresentationInvariant(OrderedList<E> list) {
        OrderedArrayList<E> orderedArrayList = (OrderedArrayList<E>)list;
        assertThat(orderedArrayList.nSorted, greaterThanOrEqualTo(0));
        assertThat(orderedArrayList.nSorted, lessThanOrEqualTo(list.size()));
        for (int i = 0; i < orderedArrayList.nSorted-1; i++) {
            assertThat(
                    String.format("Item-%d shall preceed item-%d in sorted part of list before nSorted=%d", i, i+1, orderedArrayList.nSorted),
                    orderedArrayList.getOrdening().compare(orderedArrayList.get(i), orderedArrayList.get(i+1)), lessThanOrEqualTo(0));
        }
    }
}

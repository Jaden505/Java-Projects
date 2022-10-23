package models.Comparators;

import models.Car;

import java.util.Comparator;

public class LicenseplateOrder implements Comparator<Car> {
    @Override
    public int compare(Car c1, Car c2) {
        return c1.getLicensePlate().compareTo(c2.getLicensePlate());
    }
}

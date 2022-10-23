package models.Comparators;

import models.Violation;

import java.util.Comparator;

public class CarCityOrder implements Comparator<Violation> {
    @Override
    public int compare(Violation v1, Violation v2) {
        return Comparator.comparing(Violation::getCar)
                .thenComparing(Violation::getCity)
                .compare(v1, v2);
    }
}

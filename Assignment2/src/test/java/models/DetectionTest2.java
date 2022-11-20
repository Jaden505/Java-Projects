package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class DetectionTest2 {

    Car scoda, audi, bmw, mercedes, icova, volvo1, volvo2, daf1, daf2, kamaz;
    List<Car> cars;

    @BeforeEach
    private void setup() {
        Locale.setDefault(Locale.ENGLISH);
        scoda = new Car("1-AAA-02", 1, Car.CarType.Car, Car.FuelType.Gasoline, LocalDate.of(2014,1,31));
        audi = new Car("AA-11-BB", 1, Car.CarType.Car, Car.FuelType.Diesel, LocalDate.of(1998,1,31));
        mercedes = new Car("VV-11-BB", 4, Car.CarType.Van, Car.FuelType.Diesel, LocalDate.of(1998,1,31));
        bmw = new Car("A-123-BB", 4, Car.CarType.Car, Car.FuelType.Gasoline, LocalDate.of(2019,1,31));
        icova = new Car("1-TTT-99", 1, Car.CarType.Truck, Car.FuelType.Lpg, LocalDate.of(2011,1,31));
        volvo1 = new Car("1-TTT-01", 1, Car.CarType.Truck, Car.FuelType.Diesel, LocalDate.of(2009,1,31));
        volvo2 = new Car("1-TTT-02", 4, Car.CarType.Truck, Car.FuelType.Diesel, LocalDate.of(2011,1,31));
        daf1 = new Car("1-CCC-01", 4, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2009,1,31));
        daf2 = new Car("1-CCC-02", 1, Car.CarType.Coach, Car.FuelType.Diesel, LocalDate.of(2011,1,31));
        kamaz = new Car("1-AAAA-0000");
        cars = new ArrayList<>(List.of(scoda, audi, mercedes, bmw, icova, volvo1, volvo2, daf1, daf2, kamaz));
    }

    @Test
    public void validatePurple() {
        Detection detection1 = Detection.fromLine("1-AAA-02,Amsterdam,2022-10-01T12:11:10", cars);
        //check is validatePurple is null
        assertEquals(detection1.validatePurple(),null,"No offences where made");

    }


}
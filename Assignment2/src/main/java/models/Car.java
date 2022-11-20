package models;

import java.time.LocalDate;

public class Car implements Comparable<Car> {

    public enum CarType {
        Unknown,
        Car,
        Van,
        Truck,
        Coach
    }
    public enum FuelType {
        Unknown,
        Gasoline,
        Lpg,
        Diesel,
        Electric
    }

    private final String licensePlate;      // defines the car uniquely
    private int emissionCategory;           // a number between 0 and 9, higher is cleaner, depends on type, fuel and age, typically.
    private CarType carType;
    private FuelType fuelType;
    private LocalDate dateOfAdmission;      // date of registration of the car at RDW

    public Car(String licensePlate) {
        // base constructor for unregistered and foreign cars
        this.licensePlate = licensePlate;
        this.emissionCategory = 0;
        this.carType = CarType.Unknown;
        this.fuelType = FuelType.Unknown;
        this.dateOfAdmission = LocalDate.EPOCH;
    }
    public Car(String licensePlate, int emissionCategory, CarType carType, FuelType fuelType, LocalDate dateOfAdmission) {
        this(licensePlate);
        this.emissionCategory = emissionCategory;
        this.carType = carType;
        this.fuelType = fuelType;
        this.dateOfAdmission = dateOfAdmission;
    }

    /**
     * parses car information from a textLine
     * with format: licensePlate, emissionCategory, carType, fuelType, dateOfAdmission
     * should ignore leading and trailing whitespaces in each field
     * @param textLine
     * @return  a new Car instance with the provided information
     *          or null if the textLine is corrupt, incomplete or empty
     */
    public static Car fromLine(String textLine) {
        Car newCar = null;

        // extract the comma-separated fields from the textLine
        String[] fields = textLine.split(",");
        if (fields.length >= 5) {
            try {
                // parse the fields and instantiate a new car
                newCar = new Car(
                        fields[0].trim(),
                        Integer.parseInt(fields[1].trim()),
                        CarType.valueOf(fields[2].trim()),
                        FuelType.valueOf(fields[3].trim()),
                        LocalDate.parse(fields[4].trim())
                );
            } catch (Exception e) {
                // any of the parse and valueOf methods could throw an exception on a format mismatch
                System.out.printf("Could not parse Car specification in text line '%s'\n", textLine);
                System.out.println(e.getMessage());
            }
        }

        return newCar;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getEmissionCategory() {
        return emissionCategory;
    }

    public void setEmissionCategory(int emissionCategory) {
        this.emissionCategory = emissionCategory;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public void setDateOfAdmission(LocalDate dateOfAdmission) {
        this.dateOfAdmission = dateOfAdmission;
    }

    public LocalDate getDateOfAdmission() {
        return dateOfAdmission;
    }

    @Override
    public int compareTo(Car other) {
        // cars are uniquely defined by their license plate
        return this.licensePlate.compareTo(other.licensePlate);
    }

    @Override
    public String toString() {

        return String.format("%s/%d/%s/%s",
                this.licensePlate, this.emissionCategory, this.carType, this.fuelType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car car)) return false;
        // cars are uniquely defined by their license plate
        return licensePlate.equals(car.licensePlate);
    }

    @Override
    public int hashCode() {
        return licensePlate.hashCode();
    }
}

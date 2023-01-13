package maze_escape;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Country {

    private String name;
    private int population;
    private Map<Country,Integer> borders;

    public Country(String name) {
        this.name = name;
        this.borders = new HashMap<>();
    }

    public Country(String name, int population) {
        this(name);
        this.population = population;
    }

    public boolean addBorder(Country neighbour, int length) {
        if (this.borders.putIfAbsent(neighbour, length) == null) {
            neighbour.addBorder(this, length);
            return true;
        }
        return false;
    }

    public double distanceTo(Country neighbour) {
        Integer borderLength = this.borders.get(neighbour);
        return (borderLength == null) ? Double.MAX_VALUE : borderLength;
    }


    public Set<Country> getNeighbours() {
        return this.borders.keySet();
    }
    public int getPopulation() {
        return population;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Country country = (Country) o;
        return name.equals(country.name);
    }
}

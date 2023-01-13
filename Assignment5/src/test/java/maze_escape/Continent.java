package maze_escape;

import java.util.Set;

public class Continent extends AbstractGraph<Country> {

    @Override
    public Set<Country> getNeighbours(Country fromVertex) {
        return fromVertex.getNeighbours();
    }
}

package maze_escape;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AbstractGraphTest {

    Country nl, be, de, lux, fr, uk, ro, hu;
    Continent europe = new Continent();

    @BeforeAll
    static void beforeALl() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @BeforeEach
    void setUp() {
        nl = new Country("NL", 18);
        be = new Country("BE", 12);
        nl.addBorder(be,100);
        de = new Country("DE", 83);
        de.addBorder(nl,200);
        de.addBorder(be,30);
        lux = new Country("LUX",1);
        lux.addBorder(be,60);
        lux.addBorder(de,50);
        fr = new Country("FR", 67);
        fr.addBorder(lux,30);
        fr.addBorder(be,110);
        fr.addBorder(de,50);
        uk = new Country("UK", 67);
        uk.addBorder(be,70);
        uk.addBorder(fr,150);
        uk.addBorder(nl,250);

        ro = new Country("RO", 19);
        hu = new Country("HU", 10);
        ro.addBorder(hu,250);
    }

    @Test
    void getAllVerticesShouldFindAllConnected() {
        Set<Country> westEurope = europe.getAllVertices(nl);
        Set<Country> eastEurope = europe.getAllVertices(ro);
        Set<Country> africa = europe.getAllVertices(new Country("MO"));

        assertEquals(6,westEurope.size());
        assertEquals(2,eastEurope.size());
        assertEquals(1,africa.size());
    }

    @Test
    void adjacencyListsShouldFormatPerOrder() {
        assertEquals("Graph adjacency list:\n" +
                "NL: [DE,BE,UK]\n" +
                "DE: [BE,FR,NL,LUX]\n" +
                "BE: [DE,UK,FR,NL,LUX]\n" +
                "UK: [BE,FR,NL]\n" +
                "FR: [DE,BE,UK,LUX]\n" +
                "LUX: [DE,BE,FR]\n", europe.formatAdjacencyList(nl));
        assertEquals("Graph adjacency list:\n" +
                "RO: [HU]\n" +
                "HU: [RO]\n", europe.formatAdjacencyList(ro));
    }

    @Test
    void depthFirstSearchShouldFindPathAndVisited() {
        AbstractGraph.GPath path = europe.depthFirstSearch(uk,lux);
        assertNotNull(path);
        assertSame(uk, path.getVertices().peek(),
                "First country in path should match the start");
        assertSame(lux, path.getVertices().stream().reduce((c1,c2)->c2).get(),
                "Last country in path should match the target");
        assertTrue(path.getVertices().size() >= 3);
        assertTrue(path.getVisited().size() >= path.getVertices().size());
    }

    @Test
    void depthFirstSearchShouldWorkWhenStartIsTarget() {
        AbstractGraph.GPath path = europe.depthFirstSearch(hu,hu);
        assertNotNull(path);
        assertSame(hu, path.getVertices().peek());
        assertEquals(1, path.getVertices().size());
        assertEquals(1, path.getVisited().size());
    }

    @Test
    void depthFirstSearchShouldDetectUnconnected() {
        AbstractGraph.GPath path = europe.depthFirstSearch(uk,hu);
        assertNull(path);
    }

    @Test
    void breadthFirstSearchShouldFindPathAndVisited() {
        AbstractGraph.GPath path = europe.breadthFirstSearch(uk,lux);
        assertNotNull(path);
        assertSame(uk, path.getVertices().peek(),
                "First country in path should match the start");
        assertSame(lux, path.getVertices().stream().reduce((c1,c2)->c2).get(),
                "Last country in path should match the target");
        assertEquals(3, path.getVertices().size());
        assertTrue(path.getVisited().size() >= path.getVertices().size());
    }

    @Test
    void breadthFirstSearchShouldWorkWhenStartIsTarget() {
        AbstractGraph.GPath path = europe.breadthFirstSearch(hu,hu);
        assertNotNull(path);
        assertSame(hu, path.getVertices().peek());
        assertEquals(1, path.getVertices().size());
        assertEquals(1, path.getVisited().size());
    }

    @Test
    void breadthFirstSearchShouldDetectUnconnected() {
        AbstractGraph.GPath path = europe.breadthFirstSearch(uk,hu);
        assertNull(path);
    }

    @Test
    void dijkstraShortestPathShouldFindPathAndVisited() {
        AbstractGraph.GPath path = europe.dijkstraShortestPath(uk,lux, Country::distanceTo);
        assertNotNull(path);
        assertSame(uk, path.getVertices().peek(),
                "First country in path should match the start");
        assertSame(lux, path.getVertices().stream().reduce((c1,c2)->c2).get(),
                "Last country in path should match the target");
        assertEquals(130.0, path.getTotalWeight(), 0.0001);
        assertEquals(3, path.getVertices().size());
        assertTrue(path.getVisited().size() >= path.getVertices().size());
    }

    @Test
    void dijkstraShortestPathShouldWorkWhenStartIsTarget() {
        AbstractGraph.GPath path = europe.dijkstraShortestPath(hu,hu, Country::distanceTo);
        assertNotNull(path);
        assertEquals(hu, path.getVertices().peek());
        assertEquals(0.0, path.getTotalWeight(), 0.0001);
        assertEquals(1, path.getVertices().size());
        assertEquals(1, path.getVisited().size());
    }

    @Test
    void dijkstraShortestPathShouldDetectUnconnected() {
        AbstractGraph.GPath path = europe.dijkstraShortestPath(uk,hu, Country::distanceTo);
        assertNull(path);
    }
}

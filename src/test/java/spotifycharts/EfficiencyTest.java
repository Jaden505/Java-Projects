package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class EfficiencyTest {
    private SongSorter songSorter;
    private SorterImpl<Song> test;
    private List<Song> fewSongs;
    private List<Song> manySongs;
    private List<Song> alot;
    private Comparator<Song> rankingScheme = Song::compareByHighestStreamsCountTotal;
    private List<Song> heap;

    private Comparator<Song> heapComparator = Comparator.comparing(Song::getTitle);

    @BeforeEach
    void setup() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(1L);
        this.songSorter = new SongSorter();
        test = new SorterImpl<>();
        fewSongs = new ArrayList(chartsCalculator.registerStreamedSongs(10));
        alot = new ArrayList<>(fewSongs);
    }

    void HeapCheacker() {
        System.gc();
        test.topsHeapSort(10, alot, Comparator.comparing(Song::getStreamsCountTotal));

    }
    @Test
    public void heapTester(){
        long start = System.nanoTime();
        HeapCheacker();
        long end = System.nanoTime();
        long duration = end - start;

        System.out.println("heapTester Time:"+duration);
    }

    void bubbleChecker() {
        System.gc();
        test.selInsBubSort(alot, Comparator.comparing(Song::getStreamsCountTotal));

    }
    @Test
    public void bubbleTester(){
        long start = System.nanoTime();
        bubbleChecker();
        long end = System.nanoTime();
        long duration = end - start;

        System.out.println("bubbleChecker Time:"+duration);
    }

    void quicksortChecker() {
        System.gc();
        test.quickSort(alot, Comparator.comparing(Song::getStreamsCountTotal));

    }
    @Test
    public void quicksortTester(){
        long start = System.nanoTime();
        quicksortChecker();
        long end = System.nanoTime();
        long duration = end - start;

        System.out.println("quicksortTester Time:"+duration);
    }

}

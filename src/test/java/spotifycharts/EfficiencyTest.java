package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class EfficiencyTest {
    private SongSorter songSorter;
    private SorterImpl<Song> test;
    private List<Song> fewSongs;
    private List<Song> alot;

    @BeforeEach
    void setup() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(1L);
        this.songSorter = new SongSorter();
        test = new SorterImpl<>();
        fewSongs = new ArrayList(chartsCalculator.registerStreamedSongs(1000000));
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

//        System.out.println("heapTester Time:"+duration);

        double heapTimeInSecond = (double) duration / 1_000_000_000;
        System.out.println("heapTester Time: "+ heapTimeInSecond + " seconds");

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

//        System.out.println("bubbleChecker Time:"+duration);

        double bubbleTimeInSecond = (double) duration / 1_000_000_000;
        System.out.println("bubbleChecker Time: "+ bubbleTimeInSecond + " seconds");
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

//        System.out.println("quicksortTester Time:"+duration);

        double quickSortTimeInSecond = (double) duration / 1_000_000_000;
        System.out.println("quicksortTester Time: "+ quickSortTimeInSecond + " seconds");
    }

}

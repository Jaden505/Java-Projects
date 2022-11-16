package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SongSorterTest {
    private SongSorter songSorter;
    private List<Song> fewSongs;
    private List<Song> manySongs;
    private Comparator<Song> rankingScheme = Song::compareByHighestStreamsCountTotal;
    private List<Song> heap;
    private Comparator<Song> heapComparator = Comparator.comparing(Song::getTitle);

    @BeforeEach
    void setup() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(1L);
        this.songSorter = new SongSorter();
        fewSongs = new ArrayList(chartsCalculator.registerStreamedSongs(23));
        manySongs = new ArrayList(chartsCalculator.registerStreamedSongs(250));
    }

    @Test
    void selInsBubSortAndCollectionSortYieldSameOrder() {
        customSortAndCollectionSortResultInSameOrder(songSorter::selInsBubSort);
    }

    @Test
    void quickSortAndCollectionSortYieldSameOrder() {
        customSortAndCollectionSortResultInSameOrder(songSorter::quickSort);
    }

    private void customSortAndCollectionSortResultInSameOrder(BiFunction<List<Song>,Comparator,List<Song>> sorterMethod) {
        List<Song> fewSortedSongs = new ArrayList<>(fewSongs);
        Collections.shuffle(fewSortedSongs);
        List<Song> manySortedSongs = new ArrayList<>(manySongs);
        Collections.shuffle(manySortedSongs);

        sorterMethod.apply(fewSortedSongs, Comparator.comparing(Song::getTitle));
        fewSongs.sort(Comparator.comparing(Song::getTitle));
        String difference = findFirstDifference(fewSongs, fewSortedSongs, Comparator.comparing(Song::getTitle), 3);
        assertNull(difference, difference);

        sorterMethod.apply(manySortedSongs, Comparator.comparing(Song::getArtist));
        manySongs.sort(Comparator.comparing(Song::getArtist));
        difference = findFirstDifference(manySongs, manySortedSongs, Comparator.comparing(Song::getArtist), 3);
        assertNull(difference, difference);

        sorterMethod.apply(fewSortedSongs, rankingScheme);
        fewSongs.sort(rankingScheme);
        difference = findFirstDifference(fewSongs, fewSortedSongs, rankingScheme, 3);
        assertNull(difference, difference);

        sorterMethod.apply(manySortedSongs, rankingScheme);
        manySongs.sort(rankingScheme);
        difference = findFirstDifference(manySongs, manySortedSongs, rankingScheme, 3);
        assertNull(difference, difference);
    }


    @Test
    void topsHeapSortAndCollectionSortYieldSameOrder() {
        List<Song> fewSortedSongs = new ArrayList<>(fewSongs);
        Collections.shuffle(fewSortedSongs);
        List<Song> manySortedSongs = new ArrayList<>(manySongs);
        Collections.shuffle(manySortedSongs);

        songSorter.topsHeapSort(5, fewSortedSongs, Comparator.comparing(Song::getTitle));
        fewSongs.sort(Comparator.comparing(Song::getTitle));
        assertEquals(fewSongs.subList(0,5).stream().map(Song::getTitle).collect(Collectors.toList()),
                fewSortedSongs.subList(0,5).stream().map(Song::getTitle).collect(Collectors.toList()));

        songSorter.topsHeapSort(1, manySortedSongs, rankingScheme);
        manySongs.sort(rankingScheme);
        assertEquals(manySongs.get(0), manySortedSongs.get(0));

        songSorter.topsHeapSort(25, manySortedSongs, rankingScheme);
        assertEquals(manySongs.subList(0,25), manySortedSongs.subList(0,25));
    }

    public static <E> String findFirstDifference(List<E> expected, List<E> actual, Comparator<E> ranker, int displayLength) {
        if (expected.size() != actual.size()) {
            return String.format("Expected list with size=%d, got %d", expected.size(), actual.size());
        }
        for (int i = 0; i < expected.size(); i++) {
            if (ranker.compare(actual.get(i), expected.get(i)) != 0) {
                int subListEnd = Integer.min(i+displayLength, expected.size());
                return String.format("Expected items[%d..%d] = %s,\n   got: %s", i, subListEnd-1,
                        expected.subList(i,subListEnd), actual.subList(i,subListEnd));
            }
        }
        return null;
    }

    @Test
    void swimShouldBuildAHeapOf6Correctly() {
        this.heap = new ArrayList<>(this.fewSongs.subList(0,6));

        // boundary condition test on heap of size 1
        this.songSorter.heapSwim(this.heap,1,this.heapComparator);
        this.checkHeapCondition(1);

        // swim heap[1]
        this.songSorter.heapSwim(this.heap,2,this.heapComparator);
        this.checkHeapCondition(2);

        // swim heap[2]
        this.songSorter.heapSwim(this.heap,3,this.heapComparator);
        this.checkHeapCondition(3);

        // swim heap[3]
        this.songSorter.heapSwim(this.heap,4,this.heapComparator);
        this.checkHeapCondition(4);

        // swim heap[4]
        this.songSorter.heapSwim(this.heap,5,this.heapComparator);
        this.checkHeapCondition(5);

        // swim heap[5]
        this.songSorter.heapSwim(this.heap,6,this.heapComparator);
        this.checkHeapCondition(6);
    }

    @Test
    void sinkShouldSortAHeapOf6Correctly() {

        // first build the heap correctly
        this.swimShouldBuildAHeapOf6Correctly();

        // fix heap[5]
        Collections.swap(this.heap, 0, 5);
        this.songSorter.heapSink(this.heap, 5, this.heapComparator);
        this.checkHeapCondition(5);

        // fix heap[4]
        Collections.swap(this.heap, 0, 4);
        this.songSorter.heapSink(this.heap, 4, this.heapComparator);
        this.checkHeapCondition(4);

        // fix heap[3]
        Collections.swap(this.heap, 0, 3);
        this.songSorter.heapSink(this.heap, 3, this.heapComparator);
        this.checkHeapCondition(3);

        // fix heap[2]
        Collections.swap(this.heap, 0, 2);
        this.songSorter.heapSink(this.heap, 2, this.heapComparator);
        this.checkHeapCondition(2);

        // fix heap[1]
        Collections.swap(this.heap, 0, 1);
        this.songSorter.heapSink(this.heap, 1, this.heapComparator);
        this.checkHeapCondition(1);
    }

    private void checkHeapCondition(int heapSize) {
        if (heapSize > 1) {
            assertTrue(this.heapComparator.compare(this.heap.get(0),this.heap.get(1)) <= 0,
                    String.format("heap[0]='%s' should preceed heap[1]='%s' in zero-based heap of size=%d ",
                            this.heap.get(0).getTitle(), this.heap.get(1).getTitle(), heapSize));
        }
        if (heapSize > 2) {
            assertTrue(this.heapComparator.compare(this.heap.get(0),this.heap.get(2)) <= 0,
                    String.format("heap[0]='%s' should preceed heap[2]='%s' in zero-based heap of size=%d ",
                            this.heap.get(0).getTitle(), this.heap.get(2).getTitle(), heapSize));
        }
        if (heapSize > 3) {
            assertTrue(this.heapComparator.compare(this.heap.get(1),this.heap.get(3)) <= 0,
                    String.format("heap[1]='%s' should preceed heap[3]='%s' in zero-based heap of size=%d ",
                            this.heap.get(1).getTitle(), this.heap.get(3).getTitle(), heapSize));
        }
        if (heapSize > 4) {
            assertTrue(this.heapComparator.compare(this.heap.get(1),this.heap.get(4)) <= 0,
                    String.format("heap[1]='%s' should preceed heap[4]='%s' in zero-based heap of size=%d ",
                            this.heap.get(1).getTitle(), this.heap.get(4).getTitle(), heapSize));
        }
        if (heapSize > 5) {
            assertTrue(this.heapComparator.compare(this.heap.get(2),this.heap.get(5)) <= 0,
                    String.format("heap[2]='%s' should preceed heap[5]='%s' in zero-based heap of size=%d ",
                            this.heap.get(2).getTitle(), this.heap.get(5).getTitle(), heapSize));
        }
        assertEquals(new HashSet(this.fewSongs.subList(0,6)), new HashSet(this.heap),
                "The overall content of the heap should not change, only the order of its items");
    }

}

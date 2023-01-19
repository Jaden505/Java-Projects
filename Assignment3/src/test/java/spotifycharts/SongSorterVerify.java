package spotifycharts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SongSorterVerify {
    private SongSorter songSorter;
    private List<Song> fewSongs;
    private static Comparator<Song> rankingSchemeTotal;

    @BeforeEach
    void setup() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(1L);
        this.songSorter = new SongSorter();
        fewSongs = new ArrayList(chartsCalculator.registerStreamedSongs(23));
        rankingSchemeTotal = Song::compareByHighestStreamsCountTotal;
    }

    @Test
    void checkSwappingDoneCorrectly() {
        Song temp = fewSongs.get(0);
        fewSongs = songSorter.swap(0, 1, fewSongs);
        assertEquals(temp, fewSongs.get(1));
    }

    @Test
    void checkSongsSizeAfterSorts() {
        int temp = fewSongs.size();
        songSorter.topsHeapSort(fewSongs.size(), fewSongs, rankingSchemeTotal);
        assertEquals(temp, fewSongs.size(), "Array size not the same after sorting on whole array");
    }
}

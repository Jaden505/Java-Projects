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

    @BeforeEach
    void setup() {
        ChartsCalculator chartsCalculator = new ChartsCalculator(1L);
        this.songSorter = new SongSorter();
        fewSongs = new ArrayList(chartsCalculator.registerStreamedSongs(23));
    }

    @Test
    void checkSwappingDoneCorrectly() {
        Song temp = fewSongs.get(0);
        fewSongs = songSorter.swap(0, 1, fewSongs);
        assertEquals(temp, fewSongs.get(1));
    }
}

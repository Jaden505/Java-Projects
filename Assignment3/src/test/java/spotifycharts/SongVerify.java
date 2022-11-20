package spotifycharts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongVerify {

    private static Comparator<Song> rankingSchemeTotal, rankingSchemeDutchNational;
    Song songBYC, songKKA, songTS, songJVT, songBB;

    @BeforeAll
    static void setupClass() {
        rankingSchemeTotal = Song::compareByHighestStreamsCountTotal;
        rankingSchemeDutchNational = Song::compareForDutchNationalChart;
    }

    @BeforeEach
    void setup() {

        long begin= System.nanoTime();
        long end = System.nanoTime();
        long duration = end - begin;



        songBYC = new Song("Beyoncé", "CUFF IT", Song.Language.EN);
        songBYC.setStreamsCountOfCountry(Song.Country.UK,100);
        songBYC.setStreamsCountOfCountry(Song.Country.NL,40);
        songBYC.setStreamsCountOfCountry(Song.Country.BE,20);

        songTS = new Song("Taylor Swift", "Anti-Hero", Song.Language.EN);
        songTS.setStreamsCountOfCountry(Song.Country.UK,100);
        songTS.setStreamsCountOfCountry(Song.Country.DE,60);


        songKKA = new Song("Kris Kross Amsterdam", "Vluchtstrook", Song.Language.NL);
        songJVT = new Song("De Jeugd Van Tegenwoordig", "Sterrenstof", Song.Language.NL);
        songJVT.setStreamsCountOfCountry(Song.Country.NL,70);


        songBB = new Song("Bad Bunny", "La Coriente", Song.Language.SP);
    }

    @Test
    void compareEqualSongsReturnZero() {
        assertEquals(0, rankingSchemeTotal.compare(songTS, songTS), "Comparing song to itself not return zero (equal)");
        assertEquals(0, rankingSchemeDutchNational.compare(songBYC, songBYC), "Comparing song to itself not return zero (equal)");
    }

    @Test
    void compareEqualsNegativeBackwards() {
        assertEquals(-rankingSchemeTotal.compare(songBYC, songTS), rankingSchemeTotal.compare(songTS, songBYC), "Compare elements switched places don't equal it's negative");
        assertEquals(-rankingSchemeDutchNational.compare(songBYC, songBB), rankingSchemeDutchNational.compare(songBB, songBYC), "Compare elements switched places don't equal it's negative");
    }
}

package spotifycharts;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SongTest {

    private static Comparator<Song> rankingSchemeTotal, rankingSchemeDutchNational;
    Song songBYC, songKKA, songTS, songJVT, songBB;

    @BeforeAll
    static void setupClass() {
        rankingSchemeTotal = Song::compareByHighestStreamsCountTotal;
        rankingSchemeDutchNational = Song::compareForDutchNationalChart;
    }

    @BeforeEach
    void setup() {
        songBYC = new Song("Beyoncé", "CUFF IT", Song.Language.EN);
        songBYC.setStreamsCountOfCountry(Song.Country.UK,100);
        songBYC.setStreamsCountOfCountry(Song.Country.NL,40);
        songBYC.setStreamsCountOfCountry(Song.Country.BE,20);
        songTS = new Song("Taylor Swift", "Anti-Hero", Song.Language.EN);
        songTS.setStreamsCountOfCountry(Song.Country.UK,100);
        songTS.setStreamsCountOfCountry(Song.Country.DE,60);
        songKKA = new Song("Kris Kross Amsterdam", "Vluchtstrook", Song.Language.NL);
        songKKA.setStreamsCountOfCountry(Song.Country.NL,40);
        songKKA.setStreamsCountOfCountry(Song.Country.BE,30);
        songJVT = new Song("De Jeugd Van Tegenwoordig", "Sterrenstof", Song.Language.NL);
        songJVT.setStreamsCountOfCountry(Song.Country.NL,70);
        songBB = new Song("Bad Bunny", "La Coriente", Song.Language.SP);
    }

    @Test
    void songStreamsCountSetsAndGetsCorrectly() {
        assertEquals(40, songBYC.getStreamsCountOfCountry(Song.Country.NL),
                "Streams count was not registered correctly");
        assertEquals(20, songBYC.getStreamsCountOfCountry(Song.Country.BE),
                "Streams count was not registered correctly");
        assertEquals(0, songBYC.getStreamsCountOfCountry(Song.Country.FR),
                "Empty streams count is not reported correctly");

        songBYC.setStreamsCountOfCountry(Song.Country.NL,30);
        assertEquals(30, songBYC.getStreamsCountOfCountry(Song.Country.NL),
                "Streams count was not updated correctly");
        songBYC.setStreamsCountOfCountry(Song.Country.FR,35);
        assertEquals(35, songBYC.getStreamsCountOfCountry(Song.Country.FR),
                "Streams count was not registered correctly");
        songBYC.setStreamsCountOfCountry(Song.Country.NL,0);
        assertEquals(0, songBYC.getStreamsCountOfCountry(Song.Country.NL),
                "Streams count was not updated correctly");
        assertEquals(20, songBYC.getStreamsCountOfCountry(Song.Country.BE),
                "Streams count was not kept correctly");

        // song without any stream
        assertEquals(0, songBB.getStreamsCountOfCountry(Song.Country.NL),
                "Empty streams count is not reported correctly");
    }


    @Test
    void toStringFormatsCorrectly() {
        assertEquals("Beyoncé/CUFF IT{EN}(160)", songBYC.toString());
        assertEquals("Kris Kross Amsterdam/Vluchtstrook{NL}(70)", songKKA.toString());
        assertEquals("Bad Bunny/La Coriente{SP}(0)", songBB.toString());
    }

    @Test
    public void streamsCountTotalCalculatesCorrectly() {
        assertEquals(160, songBYC.getStreamsCountTotal(),
                "Streams count total was not calculated correctly");
        assertEquals(70, songKKA.getStreamsCountTotal(),
                "Streams count total was not calculated correctly");
        assertEquals(0, songBB.getStreamsCountTotal(),
                "Streams count total was not calculated correctly");

        songBYC.setStreamsCountOfCountry(Song.Country.NL,30);
        assertEquals(150, songBYC.getStreamsCountTotal(),
                "Streams count total was not updated correctly");
        songBYC.setStreamsCountOfCountry(Song.Country.FR,35);
        assertEquals(185, songBYC.getStreamsCountTotal(),
                "Streams count total was not updated correctly");
        songBYC.setStreamsCountOfCountry(Song.Country.NL,0);
        assertEquals(155, songBYC.getStreamsCountTotal(),
                "Streams count total was not updated correctly");
    }

    @Test
    void rankingSchemeTotalComparesCorrectly() {
        checkRankingScheme("Streams count total", rankingSchemeTotal, -1, 0, +1, 0, +1);
    }

    @Test
    void rankingSchemeDutchNationalComparesCorrectly() {
        checkRankingScheme("Dutch national", rankingSchemeDutchNational, +1, 0, -1, 0, +1);
    }

    private static int sign(int a) {
        return a > 0 ? +1 : a < 0 ? -1 : a;
    }
    private void checkRankingScheme(String schemeName, Comparator<Song> rankingScheme,
                                   int bycVSkka, int bycVSts, int kkaVSts, int kkaVSjvt, int bbVSbyc) {
        assertEquals(bycVSkka, sign(rankingScheme.compare(songBYC, songKKA)),
                String.format("'%s'-comparator does not properly compare '%s' with '%s'", schemeName, songBYC, songKKA));
        assertEquals(bycVSts, sign(rankingScheme.compare(songBYC, songTS)),
                String.format("'%s'-comparator does not properly compare '%s' with '%s'", schemeName, songBYC, songTS));
        assertEquals(kkaVSts, sign(rankingScheme.compare(songKKA, songTS)),
                String.format("'%s'-comparator does not properly compare '%s' with '%s'", schemeName, songKKA, songTS));
        assertEquals(kkaVSjvt, sign(rankingScheme.compare(songKKA, songJVT)),
                String.format("'%s'-comparator does not properly compare '%s' with '%s'", schemeName, songKKA, songJVT));
        assertEquals(bbVSbyc, sign(rankingScheme.compare(songBB, songBYC)),
                String.format("'%s'-comparator does not properly compare '%s' with '%s'", schemeName, songBB, songBYC));
    }
}

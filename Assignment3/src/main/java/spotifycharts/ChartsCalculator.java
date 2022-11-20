package spotifycharts;

import java.util.*;

public class ChartsCalculator {
    private final static int MAX_STREAM_COUNT = 10000;

    private Random randomizer;

    private List<Song> songs = new ArrayList<>();
    public List<Song> getSongs() { return this.songs; }

    public ChartsCalculator(long seed) {
        randomizer = new Random(seed);
        // propagate the seed for test data generation
        SongBuilder.reSeed(randomizer.nextLong());
    }


    /**
     * Prepares test data for a number of songs.
     * Semi-random, per country streams counts are associated with each song.
     * @param nrOfSongs     the number of songs that shall be prepared in the test data set
     * @return              the complete list of all songs that have been prepared
     */
    public List<Song> registerStreamedSongs(int nrOfSongs) {
        // force a different random streamCounts sequence for a different number of songs
        randomizer.nextInt(nrOfSongs);

        for (int i = 0; i < nrOfSongs; i++) {
            // prepares a sample song from a known set of titles
            Song song = SongBuilder.createSample(i);
            songs.add(song);
            // prepares semi-random per-country steams counts
            obtainStreamCounts(song);
        }
        return songs;
    }

    /**
     * Calculates the charts and shows key results
     */
    public void showResults() {
        Sorter<Song> sorter = new SongSorter();
        System.out.printf("%d songs have been included in this week's charts \n", songs.size());

        Collections.shuffle(songs);

        sorter.selInsBubSort(songs, Song::compareByHighestStreamsCountTotal);
        System.out.printf("\nThe five most streamed songs are:\n%s\n", songs.subList(0,5));

        sorter.quickSort(songs, Song::compareForDutchNationalChart);
        System.out.printf("\nThe top-five in the Dutch-language national chart are:\n%s\n", songs.subList(0,5));

        sorter.topsHeapSort(10, songs, Comparator.comparing(Song::getStreamsCountTotal));
        System.out.printf("\nThe bottom-ten least streamed songs are:\n%s\n", songs.subList(0,10));
    }

    // country relative sizes
    private static int[] countryMultipliers = {1,3,3,1,3,2,2};

    /**
     * Prepares semi-random, per-country streams counts for a song
     * @param song
     */
    public void obtainStreamCounts(Song song) {
        for (Song.Country country : Song.Country.values()) {
            // apply some realistic, language dependent multiplication factors
            int languageMultiplier =
                    (song.getLanguage() == Song.Language.EN || song.getLanguage().toString().equals(country.toString())) ? 2 : 1;
            song.setStreamsCountOfCountry(country,
                    randomizer.nextInt(MAX_STREAM_COUNT * languageMultiplier * countryMultipliers[country.ordinal()]));
        }
    }
}

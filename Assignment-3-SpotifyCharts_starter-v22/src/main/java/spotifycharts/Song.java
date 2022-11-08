package spotifycharts;

public class Song {

    public enum Language {
        NL, // Dutch
        EN, // English
        DE, // German
        FR, // French
        SP, // Spanish
        IT, // Italian
    }

    public enum Country {
        NL, // Netherlands
        UK, // United Kingdom
        DE, // Germany
        BE, // Belgium
        FR, // France
        SP, // Spain
        IT  // Italy
    }

    private final String artist;
    private final String title;
    private final Language language;

    // TODO add instance variable(s) to track the streams counts per country
    //  choose a data structure that you deem to be most appropriate for this application.



    /**
     * Constructs a new instance of Song based on given attribute values
     */
    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;

        // TODO initialise streams counts per country as appropriate.

    }

    /**
     * Sets the given streams count for the given country on this song
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        // TODO register the streams count for the given country.

    }

    /**
     * retrieves the streams count of a given country from this song
     * @param country
     * @return
     */
    public int getStreamsCountOfCountry(Country country) {
        // TODO retrieve the streams count for the given country.


        return 0; // replace by the proper amount
    }
    /**
     * Calculates/retrieves the total of all streams counts across all countries from this song
     * @return
     */
    public int getStreamsCountTotal() {
        // TODO calculate/get the total number of streams across all countries


        return 0; // replace by the proper amount
    }


    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestStreamsCountTotal(Song other) {
        // TODO compare the total of stream counts of this song across all countries
        //  with the total of the other song


        return 0;    // replace by proper result
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        // TODO compare this song with the other song
        //  ordening all Dutch songs upfront and then by decreasing total number of streams



        return 0;    // replace by proper result
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }

    // TODO provide a toString implementation to format songs as in "artist/title{language}(total streamsCount)"


}

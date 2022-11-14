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

    private int totalSteamCount;

    public int countNL;
    public int countUK;
    public int countDE;
    public int countBE;
    public int countFR;
    public int countSP;
    public int countIT;

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

    public Song(String artist, String title, Language language, int totalSteamCount, int countNL, int countUK, int countDE, int countBE, int countFR, int countSP, int countIT) {
        this.artist = artist;
        this.title = title;
        this.language = language;
        this.totalSteamCount = totalSteamCount;
        this.countNL = countNL;
        this.countUK = countUK;
        this.countDE = countDE;
        this.countBE = countBE;
        this.countFR = countFR;
        this.countSP = countSP;
        this.countIT = countIT;
    }
    /**
     * Sets the given streams count for the given country on this song
     * @param country
     * @param streamsCount
     */
    public void setStreamsCountOfCountry(Country country, int streamsCount) {
        // TODO register the streams count for the given country.
        switch (country){
            case NL: countNL = streamsCount;
            break;
            case UK: countUK = streamsCount;
            break;
            case DE: countDE = streamsCount;
            break;
            case BE: countBE = streamsCount;
            break;
            case FR: countFR = streamsCount;
            break;
            case SP: countSP = streamsCount;
            break;
            case IT: countIT = streamsCount;
            break;
        }

    }

    /**
     * retrieves the streams count of a given country from this song
     * @param country
     * @return
     */
    public int getStreamsCountOfCountry(Country country) {
        // TODO retrieve the streams count for the given country.
        int count = 0;
        switch (country){
            case NL: countNL = count;
            break;
            case UK: countUK = count;
            break;
            case DE: countDE = count;
            break;
            case BE: countBE = count;
            break;
            case FR: countFR = count;
            break;
            case SP: countSP = count;;
            break;
            case IT: countIT = count;
            break;
        }
        count = this.totalSteamCount;


        return count;
    }
    /**
     * Calculates/retrieves the total of all streams counts across all countries from this song
     * @return
     */
    public int getStreamsCountTotal() {
        // TODO calculate/get the total number of streams across all countries
        int streamsCountTotal = countDE + countIT + countUK
                +countNL + countSP + countFR + countBE;

        System.out.println(streamsCountTotal);
        return streamsCountTotal; // replace by the proper amount

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


        return this.artist.compareTo(other.artist);
        //work in progess
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


        return this.countNL;
        //work in progess
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


    @Override
    public String toString() {
        return artist + "/" + title + "{" + language + "}" + "(total " + getStreamsCountTotal() + ")";

    }

    // TODO provide a toString implementation to format songs as in "artist/title{language}(total streamsCount)"


}

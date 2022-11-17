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

    /**
     * Constructs a new instance of Song based on given attribute values
     */

    public Song(String artist, String title, Language language) {
        this.artist = artist;
        this.title = title;
        this.language = language;

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
        int streamsCountTotal = countDE + countIT + countUK
                +countNL + countSP + countFR + countBE;
        return streamsCountTotal; // replace by the proper amount
    }


    /**
     * compares this song with the other song
     * ordening songs with the highest total number of streams upfront
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator convention
     */

    public int compareByHighestStreamsCountTotal(Song other) {
        if (this.getStreamsCountTotal() > other.getStreamsCountTotal()){
            return this.getStreamsCountTotal();
        } else {
            return other.getStreamsCountTotal();
        }
        // TODO compare the total of stream counts of this song across all countries
        //  with the total of the other song
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        if (this.getStreamsCountOfCountry(Country.NL) > other.getStreamsCountOfCountry(Country.NL)){
            return this.getStreamsCountOfCountry(Country.NL);
        }
        else{
            return other.getStreamsCountOfCountry(Country.NL);
        }
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
}

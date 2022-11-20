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

    public Song(String artist, String title, Language language,  int countNL, int countUK, int countDE, int countBE, int countFR, int countSP, int countIT) {
        this.artist = artist;
        this.title = title;
        this.language = language;
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
        if (country == Country.NL){
            return countNL;
        } else if (country == Country.UK) {
            return countUK;
        }else if (country == Country.DE) {
            return countDE;
        }else if (country == Country.BE) {
            return countBE;
        }else if (country == Country.FR) {
            return countFR;
        }else if (country == Country.SP) {
            return countNL;
        }else {
            return countIT;
        }
    }

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
        return -Integer.compare(this.getStreamsCountTotal(), other.getStreamsCountTotal());
    }

    /**
     * compares this song with the other song
     * ordening all Dutch songs upfront and then by decreasing total number of streams
     * @param other     the other song to compare against
     * @return  negative number, zero or positive number according to Comparator conventions
     */
    public int compareForDutchNationalChart(Song other) {
        int languagecompare = this.language.compareTo(other.language);
        if (languagecompare == 0){
            return -Integer.compare(this.getStreamsCountTotal(), other.getStreamsCountTotal());
        }
        return languagecompare;

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
        return artist + "/" + title + "{" + language + "}" + "(" + getStreamsCountTotal() + ")";
    }
}

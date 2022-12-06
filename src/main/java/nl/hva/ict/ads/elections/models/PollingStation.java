package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.xml.XMLParser;

import javax.xml.stream.XMLStreamException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A polling station counts the number of votes cast at the polling station for each candidate in the constituency
 * Each polling station has an Id, but multiple vote counts can be submitted for the same polling station
 * possibly distinguishing different voting days, or votes by mail.
 */
public class PollingStation {

    private final String id;
    private final String zipCode;
    private final String name;

    /**
     *  Provides for each candidate participating in the elections of the constituency the number of casted votes
     */
    private Map<Candidate, Integer> votesByCandidate;     // counts the votes per candidate

    public PollingStation(String id, String zipCode, String name) {
        this.id = id;
        this.zipCode = zipCode;
        this.name = name;

        // TODO initialise this.votesByCandidate with an appropriate Map implementation



    }

    /**
     * Adds the given number of votes for the candidate in the votes count of this polling station
     * @param candidate
     * @param numberOfVotes
     */
    public void addVotes(Candidate candidate, int numberOfVotes) {
        // TODO add the number of votes for the candidate
        //   hint: the best quality solution used one line of code...



    }

    public int getVotes(Candidate candidate) {
        return this.votesByCandidate.get(candidate);
    }

    /**
     * Accumulates all votes on candidates into a total vote count per party in the polling station
     * @return the total number of votes in this polling station per party.
     */
    public Map<Party, Integer> getVotesByParty() {
        // TODO accumulate the votes per candidate into a map of total vote counts by party


        return null; // replace by a proper outcome
    }

    /**
     * migrate votes from one polling station into another
     * this method is used for data cleansing when duplicate entries of polling stations are found in the data file.
     * @param target
     */
    public void combineVotesWith(PollingStation target) {
        // merge the votes of this polling station into the target
        this.getVotesByCandidate().entrySet().forEach(e -> target.addVotes(e.getKey(),e.getValue()));
        System.out.printf("\nHave combined votes of %s into %s ", this, target);
        this.getVotesByCandidate().clear();
    }

    @Override
    public String toString() {
        return "PollingStation{" +
                "id='" + id + "'" +
                ",zipCode='" + zipCode + "'" +
                ",name='" + name + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PollingStation)) return false;
        PollingStation other = (PollingStation) o;
        return this.id.equals(other.id) && this.zipCode.equals(other.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,zipCode);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Map<Candidate, Integer> getVotesByCandidate() {
        return this.votesByCandidate;
    }

    private static final Logger LOG = Logger.getLogger(PollingStation.class.getName());
    public static final String POLLING_STATION_IDENTIFIER = "ReportingUnitIdentifier";
    public static final String ID = "Id";
    public static final String SELECTION = "Selection";
    public static final String POLLING_STATION_VOTES = "ReportingUnitVotes";
    public static final String VALID_VOTES = "ValidVotes";
    public static final String NO_ZIPCODE = "";
    /**
     * Auxiliary method for parsing the data from the EML files
     * This method can be used as-is and does not require your investigation or extension.
     */
    public static PollingStation importFromXml(XMLParser parser, Constituency constituency, Map<Integer,Party> parties) throws XMLStreamException {
        if (parser.findBeginTag(POLLING_STATION_VOTES)) {
            String id = null;
            String name = null;
            String zipCode = NO_ZIPCODE;
            if (parser.findBeginTag(POLLING_STATION_IDENTIFIER)) {
                id = parser.getAttributeValue(null, ID);
                name = parser.getElementText().replace("Stembureau Stembureau","Stembureau");
                parser.findAndAcceptEndTag(POLLING_STATION_IDENTIFIER);
                int postCodeIndex = name.indexOf("(postcode:");
                if (postCodeIndex >= 0) {
                    int postCodeEndIndex = name.indexOf(')', postCodeIndex);
                    if (postCodeEndIndex > postCodeIndex) {
                        zipCode = name.substring(postCodeIndex+10, postCodeEndIndex).replace(" ","").toUpperCase();
                        name = name.substring(0,postCodeIndex).trim() + name.substring(postCodeEndIndex+1).trim();
                    }
                }
                if (name.toLowerCase().contains("postcode")) {
                    System.out.printf("Could not extract all 'postcode' from polling station '%s' in %s\n", name, constituency);
                }
            }

            PollingStation pollingStation = new PollingStation(id /* +"/"+(500+name.hashCode()%500) */, zipCode, name);

            int partyId = 0;
            while (pollingStation != null && parser.getLocalName().equals(SELECTION)) {
                parser.next();
                switch (parser.getLocalName()) {
                    case Party.PARTY_IDENTIFIER:
                        partyId = parser.getIntegerAttributeValue(null, Party.ID, 0);
                        parser.findAndAcceptEndTag(Party.PARTY_IDENTIFIER);
                        if (parser.findBeginTag(VALID_VOTES)) {
                            parser.findAndAcceptEndTag(VALID_VOTES);
                        }
                        break;
                    case Candidate.CANDIDATE:
                        int rank = 0;
                        if (parser.findBeginTag(Candidate.CANDIDATE_IDENTIFIER)) {
                            rank = parser.getIntegerAttributeValue(null, Candidate.RANK, 0);
                        }

                        parser.findAndAcceptEndTag(Candidate.CANDIDATE);
                        if (parser.findBeginTag(VALID_VOTES)) {
                            int voteCount = Integer.valueOf(parser.getElementText());
                            parser.findAndAcceptEndTag(VALID_VOTES);
                            // Add votes to structure
                            Party party = parties.get(partyId);
                            if (party == null) {
                                System.out.printf("Cannot count votes in polling station %s for unknown partyId=%d\n",
                                        pollingStation, partyId);
                            }
                            Candidate candidate = constituency.getCandidate(party, rank);
                            if (candidate == null) {
                                System.out.printf("Cannot count votes in polling station %s for unknown candidate of %s, rank=%d\n",
                                        pollingStation, party, rank);
                            } else {
                                pollingStation.addVotes(candidate, voteCount);
                            }
                        }
                        break;
                    default:
                        LOG.warning("Unknown element [" + parser.getLocalName() + "] found!");
                }
                parser.findAndAcceptEndTag(SELECTION);
            }

            parser.findAndAcceptEndTag(POLLING_STATION_VOTES);
            return pollingStation;
        }
        return null;
    }
}

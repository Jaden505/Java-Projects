package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.xml.XMLParser;

import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * A party that participates in the elections
 * Every party is identified by a unique Id
 * Every party maintains a list of Candidates for the election
 * All candidates have a unique (full) name within their party
 * A party shall register (some of) its candidates into one or more Constituencies for participation.
 * Different, ranked Candidate lists may be registered into different constituencies.
 * Votes can be collected by Candidate and by Party across all Constituencies
 */
public class Party {

    private final int id;
    private final String name;

    /**
     * tracks the candidates of this party
     * Candidates have a unique (full) name within the party.
     * But there may be different Candidates with the same name across different Parties.
     */
    private Set<Candidate> candidates;

    public Party(int id, String name) {
        this.id = id;
        this.name = name;

        // TODO initialise this.candidates with an appropriate Set implementation


    }

    /**
     * Adds a newCandidate to the set of candidates in the party
     * If a candidate with the same name already had been registered in the party earlier,
     * then this duplicate instance shall be retrieved from the set and returned for further use
     * thereby avoiding the memory footprint of continued use of all duplicate instances of candidates
     * as they are imported from XML
     * @param newCandidate
     * @return  the existing duplicate instance of newCandidate if available,
     *              or otherwise the newCandidate itself
     */
    public Candidate addOrGetCandidate(Candidate newCandidate) {

        // associate the new Candidate with this party
        newCandidate.setParty(this);

        // TODO try to add the newCandidate to the set of candidates,
        //  and if that fails then return the existing duplicate instance that is in the set already.




        return null; // replace by a proper outcome
    }

    @Override
    public String toString() {
        return "Party{" +
                "id=" + id +
                ",name='" + name + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Party)) return false;
        Party other = (Party) o;

        // TODO provide the equality criterion to identify unique party instances



        return false; // replace by a proper outcome
    }

    @Override
    public int hashCode() {
        // TODO provide a hashCode that is consistent with above equality criterion



        return 0; // replace by a proper outcome
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Candidate> getCandidates() {
        return candidates;
    }

    public static final String PARTY = "Affiliation";
    public static final String PARTY_IDENTIFIER = "AffiliationIdentifier";
    public static final String ID = "Id";
    private static final String REGISTERED_NAME = "RegisteredName";
    public static final String INVALID_NAME = "INVALID";
    /**
     * Auxiliary method for parsing the data from the EML files
     * This methode can be used as-is and does not require your investigation or extension.
     */
    public static Party importFromXML(XMLParser parser, Constituency constituency, Map<Integer,Party> parties) throws XMLStreamException {
        if (parser.findBeginTag(PARTY)) {
            int id = 0;
            String name = INVALID_NAME;
            if (parser.findBeginTag(PARTY_IDENTIFIER)) {
                id = parser.getIntegerAttributeValue(null, ID, 0);
                if (parser.findBeginTag(REGISTERED_NAME)) {
                    name = parser.getElementText();
                }
                parser.findAndAcceptEndTag(REGISTERED_NAME);
                parser.findAndAcceptEndTag(PARTY_IDENTIFIER);

            }

            // work around effective final constraint of global variables in lambda expression
            final String partyName = name;
            Party party = parties.computeIfAbsent(id, (i) -> new Party(i, partyName));

            parser.findBeginTag(Candidate.CANDIDATE);
            while (parser.getLocalName().equals(Candidate.CANDIDATE)) {
                // parse the candidate entry from the Xml file
                Candidate.importFromXml(parser, constituency, party);
            }

            parser.findAndAcceptEndTag(PARTY);
            return party;
        }
        return new Party(-1, INVALID_NAME);
    }
}

package nl.hva.ict.ads.elections.models;

import nl.hva.ict.ads.utils.PathUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ElectionTest {
    static Election election;

    @BeforeAll
    static void setup() throws IOException, XMLStreamException {
        election = Election.importFromDataFolder(PathUtils.getResourcePath("/EML_bestanden_TK2021_HvA_UvA"));
    }

    @Test
    void allConstituenciesShouldHaveBeenLoaded() {
        assertEquals(2, election.getConstituencies().size(), "Not all constituencies have been found from the XML input");
    }

    @Test
    void allPartiesShouldHaveBeenRegisteredByUniqueId() {
        assertEquals(36, election.getParties().size(), "Not all parties have been found from the XML input");
    }

    @Test
    void constituenciesShouldReferenceSharedInstancesOfParties() {
        for (Constituency c : election.getConstituencies())
            for (Party p: c.getParties()) {
                Party electionParty = election.getParty(p.getId());
                assertSame(electionParty, p,
                        String.format("Party %s in Constituency %s is a duplicate instance of party %s in the election.", p, c, electionParty));
            }
    }

    @Test
    void electionShouldReferenceASingleInstanceOfEachUniqueCandidate() {
        assertEquals(1119, election.getAllCandidates().size(), "Not all candidates have been found, or unique candidates have not been identified properly by party and by name.");
    }

    @Test
    void checkTotalNumberOfPollingStations() {
        int numPollingStations = election.getConstituencies().stream().mapToInt(c -> c.getPollingStations().size()).sum();
        assertEquals(4, numPollingStations);
    }

    @Test
    void candidatesWithDuplicateNamesShouldBeFound() {
        Set<Candidate> candidates = election.getCandidatesWithDuplicateNames();
        assertEquals(6, candidates.size(),
                String.format("Incorrect number of candidates found in %s", candidates));
    }

    @Test
    void getPollingStationsByZipCodeRange() {
        String expected = "[PollingStation{id='0363::SB126/558',zipCode='1091GH',name='Stembureau Hogeschool van Amsterdam, Wibauthuis'}, PollingStation{id='0363::SB159/680',zipCode='1097DZ',name='Stembureau Hogeschool van Amsterdam, Leeuwenburg'}]";
        Collection<PollingStation> pollingStations = election.getPollingStationsByZipCodeRange("1091AA", "1097ZZ");
        assertEquals(2, pollingStations.size(),
                String.format("Incorrect number of polling stations found in %s\nExpected: %s", pollingStations, expected));

    }

    @Test
    void getVotesByPartyAcrossPollingStations() {
        String expected = "{Party{id=1,name='VVD'}=156, Party{id=2,name='PVV (Partij voor de Vrijheid)'}=40, Party{id=3,name='CDA'}=30, Party{id=4,name='D66'}=497, Party{id=5,name='GROENLINKS'}=255, Party{id=6,name='SP (Socialistische Partij)'}=60, Party{id=7,name='Partij van de Arbeid (P.v.d.A.)'}=117, Party{id=8,name='ChristenUnie'}=8, Party{id=9,name='Partij voor de Dieren'}=162, Party{id=10,name='50PLUS'}=4, Party{id=11,name='Staatkundig Gereformeerde Partij (SGP)'}=3, Party{id=12,name='DENK'}=83, Party{id=13,name='Forum voor Democratie'}=33, Party{id=14,name='BIJ1'}=137, Party{id=15,name='JA21'}=19, Party{id=16,name='CODE ORANJE'}=4, Party{id=17,name='Volt'}=197, Party{id=18,name='NIDA'}=29, Party{id=19,name='Piratenpartij'}=7, Party{id=20,name='LP (Libertaire Partij)'}=5, Party{id=21,name='JONG'}=1, Party{id=22,name='Splinter'}=4, Party{id=23,name='BBB'}=1, Party{id=24,name='NLBeter'}=2, Party{id=25,name='Lijst Henk Krol'}=1, Party{id=26,name='OPRECHT'}=0, Party{id=27,name='JEZUS LEEFT'}=1, Party{id=28,name='Trots op Nederland (TROTS)'}=0, Party{id=29,name='U-Buntu Connected Front'}=0, Party{id=31,name='Partij van de Eenheid'}=1, Party{id=33,name='Vrij en Sociaal Nederland'}=0, Party{id=36,name='De Groenen'}=0, Party{id=37,name='Partij voor de Republiek'}=0}";
        Collection<PollingStation> pollingStations = election.getPollingStationsByZipCodeRange("1091AA", "1099ZZ");
        Map<Party,Integer> votesByParty = election.getVotesByPartyAcrossPollingStations(pollingStations);
        assertEquals(1857, Election.integersSum(votesByParty.values()),
                String.format("Incorrect total number of votes found in %s\nExpected: %s", votesByParty, expected));
    }

    private static final int[] numbersOfCandidatesByParty = {
            -1,
            80,  // 1  VVD
            50,  // 2  PVV
            66,  // 3  CDA
            83,  // 4  D66
            50,  // 5  Groenlinks
            50,  // 6  SP
            53,  // 7  PvdA
            50,  // 8  CU
            50,  // 9  PvdD
            40,  // 10 50-Plus
            34,  // 11 SGP
            21,  // 12 Denk
            50,  // 13 FvD
            18,  // 14 BIJ1
            30,  // 15 JA21
            51,  // 16 Code Oranje
            28,  // 17 Volt
            31,  // 18 NIDA
            28,  // 19 Piratenpartij
            31,  // 20 LP
            18,  // 21 Jong
            12,  // 22 Splinter
            26,  // 23 BBB
            14,  // 24 NLBeter
            22,  // 25 Lijst Henk Krol
            17,  // 26 Oprecht
            3,   // 27 Jezus Leeft
            17,  // 28 Trots
            19,  // 29 U-Buntu
            21,  // 30 BLANCO
            10,  // 31 Party van de Eenheid
            1,   // 32 Feestpartij
            10,  // 33 Vrij en Sociaal Nederland
            10,  // 34 Wij zijn NL
            -1,   // 35 Modern NL
            14,  // 36 De Groenen
            11   // 37 Party voor de Republiek
    };

    private final static int CDA_PARTY_ID = 3;
    private static final int[] numbersOfCDARegistrationsByConstituency = {
            -1,
            73,  // 1  Groningen
            71,  // 2  Leeuwarden
            65,  // 3  Assen
            57,  // 4  Zwolle
            70,  // 5  Lelystad
            59,  // 6  Nijmegen
            71,  // 7  Arnhem
            68,  // 8  Utrecht
            56,  // 9  Amsterdam
            61,  // 10 Haarlem
            62,  // 11 Den Helder
            70,  // 12 's-Gravenhage
            76,  // 13 Rotterdam
            80,  // 14 Dordrecht
            80,  // 15 Leiden
            77,  // 16 Middelburg
            70,  // 17 Tilburg
            75,  // 18 's-Hertogenbosch'
            60,  // 19 Maastricht
            53,  // 20 Bonaire
    };

    private static final int[] expectedNumberOfVotesByParty = {
            -1,
            213,  // 1  VVD
            44,  // 2  PVV
            37,  // 3  CDA
            594,  // 4  D66
            304,  // 5  Groenlinks
            69,  // 6  SP
            138,  // 7  PvdA
            8,  // 8  CU
            192,  // 9  PvdD
            5,  // 10 50-Plus
            4,  // 11 SGP
            88,  // 12 Denk
            43,  // 13 FvD
            169,  // 14 BIJ1
            21,  // 15 JA21
            4,  // 16 Code Oranje
            216,  // 17 Volt
            33,  // 18 NIDA
            7,  // 19 Piratenpartij
            5,  // 20 LP
            1,  // 21 Jong
            6,  // 22 Splinter
            1,  // 23 BBB
            3,  // 24 NLBeter
            1,  // 25 Lijst Henk Krol
            0,  // 26 Oprecht
            1,   // 27 Jezus Leeft
            0,  // 28 Trots
            0,  // 29 U-Buntu
            -1,   // 30 BLANCO
            1,  // 31 Party van de Eenheid
            -1,   // 32 Feestpartij
            0,  // 33 Vrij en Sociaal Nederland
            -1,   // 34 Wij zijn NL
            -1,   // 35 Modern NL
            0,  // 36 De Groenen
            0   // 37 Party voor de Republiek
    };

    @ParameterizedTest(name = "Party {0} should have {1} candidates")
    @MethodSource("streamNumbersOfCandidatesByParty")
    void everyPartyShouldHoldCorrectNumberOfCandidates(final Party party, final int numberOfCandidates) {
        assertEquals(numberOfCandidates, party.getCandidates().size(),
                String.format("%s does not register the correct number of candidates in its list. Unique candidates may not have been identified properly.", party));
    }
    private static Stream<Arguments> streamNumbersOfCandidatesByParty() {
        return election.getParties().stream()
                .map(p -> Arguments.of(p, numbersOfCandidatesByParty[p.getId()]));
    }

    @ParameterizedTest(name = "Constituency {0} should have {1} registrations")
    @MethodSource("streamNumbersOfRegistrationsByConstituency")
    void everyConstituencyShouldHoldCorrectNumberOfRegistrations(final Constituency constituency, final int expectedNumberOfRegistrations,
                                                                 Map<Constituency,Integer> numbersOfRegistration) {
        assertEquals(expectedNumberOfRegistrations, numbersOfRegistration.get(constituency),
                String.format("%s does not hold the correct number of registrations in its map. Unique rank or candidates may not have been identified properly.", constituency));
    }
    private static Stream<Arguments> streamNumbersOfRegistrationsByConstituency() {
        Map<Constituency,Integer> numbersOfRegistrationsByConstituency =
                election.numberOfRegistrationsByConstituency(election.getParty(CDA_PARTY_ID));
        return election.getConstituencies().stream()
                .map(c -> Arguments.of(c, numbersOfCDARegistrationsByConstituency[c.getId()], numbersOfRegistrationsByConstituency));
    }

    @ParameterizedTest(name = "Party {0} should have got {1} votes")
    @MethodSource("streamNumbersOfVotesByParty")
    void everyPartyShouldHaveWonCorrectNumberOfVotes(final Party party, final int expectedNumberOfVotes,
                                                     Map<Party,Integer> votesByParty) {

        // skip parties without vote counts by polling stations
        if (expectedNumberOfVotes < 0) return;
        assertEquals(expectedNumberOfVotes, votesByParty.get(party),
                String.format("%s does not win the correct number of votes.", party));
    }
    private static Stream<Arguments> streamNumbersOfVotesByParty() {
        int totalVotes = Arrays.stream(expectedNumberOfVotesByParty).filter(v -> v >= 0).sum();
        final Map<Party,Integer> votesByParty = election.getVotesByParty();
        assertEquals(totalVotes, Election.integersSum(votesByParty.values()),
                "Total number of casted votes does not match expectation");

        return election.getParties().stream()
                .map(p -> Arguments.of(p,expectedNumberOfVotesByParty[p.getId()],votesByParty));
    }

    @Test
    void sortedElectionResultsByPartyPercentageShouldReturnCorrectRanking() {
        int totalVotes = Arrays.stream(expectedNumberOfVotesByParty).filter(v -> v >= 0).sum();
        Map<Party,Integer> electionResults = election.getVotesByParty();

        assertEquals(List.of(Map.entry(election.getParty(4),100.0*expectedNumberOfVotesByParty[4]/totalVotes),
                        Map.entry(election.getParty(5),100.0*expectedNumberOfVotesByParty[5]/totalVotes),
                        Map.entry(election.getParty(17),100.0*expectedNumberOfVotesByParty[17]/totalVotes)
                        ),
                Election.sortedElectionResultsByPartyPercentage(3, electionResults),
                "Overall election result is not properly calculated or ranked");
    }

    @Test
    void mostRepresentativePollingStationShouldBeFound() {
        String expected = "PollingStation{id='0363::SB126',zipCode='1091GH',name='Stembureau Hogeschool van Amsterdam, Wibauthuis'}";
        PollingStation mostRepresentativeStation = election.findMostRepresentativePollingStation();

        assertNotNull(mostRepresentativeStation,
                "The most representative polling station was not found.");
        assertEquals("1091GH", mostRepresentativeStation.getZipCode(),
                String.format("%s is not the most representative; expected: %s", mostRepresentativeStation, expected));
        assertEquals("0363::SB126", mostRepresentativeStation.getId().substring(0,11),
                String.format("%s is not the most representative; expected: %s", mostRepresentativeStation, expected));
    }
}

package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConstituencyTest {

    private final int VOTES_S1 = 11;
    private final int VOTES_S2 = 22;
    private final int VOTES_S3 = 33;
    private final int VOTES_T1 = 1;
    private final int VOTES_T2 = 2;
    private final int VOTES_ST = 3;

    private Constituency constituency;
    private Party studentsParty, teachersParty;
    private Candidate student1, student2, student3a, student3b, teacher1, teacher2;
    private Candidate studentTeacher;
    private List<Candidate> studentCandidates;
    private List<Candidate> teacherCandidates;
    private PollingStation pollingStation1, pollingStation2;

    @BeforeEach
    public void setup() {

        this.constituency = new Constituency(0, "HvA");

        this.studentsParty = new Party(101,"Students Party");
        this.teachersParty = new Party(102,"Teachers Party");

        this.student1 = new Candidate("S.", null, "Leader", this.studentsParty);
        this.student2 = new Candidate("S.", null, "Vice-Leader", this.studentsParty);
        this.student3a = new Candidate("A.", null, "Student", this.studentsParty);
        this.student3b = new Candidate("A.", null, "Student", this.studentsParty);
        this.teacher1 = new Candidate("T.", null, "Leader", this.teachersParty);
        this.teacher2 = new Candidate("T.", null, "Vice-Leader", this.teachersParty);
        this.studentTeacher = new Candidate("A.", null, "Student", this.teachersParty);

        this.studentCandidates = List.of(this.student1, this.student3a);
        this.constituency.register(1, this.student1);
        this.constituency.register(3, this.student3a);
        this.teacherCandidates = List.of(this.teacher1);
        this.constituency.register(1, this.teacher1);

        this.pollingStation1 = new PollingStation("WHB", "1091GH", "Wibauthuis");
        this.pollingStation2 = new PollingStation("LWB", "1097DZ", "Leeuwenburg");
        this.constituency.add(this.pollingStation1);
        this.constituency.add(this.pollingStation2);
        pollingStation1.addVotes(this.student1,VOTES_S1);
        pollingStation1.addVotes(this.student3a,VOTES_S3);
        pollingStation1.addVotes(this.teacher1,VOTES_T1);
        pollingStation1.addVotes(this.studentTeacher,VOTES_ST);
        pollingStation2.addVotes(this.student1,VOTES_S1);
        pollingStation2.addVotes(this.student3b,VOTES_S3);
    }

    @Test
    void getCandidatesShouldReturnAllCandidatesOfAPartyInOrderOfRank() {
        assertEquals(this.teacherCandidates, this.constituency.getCandidates(this.teachersParty),
                "Cannot retrieve the correct ballot list with the correct order of candidates");
        assertEquals(this.studentCandidates, this.constituency.getCandidates(this.studentsParty),
                "Cannot retrieve the correct ballot list with the correct order of candidates");
    }

    @Test
    void getCandidateShouldReturnTheCorrectCandidateAtGivenPartyAndRank() {
        assertEquals(this.teacher1, this.constituency.getCandidate(this.teachersParty,1),
                "Cannot retrieve the correct candidate from the ballot list");
        assertEquals(this.student1, this.constituency.getCandidate(this.studentsParty,1),
                "Cannot retrieve the correct candidate from the ballot list");
        assertEquals(this.student3a, this.constituency.getCandidate(this.studentsParty,3),
                "Cannot retrieve the correct candidate from the ballot list");

        assertNull(this.constituency.getCandidate(this.studentsParty,2),
                "Should not find a candidate with given rank");
        assertTrue(this.constituency.register(2, this.student2),
                "Successful registration should return true");
        assertEquals(this.student2, this.constituency.getCandidate(this.studentsParty,2),
                "Could not find the candidate with given rank");
    }

    @Test
    void registerShouldFailOnDuplicateRegistration() {
        assertFalse(this.constituency.register(1, this.student2),
                "Duplicate registration should fail");
        assertEquals(this.student1, this.constituency.getCandidate(this.studentsParty,1),
                "Original registration was lost after duplicate registration");
        assertFalse(this.constituency.register(4, this.student3a),
                "Duplicate registration should fail");
        assertFalse(this.constituency.register(4, this.student3b),
                "Duplicate registration should fail");
        assertEquals(this.student3a, this.constituency.getCandidate(this.studentsParty,3),
                "Original registration was lost after duplicate registration");
        assertNull(this.constituency.getCandidate(this.studentsParty,4),
                "Duplicate registration still persisted");
    }

    @Test
    void getAllPartiesShouldReturnAllPartiesOfRegisteredCandidates() {
        assertEquals(Set.of(this.studentsParty, this.teachersParty), this.constituency.getParties(),
                "Could not retrieve all parties of registered candidates");
    }

    @Test
    void getAllCandidatesShouldBuildACorrectSetOfCandidates() {
        Set<Candidate> candidates = this.constituency.getAllCandidates();
        assertEquals(3, candidates.size(),
                "Could not retrieve all candidates across all parties");

        assertTrue(this.constituency.register(2, this.studentTeacher),
                "Registration with duplicate name in different party should be allowed");
        candidates = this.constituency.getAllCandidates();
        assertEquals(4, candidates.size(),
                "Registration with duplicate name in different party should be allowed");
    }

    @Test
    void getVotesByPartyShouldReturnCorrectResult() {
        Map<Party,Integer> votesByParty = this.constituency.getVotesByParty();
        assertEquals(Map.of(this.studentsParty, 2*VOTES_S1+2*VOTES_S3, this.teachersParty, VOTES_T1+VOTES_ST), votesByParty,
                "Votes have not been correctly registered or accumulated by party");
    }

    @Test
    void getPollingStationsByZipCodeRangeShouldReturnSome() {
        NavigableSet<PollingStation> pollingStations = this.constituency.getPollingStations();
        assertEquals(Set.of(this.pollingStation1, this.pollingStation2), pollingStations,
                "Could not retrieve or register all polling stations from just the getter.");

        pollingStations = this.constituency.getPollingStationsByZipCodeRange("1091AA", "1099ZZ");
        assertEquals(Set.of(this.pollingStation1, this.pollingStation2), pollingStations,
                "Could not retrieve exactly the polling stations within the zip code range.");

        pollingStations = this.constituency.getPollingStationsByZipCodeRange("1091AA", "1091ZZ");
        assertEquals(Set.of(this.pollingStation1), pollingStations,
                "Could not retrieve exactly the polling stations within the zip code range.");

        pollingStations = this.constituency.getPollingStationsByZipCodeRange("1000AA", "1000ZZ");
        assertEquals(Set.of(), pollingStations,
                "Could not retrieve exactly the polling stations within the zip code range.");

    }
}
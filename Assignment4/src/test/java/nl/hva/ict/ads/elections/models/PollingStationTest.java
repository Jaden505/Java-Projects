package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PollingStationTest {
    private final int VOTES11 = 10;
    private final int VOTES12 = 20;
    private final int VOTES21 = 40;

    private Party party1, party2;
    private Candidate candidate11, candidate12, candidate21, candidate21a;
    private PollingStation hva;

    @BeforeEach
    void setup() {
        this.party1 = new Party(1,"Party-1");
        this.party2 = new Party(2,"Party-2");
        this.candidate11 = new Candidate("A.", null, "Candidate", this.party1);
        this.candidate12 = new Candidate("B.", null, "Candidate", this.party1);
        this.candidate21 = new Candidate("A.", null, "Candidate", this.party2);
        this.candidate21a = new Candidate("A.", null, "Candidate", this.party2);;
        this.hva = new PollingStation("hva", "1091GH", "hva");
        hva.addVotes(candidate11, VOTES11);
        hva.addVotes(candidate12, VOTES12);
        hva.addVotes(candidate21, VOTES21);
    }
    @Test
    void addVotesShallRegisterAndAccumulateAllVotesByCandidate() {
        assertEquals(VOTES21, hva.getVotes(candidate21));
        hva.addVotes(candidate21, VOTES21);
        assertEquals(2 * VOTES21, hva.getVotes(candidate21));
        hva.addVotes(candidate21a, VOTES21);
        assertEquals(3 * VOTES21, hva.getVotes(candidate21),
                "candidate21a actually is the same candidate as candidate21, just another instance");
        assertEquals(3 * VOTES21, hva.getVotes(candidate21a),
                "candidate21a actually is the same candidate as candidate21, just another instance");
    }

    @Test
    void getVotesByPartyShouldAggregateCorrectResults() {
        Map<Party,Integer> votesByParty = hva.getVotesByParty();
        assertEquals(VOTES11+VOTES12, votesByParty.get(party1));
        assertEquals(VOTES21, votesByParty.get(party2));
    }
}
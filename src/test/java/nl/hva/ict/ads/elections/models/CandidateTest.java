package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CandidateTest {

    private Candidate candidate11, candidate12, candidate21, candidate21a;

    @BeforeEach
    void setup() {
        Party party1 = new Party(1, "Party-1");
        Party party2 = new Party(2, "Party-2");
        this.candidate11 = new Candidate("A.", null, "Candidate", party1);
        this.candidate12 = new Candidate("B.", "van", "Candidate", party1);
        this.candidate21 = new Candidate("A.", null, "Candidate", party2);
        this.candidate21a = new Candidate("A.", null, "Candidate", party2);
    }

    @Test
    void candidatesShallShowANicelyFormattedFullName() {
        assertEquals("A. Candidate", this.candidate11.getFullName());
        assertEquals("B. van Candidate", this.candidate12.getFullName());
    }

    @Test
    void equalsShallIdentifyByPartyIdAndCandidateFullName() {
        assertEquals(this.candidate21, this.candidate21a,
                "Candidates are uniquely defined by party-id and candidate-fullname");
        assertNotEquals(this.candidate11, this.candidate12,
                "Candidates are uniquely defined by party-id and candidate-fullname");
        assertNotEquals(this.candidate11, this.candidate21,
                "Candidates are uniquely defined by party-id and candidate-fullname");
    }

    @Test
    void hashCodeShallIdentifyByPartyIdAndCandidateName() {
        assertNotEquals(0, this.candidate11.hashCode(),
                "Did you implement a proper hashCode aligned with the candidate-equality criterion?");
        assertEquals(this.candidate21.hashCode(), this.candidate21a.hashCode(),
                "Candidates are uniquely defined by party-id and candidate-fullname");
    }
}
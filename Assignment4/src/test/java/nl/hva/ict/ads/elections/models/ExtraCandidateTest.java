package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ExtraCandidateTest {

    private Candidate candidate11, candidate12,candidate13,candidate14,candidate15,candidate16, candidate21, candidate21a;

    @BeforeEach
    void setup() {
        Party party1 = new Party(6, "Party-1");
        Party party2 = new Party(8, "Party-2");
        this.candidate11 = new Candidate("A.", null, "Candidate", party1);
        this.candidate12 = new Candidate("B.", "van", "Candidate", party1);
        this.candidate13 = new Candidate("C.", null, "Candidate", party1);
        this.candidate14 = new Candidate("W.", null, "Smith", party1);
        this.candidate15 = new Candidate("E.", null, "Candidate", party1);
        this.candidate16 = new Candidate("F.", "van", "jackson", party1);
        this.candidate21 = new Candidate("A.", null, "Candidate", party2);
        this.candidate21a = new Candidate("A.", null, "Candidate", party2);
    }

    @Test
    void candidatesShallShowANicelyFormattedFullName() {
        assertEquals("A. Candidate", this.candidate11.getFullName());
        assertEquals("B. van Candidate", this.candidate12.getFullName());
        assertEquals("C. Candidate", this.candidate13.getFullName());
        assertEquals("W. Smith", this.candidate14.getFullName());
        assertEquals("E. Candidate", this.candidate15.getFullName());
        assertEquals("F. van jackson", this.candidate16.getFullName());
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


}
package nl.hva.ict.ads.elections.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PartyTest {

    private Party studentsParty;
    private Candidate student1, student2, student3a, student3b;

    @BeforeEach
    public void setup() {

        this.studentsParty = new Party(101,"Students Party");

        this.student1 = new Candidate("A.", null, "Student");
        this.student3a = new Candidate("C.", "D.", "Student");
        this.student3b = new Candidate("C.", "D.", "Student");
    }

    @Test
    void addOrGetCandidate() {
        Candidate student = this.studentsParty.addOrGetCandidate(this.student1);
        assertEquals(1, this.studentsParty.getCandidates().size(),
                String.format("%s should have been added",this.student1));
        assertSame(this.student1, student,
                String.format("%s should have been returned", this.student1));

        student = this.studentsParty.addOrGetCandidate(this.student3a);
        assertEquals(2, this.studentsParty.getCandidates().size(),
                String.format("%s should have been added",this.student3a));
        assertSame(this.student3a, student,
                String.format("%s should have been returned",this.student3a));

        student = this.studentsParty.addOrGetCandidate(this.student3b);
        assertEquals(2, this.studentsParty.getCandidates().size(),
                String.format("%s should not have been added",this.student3b));
        assertSame(this.student3a, student,
                String.format("the original %s should have been returned",this.student3a));
    }
}
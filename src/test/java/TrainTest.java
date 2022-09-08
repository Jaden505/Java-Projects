import models.*;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TrainTest {
    Train passengerTrain, trainWithoutWagons, freightTrain;

    Wagon passengerWagon1, passengerWagon2, passengerWagon3;
    Wagon passengerWagon8001, passengerWagon8002;
    Wagon freightWagon1, freightWagon2;
    Wagon freightWagon9001, freightWagon9002;

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.ENGLISH);
        Locomotive rembrandt = new Locomotive(24531, 8);
        passengerTrain = new Train(rembrandt, "Amsterdam", "Paris");
        Wagon wagon;
        passengerWagon8001 = (Wagon)(Object)new PassengerWagon(8001,32);
        passengerTrain.setFirstWagon(passengerWagon8001);
        passengerWagon8002 = (Wagon)(Object)new PassengerWagon(8002,32);
        passengerWagon8001.attachTail(passengerWagon8002);
        passengerWagon8002.attachTail((Wagon)(Object)new PassengerWagon(8003,18)); wagon = passengerWagon8002.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8004,44)); wagon = wagon.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8005,44)); wagon = wagon.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8006,44)); wagon = wagon.getNextWagon();
        wagon.attachTail((Wagon)(Object)new PassengerWagon(8007,40));

        Locomotive vanGogh = new Locomotive(29123, 7);
        trainWithoutWagons = new Train(vanGogh, "Amsterdam", "London");

        Locomotive clusius = new Locomotive(63427, 50);
        freightTrain = new Train(clusius, "Amsterdam", "Berlin");
        freightWagon9001 = (Wagon)(Object)new FreightWagon(9001,50000);
        freightTrain.setFirstWagon(freightWagon9001);
        freightWagon9002 = (Wagon)(Object)new FreightWagon(9002,40000);
        freightWagon9001.attachTail(freightWagon9002);
        freightWagon9002.attachTail((Wagon)(Object)new FreightWagon(9003,30000));

        passengerWagon1 = (Wagon)(Object)new PassengerWagon(8011,50);
        passengerWagon2 = (Wagon)(Object)new PassengerWagon(8012,50);
        passengerWagon3 = (Wagon)(Object)new PassengerWagon(8013,50);
        passengerWagon1.attachTail(passengerWagon2);
        passengerWagon2.attachTail(passengerWagon3);
        freightWagon1 = (Wagon)(Object)new FreightWagon(9011,60000);
        freightWagon2 = (Wagon)(Object)new FreightWagon(9012,60000);
        freightWagon1.attachTail(freightWagon2);
    }

    @AfterEach
    public void checkRepresentationInvariants() {
        WagonTest.checkRepresentationInvariant(passengerWagon1);
        WagonTest.checkRepresentationInvariant(passengerWagon2);
        WagonTest.checkRepresentationInvariant(passengerWagon3);
        WagonTest.checkRepresentationInvariant(freightWagon1);
        WagonTest.checkRepresentationInvariant(freightWagon2);
        checkRepresentationInvariant(passengerTrain);
        checkRepresentationInvariant(freightTrain);
    }
    public static void checkRepresentationInvariant(Train train) {
        // TODO check all aspects of the representation invariant of a train and its wagons

        assertFalse(train.hasWagons() && train.getFirstWagon().hasPreviousWagon(),
                "The first wagon in a train should not have a previous wagon");
        //  check the representation invariant of each wagon
        for (Wagon wagon = train.getFirstWagon(); wagon != null; wagon = wagon.getNextWagon()) {
            //  check that all wagons are of the same type
            assertTrue(!wagon.hasNextWagon() || wagon.getClass() == wagon.getNextWagon().getClass(),
                    String.format("Wagon %s should be of the same type as its next wagon in the train, if any", wagon));
            WagonTest.checkRepresentationInvariant(wagon);
        }
        //  check that the capacity of the engine has not exceeded
        assertThat("Number of wagons in the train should not exceed engine capacity",
                train.getNumberOfWagons(), lessThanOrEqualTo(train.getEngine().getMaxWagons()));
    }

    @Test
    public void T11_APassengerTrainsIsNoAFreightTrain() {
        passengerTrain = new Train(new Locomotive(13, 13), "Here", "There");
        passengerTrain.setFirstWagon((Wagon)(Object)new PassengerWagon(1313, 1300));
        assertTrue(passengerTrain.isPassengerTrain());
        assertFalse(passengerTrain.isFreightTrain());
    }

    @Test
    public void T11_AFreightTrainIsNotAPassengerTrain() {
        freightTrain = new Train(new Locomotive(13, 13), "Here", "There");
        freightTrain.setFirstWagon((Wagon)(Object)new FreightWagon(1313, 1300));
        assertFalse(freightTrain.isPassengerTrain());
        assertTrue(freightTrain.isFreightTrain());
    }

    @Test
    public void T11_ATrainWithoutWagonsIsNotAPassengerOrAFreightTrain() {
        assertFalse(trainWithoutWagons.isPassengerTrain());
        assertFalse(trainWithoutWagons.isFreightTrain());
    }

    @Test
    public void T12_ATrainWithoutWagonsShouldBeEmpty() {
        assertFalse(trainWithoutWagons.hasWagons());
        assertEquals(0, trainWithoutWagons.getNumberOfWagons());
        assertNull(trainWithoutWagons.getLastWagonAttached());
    }

    @Test
    public void T12_ATrainShouldKnowItsNumberOfWagons() {
        assertTrue(passengerTrain.hasWagons());
        assertEquals(7, passengerTrain.getNumberOfWagons());
        assertEquals(8007, passengerTrain.getLastWagonAttached().getId());
        assertTrue(freightTrain.hasWagons());
        assertEquals(3, freightTrain.getNumberOfWagons());
        assertEquals(9003, freightTrain.getLastWagonAttached().getId());
    }

    @Test
    public void T13_checkCumulativeWagonPropertiesOnTrain() {
        assertEquals( 254, passengerTrain.getTotalNumberOfSeats());
        assertEquals( 0, trainWithoutWagons.getTotalNumberOfSeats());
        assertEquals( 0, freightTrain.getTotalNumberOfSeats());
        assertEquals( 0, passengerTrain.getTotalMaxWeight());
        assertEquals( 0, trainWithoutWagons.getTotalMaxWeight());
        assertEquals( 120000, freightTrain.getTotalMaxWeight());

        // check final wagon
        assertEquals( 40, ((PassengerWagon)(Object)passengerTrain.getLastWagonAttached()).getNumberOfSeats());
        assertEquals( 30000, ((FreightWagon)(Object)freightTrain.getLastWagonAttached()).getMaxWeight());
        //System.out.println(passengerTrain);

        // check toString
        assertTrue(freightTrain.toString().indexOf(" from Amsterdam to Berlin") > 0);
    }

    @Test
    public void T14_findWagonOnTrainAtPosition() {

        // find by position
        assertEquals(8001, passengerTrain.findWagonAtPosition(1).getId(),"First wagon should be found");
        assertEquals(8002, passengerTrain.findWagonAtPosition(2).getId(),"Second wagon should be found");
        assertEquals(8007, passengerTrain.findWagonAtPosition(7).getId(),"Last wagon should be found");
        assertNull(passengerTrain.findWagonAtPosition(8),"No wagon should be found beyond last position");
        assertNull(passengerTrain.findWagonAtPosition(0),"No wagon should be found before first position");
        assertNull(trainWithoutWagons.findWagonAtPosition(1),"No wagon should be found beyond last position");

        // extra tests 28 oct 2021
        assertNull(passengerTrain.findWagonAtPosition(9),"No wagon should be found beyond last position");
        assertNull(passengerTrain.findWagonAtPosition(-1),"No wagon should be found before first position");
        assertNull(trainWithoutWagons.findWagonAtPosition(2),"No wagon should be found beyond last position");
        assertNull(trainWithoutWagons.findWagonAtPosition(0),"No wagon should be found before first position");
        assertNull(trainWithoutWagons.findWagonAtPosition(-1),"No wagon should be found before first position");
    }

    @Test
    public void T15_findWagonOnTrainById() {
        // find by id
        assertEquals(50000, ((FreightWagon)(Object)(freightTrain.findWagonById(9001))).getMaxWeight());
        assertEquals(40000, ((FreightWagon)(Object)(freightTrain.findWagonById(9002))).getMaxWeight());
        assertEquals(30000, ((FreightWagon)(Object)(freightTrain.findWagonById(9003))).getMaxWeight());
        assertNull(freightTrain.findWagonById(9000));
        assertNull(trainWithoutWagons.findWagonById(8000));
    }

    @Test
    public void T16_CantAttachMoreWagonsThanTrainsCapacity() {
        assertFalse(passengerTrain.canAttach(passengerWagon1));
        assertFalse(passengerTrain.canAttach(passengerWagon2));
        assertTrue(passengerTrain.canAttach(passengerWagon3));
        assertTrue(freightTrain.canAttach(freightWagon1));

        assertEquals(7, passengerTrain.getNumberOfWagons());
        assertEquals(3, freightTrain.getNumberOfWagons());
    }

    @Test
    public void T16_CantAttachWrongTypeWagons() {
        assertFalse(freightTrain.canAttach(passengerWagon3));
        assertFalse(passengerTrain.canAttach(freightWagon2));
        assertTrue(trainWithoutWagons.canAttach(passengerWagon3));
        assertTrue(trainWithoutWagons.canAttach(freightWagon2));
        assertTrue(trainWithoutWagons.canAttach(passengerWagon1));
        assertTrue(trainWithoutWagons.canAttach(freightWagon1));
        assertEquals(0, trainWithoutWagons.getNumberOfWagons());
    }

    @Test
    public void T16_CantAttachWagonsAlreadyOnTheTrain() {
        assertFalse(freightTrain.canAttach(freightWagon9001));
        assertFalse(freightTrain.canAttach(freightWagon9002));
        assertFalse(passengerTrain.canAttach(passengerWagon8001));
        assertFalse(passengerTrain.canAttach(passengerWagon8002));
    }

    @Test
    public void T17_CanAttachToRear() {
        assertTrue(trainWithoutWagons.attachToRear(passengerTrain.getLastWagonAttached()),
                "can attach a single wagon to an empty train");
        assertEquals(8007, trainWithoutWagons.getFirstWagon().getId(),
                "attachToRear should disconnect and reattach the given head wagon");

        assertTrue(trainWithoutWagons.attachToRear(passengerTrain.getLastWagonAttached()),
                "can attach a single wagon at the rear of a train");
        assertEquals(8006, trainWithoutWagons.getLastWagonAttached().getId(),
                "attachToRear should disconnect and reattach the given head wagon to the rear");

        assertTrue(trainWithoutWagons.attachToRear(passengerWagon8002),
                "can attach a sequence at at the rear of a train");
        assertEquals(8002, trainWithoutWagons.findWagonAtPosition(3).getId(),
                "attachToRear should disconnect and reattach the head wagon to the rear");
        assertEquals(8003, trainWithoutWagons.findWagonAtPosition(4).getId(),
                "attachToRear should disconnect and reattach the complete sequence to the rear");
        assertEquals(8004, trainWithoutWagons.findWagonAtPosition(5).getId(),
                "attachToRear should disconnect and reattach the complete sequence to the rear");
        assertEquals(8005, trainWithoutWagons.getLastWagonAttached().getId(),
                "attachToRear should disconnect and reattach the complete sequence to the rear");

        assertFalse(trainWithoutWagons.attachToRear(passengerWagon8002),
                "cannot attachToRear a wagon that is already on the train");

        assertEquals(6, trainWithoutWagons.getNumberOfWagons());
        assertEquals(1, passengerTrain.getNumberOfWagons());
    }

    @Test
    public void T18_CanInsertAtFront() {
        assertTrue(freightTrain.insertAtFront(freightWagon1));
        assertEquals(5, freightTrain.getNumberOfWagons());
        assertSame(freightWagon1, freightTrain.getFirstWagon(),
                "insertAtFront should insert the first wagon at position 1");
        assertSame(freightWagon2, freightTrain.findWagonAtPosition(2),
                "insertAtFront should insert the second wagon at position 2");

        assertFalse(passengerTrain.insertAtFront(passengerWagon1),
                "Cannot insert 3 passenger wagons into a train with insufficient capacity");
        assertFalse(passengerTrain.insertAtFront(passengerWagon2),
                "Cannot insert 2 passenger wagons into a train with insufficient capacity");
        assertTrue(passengerTrain.insertAtFront(passengerWagon3),
                "Can insert 1 passenger wagon into a train with one more free space");
        assertEquals(8, passengerTrain.getNumberOfWagons());
        assertEquals(2, passengerWagon1.getSequenceLength(),
                "insertAtFront should detach the wagons from its predecessors");
        assertSame(passengerWagon3, passengerTrain.getFirstWagon(),
                "insertAtFront should insert the wagons at position 1");
    }

    @Test
    public void T18_CanInsertPassengerWagonsToEmptyTrainWithCapacity() {
        // check type compatibility and loc capacity
        assertTrue(trainWithoutWagons.insertAtFront(passengerWagon1));
        assertEquals(3, trainWithoutWagons.getNumberOfWagons());
        assertSame(passengerWagon1, trainWithoutWagons.getFirstWagon(),
                "insertAtFront should insert the wagons at position 1");

        assertFalse(trainWithoutWagons.insertAtFront(passengerWagon1),
                "cannot insert wagons on a train that are already there");
        assertEquals(3, trainWithoutWagons.getNumberOfWagons());
    }

    @Test
    public void T19_CanInsertAtAnyPosition() {
        assertTrue(trainWithoutWagons.insertAtPosition(1, passengerTrain.getLastWagonAttached()),
                "can insert a single wagon at position 1 of empty train");
        assertEquals(8007, trainWithoutWagons.getFirstWagon().getId(),
                "insertAtPosition should disconnect and insert the given head wagon");

        assertTrue(trainWithoutWagons.insertAtPosition(1, passengerTrain.getLastWagonAttached()),
                "can insert a single wagon at position 1 of a train");
        assertEquals(8006, trainWithoutWagons.getFirstWagon().getId(),
                "insertAtPosition should disconnect and insert the given head wagon");

        assertTrue(trainWithoutWagons.insertAtPosition(3, passengerTrain.getLastWagonAttached()),
                "can insert a single wagon immediately after the last wagon in a train");
        assertEquals(8005, trainWithoutWagons.findWagonAtPosition(3).getId(),
                "insertAtPosition should disconnect and insert the given head wagon");

        assertTrue(trainWithoutWagons.insertAtPosition(3, passengerTrain.getLastWagonAttached()),
                "can insert a single wagon immediately before the last wagon in a train");
        assertEquals(8004, trainWithoutWagons.findWagonAtPosition(3).getId(),
                "insertAtPosition should disconnect and insert the given head wagon");

        assertTrue(trainWithoutWagons.insertAtPosition(3, passengerWagon8002),
                "can insert a sequence at a mid position in a train");
        assertEquals(8002, trainWithoutWagons.findWagonAtPosition(3).getId(),
                "insertAtPosition should disconnect and insert the given head wagon");
        assertEquals(8003, trainWithoutWagons.findWagonAtPosition(4).getId(),
                "insertAtPosition should disconnect and insert the given head wagon and its successors");
        assertEquals(8004, trainWithoutWagons.findWagonAtPosition(5).getId(),
                "insertAtPosition should disconnect and insert the given head wagon and its successors and reattach the previous wagon at given position");

        assertFalse(trainWithoutWagons.insertAtPosition(2, passengerWagon8002),
                "cannot insert a wagon that is already on the train");

        assertEquals(6, trainWithoutWagons.getNumberOfWagons());
        assertEquals(1, passengerTrain.getNumberOfWagons());
    }

    @Test
    public void T20_ShouldSplitTrainCorrectly() {
        assertTrue(passengerTrain.splitAtPosition(5, trainWithoutWagons));
        assertEquals(3, trainWithoutWagons.getNumberOfWagons());
        assertEquals(4, passengerTrain.getNumberOfWagons());

        assertTrue(passengerTrain.splitAtPosition(4, trainWithoutWagons),
                "can split the last wagon of a train");
        assertEquals(4, trainWithoutWagons.getNumberOfWagons());
        assertEquals(3, passengerTrain.getNumberOfWagons());

        assertFalse(passengerTrain.splitAtPosition(4, trainWithoutWagons),
                "cannot split from beyond the last wagon of a train");
        assertFalse(passengerTrain.splitAtPosition(3, freightTrain),
                "cannot split towards an incompatible train");

        assertTrue(passengerTrain.splitAtPosition(1, trainWithoutWagons),
                "can split at the first wagon of a train");
        assertEquals(7, trainWithoutWagons.getNumberOfWagons());
        assertEquals(0, passengerTrain.getNumberOfWagons());

        assertFalse(passengerTrain.splitAtPosition(1, trainWithoutWagons),
                "cannot split from an empty train");
    }

    @Test
    public void T20_CantSplitAnEmptyTrain() {
        assertFalse(trainWithoutWagons.splitAtPosition(1, passengerTrain));
    }

    @Test
    public void T20_CantSplitTowardsAFullTrain() {
        assertTrue(trainWithoutWagons.attachToRear(passengerWagon1));
        assertFalse(trainWithoutWagons.splitAtPosition(1, passengerTrain),
                "cannot split 3 wagons towards an almost full passenger train");
        assertFalse(trainWithoutWagons.splitAtPosition(2, passengerTrain),
                "cannot split 2 wagons towards an almost full passenger train");
        assertEquals(3, trainWithoutWagons.getNumberOfWagons());
        assertEquals(7, passengerTrain.getNumberOfWagons());
        assertTrue(trainWithoutWagons.splitAtPosition(3, passengerTrain),
                "can split 1 wagon towards a passenger train with one more capacity");
        assertEquals(2, trainWithoutWagons.getNumberOfWagons());
        assertEquals(8, passengerTrain.getNumberOfWagons());
        assertSame(passengerWagon3, passengerTrain.getLastWagonAttached(),
                "can split the last wagon of one train towards the last available position of another train");
    }

    @Test
    public void T21_ShouldMoveOneWagonCorrectly() {
        assertTrue(passengerTrain.moveOneWagon(8002, trainWithoutWagons),
                "can move one wagon from mid-sequence to an empty train");
        assertEquals(1, trainWithoutWagons.getNumberOfWagons(),
                "one single wagon has been disconnected from its sequence and moved thereafter");
        assertEquals(6, passengerTrain.getNumberOfWagons(),
                "one single wagon has been disconnected from its sequence and moved thereafter");
        assertSame(passengerWagon8002, trainWithoutWagons.getFirstWagon());

        assertTrue(passengerTrain.moveOneWagon(8001, trainWithoutWagons),
                "can move the first wagon to another train");
        assertEquals(2, trainWithoutWagons.getNumberOfWagons(),
                "one single wagon has been disconnected from its sequence and moved thereafter");
        assertEquals(5, passengerTrain.getNumberOfWagons(),
                "one single wagon has been disconnected from its sequence and moved thereafter");
        assertEquals(8003, passengerTrain.getFirstWagon().getId(),
                "the first two wagons of the train have both been moved");

        assertTrue(passengerTrain.moveOneWagon(8007, trainWithoutWagons),
                "can move the last wagon to another train");
        assertEquals(3, trainWithoutWagons.getNumberOfWagons(),
                "one single wagon has been disconnected from its sequence and moved thereafter");
        assertEquals(4, passengerTrain.getNumberOfWagons(),
                "one single wagon has been disconnected from its sequence and moved thereafter");

        assertFalse(passengerTrain.moveOneWagon(8006, freightTrain),
                "should not move a wagon to an incompatible train");
        assertEquals(4, passengerTrain.getNumberOfWagons());
        assertEquals(3, freightTrain.getNumberOfWagons());
    }

    @Test
    public void T22_checkReverseTrain() {

        // check type compatibility and loc capacity
        passengerTrain.reverse();
        assertEquals(7, passengerTrain.getNumberOfWagons());
        assertEquals(8007, passengerTrain.findWagonAtPosition(1).getId());
        assertEquals(8006, passengerTrain.findWagonAtPosition(2).getId());
        assertEquals(8005, passengerTrain.findWagonAtPosition(3).getId());
        assertEquals(8001, passengerTrain.findWagonAtPosition(7).getId());

        trainWithoutWagons.reverse();
        assertEquals(0, trainWithoutWagons.getNumberOfWagons());
    }
}

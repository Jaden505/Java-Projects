import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ExtraTrainTest {
    Train passengerTrain;
    Train sameDestinationTrain;

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.ENGLISH);
        Locomotive rembrandt = new Locomotive(24531, 8);
        passengerTrain = new Train(rembrandt, "Amsterdam", "Paris");
        sameDestinationTrain = new Train(rembrandt, "Paris", "Paris");
    }

    @Test
    public void T1_ADifferentOriginDestination() {
        assertSame(sameDestinationTrain.origin, sameDestinationTrain.destination);
        assertNotSame(passengerTrain.origin, passengerTrain.destination);
    }

    @Test
    public void T2_CheckEngineExists() {
        assertNotNull(passengerTrain.getEngine());
    }
}

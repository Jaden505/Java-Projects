package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class Train implements Iterable<Wagon>{
    public final String origin;
    public final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /* three helper methods that are usefull in other methods */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    public boolean isPassengerTrain() {
        if (this.hasWagons()) {
            return this.firstWagon instanceof PassengerWagon;
        }
        return false;
    }

    public boolean isFreightTrain() {
        if (this.hasWagons()) {
            return this.firstWagon instanceof FreightWagon;
        }
        return false;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     * @param wagon the first wagon of a sequence of wagons to be attached
     *              (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        if (wagon != null) {
            this.firstWagon = wagon;
        }
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        int count_wagons = 0;

        for (Wagon skip : this){
            count_wagons ++;
        }

        return count_wagons;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {

        if (this.firstWagon != null) {
            return this.firstWagon.getLastWagonAttached();
        }
        return null;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {

        int totalnumberofseats = 0;
        Wagon wagon = firstWagon;
        while(wagon != null) {
            if(isPassengerTrain()) {
                totalnumberofseats = totalnumberofseats+ ((PassengerWagon) wagon).getNumberOfSeats();
            }
            wagon = wagon.getNextWagon();
        }
        return totalnumberofseats;
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {

        int totalWeight = 0;
        if (isFreightTrain()) {
            FreightWagon temp = (FreightWagon) this.firstWagon;

            while (temp.hasNextWagon()) {
                int weight = temp.getMaxWeight();
                totalWeight += weight;
                temp = (FreightWagon) temp.getNextWagon();
            }
            totalWeight += temp.getMaxWeight();
        }
        return totalWeight;

    }

     /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        Wagon temp = this.firstWagon;

        if (!(this.getNumberOfWagons() >= position)) {
            return null;
        } else {
            if (this.firstWagon == null) {
                return null;
            } else {
                for (int i = 1; i <= position; i++) {
                    if (i == position) {
                        return temp;
                    } else {
                        temp = temp.getNextWagon();
                    }
                }
            }
        }
        return null;

    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon temp = this.firstWagon;
        if (temp != null) {
            if (temp.getId() == wagonId) {
                return temp;
            } else
                while (temp.hasNextWagon()) {
                    temp = temp.getNextWagon();
                    if (temp.getId() == wagonId) {
                        return temp;
                    }
                }
        }
        return null;

    }

    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {



        int total = 0;
        int cap = this.engine.getMaxWagons();
        int totaalTrein;
        int totaalWagon;

        totaalWagon = wagon.getSequenceLength();

        totaalTrein = this.getNumberOfWagons();


        total += (totaalWagon + totaalTrein);

        if (total <= cap) {

            if (!this.hasWagons()) {
                return true;
            } else if (this.hasWagons()) {
                return wagon instanceof PassengerWagon && this.isPassengerTrain()
                        || wagon instanceof FreightWagon && this.isFreightTrain();
            } else {
                return false;
            }
        }
        return false;

    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
//        // check if the new sequence can be attached to the train
//        if (this.canAttach(wagon)) {
//            // check if the train has wagons
//            if (this.firstWagon == null) {
//                this.firstWagon = wagon;
//            } else {
//                // detach from predesessor first, before attach sequence to the last wagon of this train
//                wagon.detachFromPrevious();
//                wagon.attachTo(this.firstWagon.getLastWagonAttached());
//            }
//            return true;
//        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        if (this.firstWagon == wagon) {
            return false;
        } else {
            // check if this train has wagons
            if (!this.hasWagons()) {
                if (canAttach(wagon)) {
                    this.setFirstWagon(wagon);
                    return true;
                }
            } else {
                if (canAttach(wagon)) {
                    Wagon mid = this.firstWagon; // get the firstwagon of this train
                    // set this firstwagon of this train to the rear of the given sequence
                    wagon.getLastWagonAttached().setNextWagon(mid);
                    mid.setPreviousWagon(wagon);
                    // replace this first sequence with the whole new sequence
                    this.firstWagon = wagon;

                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     *    after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     *    or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     * @param position the position where the head wagon and its successors shall be inserted
     *                 1 <= position <= numWagons + 1
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {

        if (canAttach(wagon)) {
            // if this train has no wagons, but the position to insert is one, set the new sequence as the fistwagon
            if (!this.hasWagons() && position == 1) {
                this.firstWagon = wagon;
                return true;

            } else if (this.hasWagons() && position == 1) {
                Wagon mid = this.firstWagon;
                // set this firstwagon to the rear of the given sequence
                wagon.getLastWagonAttached().setNextWagon(mid);
                mid.setPreviousWagon(wagon);
                this.firstWagon = wagon;

                return true;
            } else if (this.hasWagons() && position != 1) {
                // find the wagon at the given position and detaching
                Wagon mid = this.findWagonAtPosition(position);

                // get the previouswagon to attach the given sequence to the rear of that
                Wagon prev = mid.getPreviousWagon();
                mid.detachFromPrevious();
                prev.setNextWagon(wagon);

                // attach the end to the other half
                wagon.getLastWagonAttached().setNextWagon(mid);
                mid.setPreviousWagon(wagon);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId   the id of the wagon to be removed
     * @param toTrain   the train to which the wagon shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        Wagon current = this.firstWagon;

        // check if there is a wagon with wagonId
        if (findWagonById(wagonId) == null) {
            return false;
        }

        // check if the firstwagon is the same as wagonId
        if (this.firstWagon.getId() == wagonId) {
            // remove the first one and move the sequence one to the left
            setFirstWagon(this.firstWagon.getNextWagon());
            current.setNextWagon(null); // detachtail
            toTrain.attachToRear(current); // place the first wagon the the rear of the given train
            return true;
        } else {
            // loop through this sequence to find the wagon with the given wagonId, if it is found stop to the
            // same as above and stop the loop
            Wagon previous;
            for (int i = 0; i < getNumberOfWagons(); i++) {
                previous = current;
                current = current.getNextWagon();
                if (current.getId() == wagonId) {
                    previous.setNextWagon(current.getNextWagon());
                    current.setPreviousWagon(null);
                    current.setNextWagon(null);
                    toTrain.attachToRear(current);
                    break;
                }
            }
        }
        return true;
     }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position  1 <= position <= numWagons
     * @param toTrain   the train to which the split sequence shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        if (this.getNumberOfWagons() >= position) {
            Wagon mid = this.findWagonAtPosition(position);

            if (toTrain.canAttach(mid)) {
                // detach to split the sequence
                mid.detachFromPrevious();
                // check if the given train already has wagons
                if (toTrain.firstWagon != null) {
                    toTrain.getLastWagonAttached().setNextWagon(mid);
                } else {
                    toTrain.firstWagon = mid;
                }
                return true;
            }
        }
        return false;

    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */

    public void reverse() {
        if (this.firstWagon != null) {
            this.firstWagon = this.firstWagon.reverseSequence();
        }

    }

    @Override
    public Iterator<Wagon> iterator() {
        return new Iterator<>() {

            Wagon temp = firstWagon;

            @Override
            public boolean hasNext() {
                return temp != null;
            } // return temp as long it is not null

            @Override
            public Wagon next() {

                Wagon wagon = temp; // set the nextwagon to wagon
                temp = temp.getNextWagon(); // get the nextwagon
                return wagon; // return the wagon
            }
        };
    }


}

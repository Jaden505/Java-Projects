package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class Train{
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
        if (firstWagon == null) {return 0;}

        return firstWagon.getSequenceLength();
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
        int total_seats = 0;
        Wagon wagon = firstWagon;

        while(wagon != null) {
            if(isPassengerTrain()) {
                total_seats += ((PassengerWagon) wagon).getNumberOfSeats();
            }

            wagon = wagon.getNextWagon();
        }
        return total_seats;
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
        Wagon wagon = firstWagon;

        while (wagon != null) {
            if (wagon.getId() == wagonId) return wagon;

            wagon = wagon.getNextWagon();
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
        boolean wagon_type = firstWagon == null || wagon.getClass() == firstWagon.getClass();

        boolean wagon_capicity = engine.getMaxWagons() >= this.getNumberOfWagons() + wagon.getSequenceLength();

        boolean dup_wagon = this.findWagonById(wagon.getId()) == null;

        // Returns if all above booleans are true
        return wagon_type && wagon_capicity && dup_wagon;
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
        if (this.canAttach(wagon)) {
            wagon.detachFromPrevious();

            if (firstWagon == null) {
                this.setFirstWagon(wagon);
            }
            else {
                wagon.attachTo(this.getLastWagonAttached());
            }

            return true;
        }
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
        if(canAttach(wagon)) {
            wagon.detachFront();

            Wagon prev_first_wagon = firstWagon;

            this.setFirstWagon(wagon);

            // Reattaches previous front wagon to current front wagon tail if exists
            if (prev_first_wagon != null) {
                prev_first_wagon.reAttachTo(wagon.getLastWagonAttached());
            }

            return true;
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
            Wagon now;
            Wagon front;

            wagon.detachFront();

            if (!this.hasWagons() || position == 1) {
                now = firstWagon;
                setFirstWagon(wagon);
                if (now != null) {
                    wagon.attachTail(now);
                }
                return true;
            }
            now = findWagonAtPosition(position);

            if (now == null){
                this.getLastWagonAttached().attachTail(wagon);
                return true;
            }

            if (now.hasPreviousWagon()){
                front = now.getPreviousWagon();
                now.detachFront();
                front.attachTail(wagon);
                if (wagon.hasNextWagon()){
                    wagon.getLastWagonAttached().attachTail(now);
                }else {
                    return true;
                }
            }

            return false;
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

        if (findWagonById(wagonId) == null) {
            return false;
        }

        if (this.firstWagon.getId() == wagonId) {
            this.firstWagon = this.firstWagon.getNextWagon();
            current.setNextWagon(null);
            toTrain.attachToRear(current);
            return true;
        } else {

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
            Wagon middle = this.findWagonAtPosition(position);

            if (toTrain.canAttach(middle)) {
                middle.detachFromPrevious();

                if (position == 1) {this.firstWagon = null;}

                // Attach at position
                if (toTrain.hasWagons()) {
                     toTrain.attachToRear(middle);
                } else {
                    toTrain.setFirstWagon(middle);
                }

                return true;
            }
        }

        return false;
    }

    public void reverse() {
        if (this.firstWagon != null) {
            this.firstWagon = this.firstWagon.reverseSequence();
        }
    }

    /**
     * Return string as example:
     * [Loc-24531][Wagon-8003][Wagon-8002][Wagon-8001][Wagon-8004] with 4 wagons from Amsterdam to Paris
     * @return
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(engine.toString());
        Wagon wagon = firstWagon;

        // Appends every wagon id to string
        while (wagon.hasNextWagon()) {
            result.append(wagon);
            wagon = wagon.getNextWagon();
        }

        result.append(" with ").append(firstWagon.getSequenceLength()).append(" wagons from ");
        result.append(this.origin).append(" to ").append(this.destination);

        return result.toString();
    }
}

package models;

public abstract class Wagon {
    public int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
                                    // a.k.a. the successor of this wagon in a sequence
                                    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
                                    // a.k.a. the predecessor of this wagon in a sequence
                                    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon (int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    public void setPreviousWagon(Wagon previousWagon) {
        this.previousWagon = previousWagon;
    }

    public void setNextWagon(Wagon nextWagon) {
        this.nextWagon = nextWagon;
    }

    /**
     * @return  whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return  whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     * @return  the last wagon
     */
    public Wagon getLastWagonAttached() {
        Wagon wagon = this;

        while (wagon.hasNextWagon()) {
            wagon = wagon.getNextWagon();
        }

        return wagon;
    }

    /**
     * @return  the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        Wagon wagon = this;
        int count = 1;

        while (wagon.hasNextWagon()) {
            wagon = wagon.getNextWagon();
            count += 1;
        }

        return count;
    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *          The exception should include a message that reports the conflicting connection,
     *          e.g.: "%s is already pulling %s"
     *          or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {
        if (hasNextWagon()) {
            throw new IllegalStateException(this + " is already pulling " + getNextWagon());
        }

        else if (tail.hasPreviousWagon()) {
            throw new IllegalStateException(tail + " has already been attached to " + tail.getPreviousWagon());
        }

        else {
            this.nextWagon = tail;
            tail.setPreviousWagon(this);
        }
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     * @return the first wagon of the tail that has been detached
     *          or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        if (!this.hasNextWagon()) {return null;}

        Wagon tail_wagon = nextWagon; // Temporary variable

        this.nextWagon.setPreviousWagon(null);
        this.nextWagon = null;

        return tail_wagon;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     * @return  the former previousWagon that has been detached from,
     *          or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        if (!this.hasPreviousWagon()) {return null;}

        Wagon predecessor_wagon = previousWagon; // Temporary variable

        this.previousWagon.setNextWagon(null);
        this.previousWagon = null;

        return predecessor_wagon;
    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        this.detachFront();
        front.detachTail();

        front.attachTail(this);
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void detachFromPrevious() {
        if (this.hasPreviousWagon()) {
            // detaching of both sides
            this.previousWagon.nextWagon = null;
            this.previousWagon = null;
        }
    }

    public void removeFromSequence() {
        Wagon next = this.nextWagon;
        Wagon previous = this.previousWagon;


        if (this.hasPreviousWagon()) {
            this.detachFromPrevious();
        }

        if (this.hasNextWagon()) {
            this.detachTail();
        }

        if (previous != null && next != null) {
            next.reAttachTo(previous);
        }
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // TODO provide an iterative implementation,
        //   using attach- and detach methods of this class

        return null;
    }

    @Override
    public String toString() {
        return "[Wagon-" + id + "]";
    }
}

package models;
// TODO
public class FreightWagon extends Wagon {
    int maxWeight;
    public FreightWagon(int wagonId, int maxWeight) {
        super(wagonId);

        this.maxWeight = maxWeight;
    }

    public int getMaxWeight() {
        return this.maxWeight;
    }
}

package models;

import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface OrderedList<E> extends List<E> {
    Comparator<? super E> getOrdening();
    void sort();
    int indexOfByBinarySearch(E searchItem);
    boolean merge(E item, BinaryOperator<E> merger);
    double aggregate(Function<E,Double> mapper);
}

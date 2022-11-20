package spotifycharts;

import java.util.Comparator;
import java.util.List;

public interface Sorter<E> {
    /**
     * Sorts in place the list of items of type E
     * according to the relative ordening as specified by the given comparator
     * using a selection sort, insertion sort or bubblesort algorithm at discretion of the developer of the implementation
     * @param items
     * @param comparator
     * @return  the same list items, but now sorted according to specifications.
     */
    List<E> selInsBubSort(List<E> items, Comparator<E> comparator);

    /**
     * Sorts in place the list of items of type E
     * according to the relative ordening as specified by the given comparator
     * using a quicksort algorithm
     * @param items
     * @param comparator
     * @return  the same list items, but now sorted according to specifications.
     */
    List<E> quickSort(List<E> items, Comparator<E> comparator);

    /**
     * Partially sorts in place the list of items of type E
     * according to the relative ordening as specified by the given comparator
     * using a heapsort algorithm
     * only the first numTops items in the result are fully sorted
     * the remaining items in the list can be in any order (but all succeed the first numTops items)
     * @param items
     * @param comparator
     * @return  the same list items, but now sorted according to specifications.
     */
    default List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {
        // Shall be overriden in the implementation to gain better efficiency of the heapsort.
        return quickSort(items, comparator);
    }
}

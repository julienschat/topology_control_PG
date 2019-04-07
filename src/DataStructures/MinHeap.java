package DataStructures;

/**
 * Implementation of a Min-Heap.
 */
public class MinHeap {
    public int size;
    private int currentSize;
    private HeapableObject[] heapArray;

    public MinHeap(int size) {
        this.size = size;
        this.currentSize = 0;
        heapArray = new HeapableObject[size];

    }

    public HeapableObject extractMin() {
        if (currentSize > 0) {
            HeapableObject min = heapArray[0];
            currentSize--;
            if (currentSize > 0) {
                HeapableObject newMin = heapArray[currentSize];

                //Not necessary:
                heapArray[currentSize] = null;

                heapArray[0] = newMin;

                int index = 0;

                while (((index + 1) * 2 - 1 < currentSize && heapArray[(index + 1) * 2 - 1].isSmallerThan(heapArray[index]))
                        || ((index + 1) * 2 < currentSize && heapArray[(index + 1) * 2].isSmallerThan(heapArray[index]))) {
                    if (((index + 1) * 2 - 1 < currentSize && heapArray[(index + 1) * 2 - 1].isSmallerThan(heapArray[index]))) {

                        index = (index + 1) * 2 - 1;
                    }
                    if (((index + 1) * 2 < currentSize && heapArray[(index + 1) * 2].isSmallerThan(heapArray[index]))) {

                        index = (index + 1) * 2;
                    }
                    heapArray[index].index = (index - 1) / 2;
                    newMin.index = index;
                    heapArray[(index - 1) / 2] = heapArray[index];
                    heapArray[index] = newMin;
                }
            }
            return min;
        } else {
            //Exception handling;
        }
        return null;
    }

    public void insert(HeapableObject o) {
        if (currentSize < size) {
            int index = currentSize;

            heapArray[index] = o;
            currentSize++;

            while (index != 0 && heapArray[(index - 1) / 2].key > o.key) {
                HeapableObject tmp = heapArray[(index - 1) / 2];
                heapArray[(index - 1) / 2] = o;
                heapArray[index] = tmp;
                heapArray[index].index = index;
                index = (index - 1) / 2;
            }
            o.index = index;
        }

    }

    public void decreaseKey(HeapableObject o, double key) {
        int index = o.index;
        if (index < currentSize && heapArray[index] == o) {
            if (key < o.key) {

                heapArray[index].key = key;
                while (index != 0 && heapArray[(index - 1) / 2].key > key) {
                    HeapableObject tmp = heapArray[(index - 1) / 2];
                    heapArray[(index - 1) / 2] = o;
                    heapArray[index] = tmp;
                    heapArray[index].index = index;
                    index = (index - 1) / 2;
                }
                o.index = index;
            } else {
                // Exception handling
            }
        } else {
            //Exception handling
        }

    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

}

package DataStructures;

/**
 * Abstract base class for Objects that need to be heapable.
 */
public abstract class HeapableObject {
    public int index;
    public double key;
    //public abstract boolean isSmallerThan(HeapableObject o);

    public boolean isSmallerThan(HeapableObject o) {
        if (key < o.key) {
            return true;
        } else {
            return false;
        }
    }

}

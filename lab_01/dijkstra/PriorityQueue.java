import java.util.ArrayList;

public class PriorityQueue {
    private ArrayList<PQItem> queue;   

    public PriorityQueue() {
        queue = new ArrayList<PQItem>();
        queue.add(null);
    }
    
    public void add(PQItem item) {
        queue.add(item);
        shiftUp(queue.size()-1);
    }

    public PQItem remove() {
        var last = queue.remove(queue.size()-1);
        if (queue.size() == 1) return last;
        var peak = queue.set(1, last);
        this.shiftDown(1);
        return peak;                
    }

    private int parent(int i) { return i/2; }

    private int leftChild(int i) { return 2*i; }

    private int rightChild(int i) { return 2*i+1; }

    private void shiftDown(int i) {
        int min = i;
        int left = leftChild(min);
        int right = rightChild(min);
        
        if (isNode(left) && isDistMinorThan(left, min)) min = left;
        if (isNode(right) && isDistMinorThan(right, min)) min = right;
        if (min != i) {
            swap(min, i);
            shiftDown(min);            
        }
    }

    private void shiftUp(int child) {
        int parent = parent(child);
        if (parent > 0 && isDistMinorThan(child, parent)) {
            swap(child, parent);
            shiftUp(parent);
        }      
    }

    private void swap(int i, int j) {
        var aux = queue.get(i);
        queue.set(i, queue.get(j));
        queue.set(j, aux);
    }

    private boolean isNode(int i) { return i < queue.size(); }

    private boolean isDistMinorThan(int i, int j) {
        return queue.get(i).getPriority() < queue.get(j).getPriority();
    }

    public boolean isEmpty() {
        return queue.size() == 1;
    }
}

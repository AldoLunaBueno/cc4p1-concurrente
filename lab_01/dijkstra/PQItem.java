

public class PQItem {
    private Node node;
    private double priority; // Esta es la distancia dinámica

    public PQItem() {
        this.node = new Node("Undefined");
        this.priority = Double.POSITIVE_INFINITY;
    }

    public PQItem(String name) {
        this.node = new Node(name);
        this.priority = Double.POSITIVE_INFINITY;
    }

    public PQItem(Node node) {
        this.node = node;
        this.priority = Double.POSITIVE_INFINITY;
    }

    public PQItem(Node node, double priority) {
        this.node = node;
        this.priority = priority;
    }

    public Node getNode() {
        return node;
    }

    public double getPriority() {
        return priority;
    }

    public PQItem setPriority(double p) {
        priority = p;
        return this;
    }
}

public class Edge {
    Node node;
    double weight;

    public Edge(Node node, double weight) {
        this.node = node;
        this.weight = weight;
    }

    public Node nextNode() {
        return node;
    }

    public Double getWeight() {
        return weight;
    }
}

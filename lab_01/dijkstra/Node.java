import java.util.ArrayList;


public class Node {
    private String name;
    private ArrayList<Edge> edges;

    public Node(String name, ArrayList<Edge> edges) {
        this.name = name;
        this.edges  = edges;

    }

    public Node(String name) {
        this.name = name;
        this.edges  = new ArrayList<Edge>();

    }

    public Node() {
        this.name = "Undefined";
        this.edges  = new ArrayList<Edge>();
    }

    public void addDestination(Node destination, double weight) {
        this.edges.add(new Edge(destination, weight));
    }

    public String getName() {
        return name;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}

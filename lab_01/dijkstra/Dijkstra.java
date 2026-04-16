import java.util.Map;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.HashSet;

public class Dijkstra {
    private Map<Node, Node> predecessors;
    private Map<Node, Double> distances;

    public void execute(Node startNode) {
        distances = new HashMap<>();
        predecessors = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue pq = new PriorityQueue();

        distances.put(startNode, 0.0);
        pq.add(new PQItem(startNode, 0.0));

        while (!pq.isEmpty()) {
            var u = pq.remove().getNode();
            if (visited.contains(u)) continue; // inserción perezosa 2/2
            visited.add(u);
            u.getEdges().forEach(e -> {                
                var v = e.nextNode();
                if (visited.contains(v)) return;
                if (!distances.containsKey(v)) distances.put(v, Double.POSITIVE_INFINITY);
                if (distances.get(u) + e.getWeight() < distances.get(v)) {
                    double d = distances.get(u)+e.getWeight();
                    distances.put(v, d);
                    predecessors.put(v, u);
                    // actualizar distancia d asociada al nodo v en el pq
                    // aplicando inserción perezosa 1/2 (simplemente uno nuevo)
                    pq.add(new PQItem(v, d));
                }
            });
        }
    }

    public Map<Node, Double> getDistances() {
        return distances;
    }

    public Map<Node, Node> getPredecessors() {
        return predecessors;
    }

    public void printDistances() {
        Map<Node, Double> sortedMap = new TreeMap<>(Comparator.comparing(Node::getName));
        sortedMap.putAll(distances);
        sortedMap.keySet().forEach(n -> {
            System.out.println(n.getName() + "=" + distances.get(n));
        });
    }
}

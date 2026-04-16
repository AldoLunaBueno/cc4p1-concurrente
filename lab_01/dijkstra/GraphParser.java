import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class GraphParser {
    public static Map<Integer, Node> loadFromDIMACS(String filepath) throws Exception {
        Map<Integer, Node> nodes = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals("a")) { // Es una arista
                    int fromId = Integer.parseInt(parts[1]);
                    int toId = Integer.parseInt(parts[2]);
                    double weight = Double.parseDouble(parts[3]);

                    // Crea los nodos si no existen (computeIfAbsent es magia pura)
                    Node fromNode = nodes.computeIfAbsent(fromId, id -> new Node(String.valueOf(id)));
                    Node toNode = nodes.computeIfAbsent(toId, id -> new Node(String.valueOf(id)));

                    fromNode.addDestination(toNode, weight);
                }
            }
        }
        return nodes;
    }
}
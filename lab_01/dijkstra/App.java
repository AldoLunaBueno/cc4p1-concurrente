import java.util.Map;

public class App {
    public static void main(String[] args) {
        try {
            // 1. Cargamos el grafo y GUARDAMOS el resultado
            Map<Integer, Node> grafo = GraphParser.loadFromDIMACS("data/graph.gr");
            
            // 2. Extraemos el nodo de inicio (El ID 1 equivale a tu antiguo nodo 'A')
            Node nodoInicial = grafo.get(1);
            
            if (nodoInicial == null) {
                System.out.println("No se encontró el nodo de inicio.");
                return;
            }

            // 3. Ejecutamos el algoritmo
            Dijkstra dijkstra = new Dijkstra();        
            dijkstra.execute(nodoInicial);
            
            System.out.println("Distancias desde el nodo " + nodoInicial.getName() + ":");
            dijkstra.printDistances();
            
        } catch (Exception e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        }
    }    
}
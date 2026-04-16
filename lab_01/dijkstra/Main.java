import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            // 1. Configuración general
            String graphPath = "data/graph.gr";
            String reportPath = "output/benchmark.html";
            int numSourcesToTest = 500; // Tamaño del grafo
            int iterationsPerTest = 3;  // Cantidad de veces a correr cada prueba para promediar
            
            // Los saltos de hilos a evaluar
            int[] threadCountsToTest = {1, 2, 4, 8, 16};

            // 2. Carga de datos
            System.out.println("Cargando estructura del grafo...");
            Map<Integer, Node> graph = GraphParser.loadFromDIMACS(graphPath);

            // 3. Cálculo de aristas totales
            int totalNodes = graph.size();
            int totalEdges = 0;
            for (Node n : graph.values()) {
                totalEdges += n.getEdges().size(); 
            }
            System.out.println("Grafo cargado: " + totalNodes + " nodos y " + totalEdges + " aristas.");
            
            List<Node> sources = new ArrayList<>();
            int count = 0;
            for (Node n : graph.values()) {
                if (count++ >= numSourcesToTest) break;
                sources.add(n);
            }

            // 3. Ejecución y Reporte delegado
            BenchmarkManager manager = new BenchmarkManager(sources, iterationsPerTest, totalNodes, totalEdges);
            manager.generateHtmlReport(reportPath, threadCountsToTest);

        } catch (Exception e) {
            System.err.println("Error crítico en la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
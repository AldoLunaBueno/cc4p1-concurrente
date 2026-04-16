import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BenchmarkManager {

    private final List<Node> sources;
    private final int iterations;
    private final int totalNodes;
    private final int totalEdges;

    public BenchmarkManager(List<Node> sources, int iterations, int totalNodes, int totalEdges) {
        this.sources = sources;
        this.iterations = iterations;
        this.totalNodes = totalNodes;
        this.totalEdges = totalEdges;
    }

    public void generateHtmlReport(String outputPath, int[] threadCounts) {
        System.out.println("Iniciando generación del Benchmark...");
        
        try {
            System.out.println("-> Evaluando caso secuencial puro...");
            double seqTime = runSequentialBenchmark();

            // Estructuras de datos para inyectar en la plantilla
            List<Integer> jsLabelsThreads = new ArrayList<>();
            List<Double> jsDataSpeedup = new ArrayList<>();
            List<Double> jsDataTime = new ArrayList<>();
            List<Map<String, String>> tableResults = new ArrayList<>();

            for (int threads : threadCounts) {
                System.out.println("-> Evaluando en paralelo con " + threads + " hilos...");
                double parTime = runParallelBenchmark(threads);
                
                double speedup = seqTime / parTime;
                double efficiency = speedup / threads;
                double efficiencyPct = efficiency * 100;

                // Datos para Chart.js
                jsLabelsThreads.add(threads);
                jsDataSpeedup.add(Math.round(speedup * 100.0) / 100.0);
                jsDataTime.add(Math.round(parTime * 100.0) / 100000.0);

                // Datos tabulares formateados
                Map<String, String> row = new HashMap<>();
                row.put("threads", String.valueOf(threads));
                row.put("parTime", String.format("%.2f", parTime));
                row.put("speedup", String.format("%.4f", speedup));
                row.put("efficiencyPct", String.format("%.2f", efficiencyPct));
                tableResults.add(row);
            }

            // Mapeo del contexto (variables) que Mustache inyectará en el HTML
            Map<String, Object> context = new HashMap<>();
            context.put("totalNodes", String.format("%,d", totalNodes));
            context.put("totalEdges", String.format("%,d", totalEdges));
            context.put("density", String.format("%.2f", (double) totalEdges / totalNodes));
            context.put("evaluatedSources", sources.size());
            context.put("totalNodesRaw", totalNodes);
            context.put("iterations", iterations);
            context.put("seqTime", String.format("%.2f", seqTime));
            
            context.put("results", tableResults); // Para iterar con {{#results}}
            
            // Usamos .toString() que en List de Java coincide con el formato de arrays de JS [1, 2, 3]
            context.put("jsLabelsThreads", jsLabelsThreads.toString());
            context.put("jsDataSpeedup", jsDataSpeedup.toString());
            context.put("jsDataTime", jsDataTime.toString());

            // Compilar e Inyectar Plantilla
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile("report.mustache"); 

            try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
                mustache.execute(writer, context).flush();
            }
            
            System.out.println("¡Reporte generado con éxito en: " + outputPath + "!");

        } catch (Exception e) {
            System.err.println("Error generando el reporte HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double runSequentialBenchmark() {
        long totalTimeSum = 0;
        for (int i = 0; i < iterations; i++) {
            System.gc(); 
            long start = System.nanoTime();
            
            for (Node source : sources) {
                Dijkstra dijkstra = new Dijkstra();
                dijkstra.execute(source);
            }
            
            long end = System.nanoTime();
            totalTimeSum += (end - start);
        }
        return (totalTimeSum / 1_000_000.0) / iterations;
    }

    private double runParallelBenchmark(int numThreads) throws Exception {
        long totalTimeSum = 0;
        for (int i = 0; i < iterations; i++) {
            System.gc();

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<Void>> futures = new ArrayList<>();

            long start = System.nanoTime();

            for (Node source : sources) {
                Callable<Void> task = () -> {
                    Dijkstra dijkstra = new Dijkstra();
                    dijkstra.execute(source);
                    return null;
                };
                futures.add(executor.submit(task));
            }

            for (Future<Void> future : futures) {
                future.get();
            }

            executor.shutdown();
            long end = System.nanoTime();
            totalTimeSum += (end - start);
        }
        return (totalTimeSum / 1_000_000.0) / iterations;
    }
}
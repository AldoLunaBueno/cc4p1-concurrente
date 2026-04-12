import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String pathImagesTrain = "backpropagation/data/train-images-idx3-ubyte";
        String pathLabelsTrain = "backpropagation/data/train-labels-idx1-ubyte";
        String pathImagesTest = "backpropagation/data/t10k-images-idx3-ubyte";
        String pathLabelsTest = "backpropagation/data/t10k-labels-idx1-ubyte";

        try {
            System.out.println("Cargando datos...");
            double[][] trainImages = loadImages(pathImagesTrain);
            int[] trainLabels = loadLabels(pathLabelsTrain);

            double[][] testImages = loadImages(pathImagesTest);
            int[] testLabels = loadLabels(pathLabelsTest);

            // Precomputar los targets One-Hot para aliviar el Garbage Collector
            double[][] trainTargets = new double[trainImages.length][10];
            for (int i = 0; i < trainLabels.length; i++) {
                trainTargets[i][trainLabels[i]] = 1.0;
            }

            System.out.println("\n--- 1. ENTRENAMIENTO SERIAL (SGD) ---");
            NeuralNetwork nnSerial = new NeuralNetwork(784, 64, 10);
            long startSerial = System.currentTimeMillis();
            for (int i = 0; i < trainImages.length; i++) {
                nnSerial.train(trainImages[i], trainTargets[i]);
            }
            long endSerial = System.currentTimeMillis();
            System.out.println("Tiempo Serial: " + (endSerial - startSerial) + " ms.");
            evaluarRed(nnSerial, testImages, testLabels, "Serial");

            System.out.println("\n--- 2. ENTRENAMIENTO PARALELO (MINI-BATCH) ---");
            int batchSize = 256; 
            int[] threadCounts = {1, 2, 4, 8, 16};

            List<Integer> hilosUsados = new ArrayList<>();
            List<Long> tiemposParalelos = new ArrayList<>();

            for (int threads : threadCounts) {
                // Instanciar nueva red para cada prueba
                ParallelNeuralNetwork nnParallel = new ParallelNeuralNetwork(784, 64, 10);
                long startPar = System.currentTimeMillis();
                
                // Iterar el dataset en "Lotes" (Mini-Batches)
                for (int i = 0; i < trainImages.length; i += batchSize) {
                    int end = Math.min(i + batchSize, trainImages.length);
                    int currentBatchSize = end - i;
                    
                    double[][] batchInputs = new double[currentBatchSize][];
                    double[][] batchTargets = new double[currentBatchSize][];
                    System.arraycopy(trainImages, i, batchInputs, 0, currentBatchSize);
                    System.arraycopy(trainTargets, i, batchTargets, 0, currentBatchSize);
                    
                    // Entrenamiento concurrente del mini-batch
                    nnParallel.trainBatch(batchInputs, batchTargets, threads);
                }
                
                long endPar = System.currentTimeMillis();
                long elapsed = endPar - startPar;
                System.out.println("Tiempo Paralelo (" + threads + " Hilos): " + elapsed + " ms.");
                evaluarRedParalela(nnParallel, testImages, testLabels, "Paralela - " + threads + " Hilos");

                hilosUsados.add(threads);
                tiemposParalelos.add(elapsed);
            }

            // Generar la visualización del gráfico
            long elapsedSerial = endSerial - startSerial;
            generarGraficoHTML(hilosUsados, tiemposParalelos, elapsedSerial);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void evaluarRed(NeuralNetwork nn, double[][] testImages, int[] testLabels, String nombre) {
        int correct = 0;
        for (int i = 0; i < testImages.length; i++) {
            double[] output = nn.feedforward(testImages[i]);
            int maxIndex = 0;
            double maxVal = output[0];
            for (int j = 1; j < 10; j++) {
                if (output[j] > maxVal) {
                    maxVal = output[j];
                    maxIndex = j;
                }
            }
            if (maxIndex == testLabels[i]) {
                correct++;
            }
        }
        System.out.println("Precisión " + nombre + ": " + String.format("%.2f", (correct * 100.0) / testImages.length) + "%");
    }

    private static void evaluarRedParalela(ParallelNeuralNetwork nn, double[][] testImages, int[] testLabels, String nombre) {
        int correct = 0;
        for (int i = 0; i < testImages.length; i++) {
            double[] output = nn.feedforward(testImages[i]);
            int maxIndex = 0;
            double maxVal = output[0];
            for (int j = 1; j < 10; j++) {
                if (output[j] > maxVal) {
                    maxVal = output[j];
                    maxIndex = j;
                }
            }
            if (maxIndex == testLabels[i]) {
                correct++;
            }
        }
        System.out.println("Precisión " + nombre + ": " + String.format("%.2f", (correct * 100.0) / testImages.length) + "%");
    }

    private static void generarGraficoHTML(List<Integer> hilos, List<Long> tiempos, long tiempoSerial) {
        try (PrintWriter writer = new PrintWriter(new File("benchmark_grafica.html"))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head><title>Benchmark Paralelo Backpropagation</title>");
            writer.println("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>");
            writer.println("<style>body{font-family: sans-serif; text-align: center; background-color: #f4f4f9;} canvas{max-height: 500px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);}</style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<h2>Entrenamiento Backpropagation: Hilos vs Tiempo</h2>");
            writer.println("<h4>(Tiempo Serial SGD de Referencia: " + tiempoSerial + " ms)</h4>");
            writer.println("<div style=\"width: 80%; margin: 0 auto;\"><canvas id=\"graficoHilos\"></canvas></div>");
            writer.println("<script>");
            writer.println("const ctx = document.getElementById('graficoHilos').getContext('2d');");
            writer.println("new Chart(ctx, {");
            writer.println("    type: 'line',");
            writer.println("    data: {");
            
            // Array de Labels
            StringBuilder labels = new StringBuilder("[");
            for (int h : hilos) labels.append(h).append(",");
            labels.append("]");
            writer.println("        labels: " + labels.toString() + ",");
            
            writer.println("        datasets: [{");
            writer.println("            label: 'Tiempo en Paralelo (Milisegundos)',");
            
            // Array de Data
            StringBuilder dat = new StringBuilder("[");
            for (long t : tiempos) dat.append(t).append(",");
            dat.append("]");
            
            writer.println("            data: " + dat.toString() + ",");
            writer.println("            borderColor: 'rgb(54, 162, 235)',");
            writer.println("            backgroundColor: 'rgba(54, 162, 235, 0.2)',");
            writer.println("            tension: 0.1,");
            writer.println("            pointRadius: 8,");
            writer.println("            pointBackgroundColor: 'rgb(255, 99, 132)'");
            writer.println("        }]");
            writer.println("    },");
            writer.println("    options: {");
            writer.println("        scales: {");
            writer.println("            x: { title: {display: true, text: 'Cantidad de Hilos Lógicos', font: {size: 14}} },");
            writer.println("            y: { title: {display: true, text: 'Duración (ms)', font: {size: 14}}, beginAtZero: true }");
            writer.println("        },");
            writer.println("        plugins: {");
            writer.println("            legend: { labels: { font: {size: 16} } }");
            writer.println("        }");
            writer.println("    }");
            writer.println("});");
            writer.println("</script>");
            writer.println("</body>");
            writer.println("</html>");
            System.out.println("\n[!] Gráfico exportado con éxito a: benchmark_grafica.html (Abre este archivo en tu navegador)");
        } catch (Exception e) {
            System.err.println("Error generando el gráfico HTML: " + e.getMessage());
        }
    }

    private static double[][] loadImages(String filepath) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
            int magicNumber = dis.readInt();
            if (magicNumber != 2051) {
                throw new IOException("Archivo de imágenes no válido: Magic number erróneo");
            }
            int numImages = dis.readInt();
            int numRows = dis.readInt();
            int numCols = dis.readInt();

            double[][] images = new double[numImages][numRows * numCols];
            for (int i = 0; i < numImages; i++) {
                for (int j = 0; j < numRows * numCols; j++) {
                    // El valor es un unsigned byte (0-255)
                    images[i][j] = dis.readUnsignedByte() / 255.0; // Normalización a [0, 1]
                }
            }
            return images;
        }
    }

    private static int[] loadLabels(String filepath) throws IOException {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
            int magicNumber = dis.readInt();
            if (magicNumber != 2049) {
                throw new IOException("Archivo de etiquetas no válido: Magic number erróneo");
            }
            int numLabels = dis.readInt();

            int[] labels = new int[numLabels];
            for (int i = 0; i < numLabels; i++) {
                labels[i] = dis.readUnsignedByte();
            }
            return labels;
        }
    }
}

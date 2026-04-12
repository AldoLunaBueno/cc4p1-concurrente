import java.util.Random;

public class ParallelNeuralNetwork {
    private int inputNodes, hiddenNodes, outputNodes;
    private double[][] weightsIH; // Pesos entre Entrada e Hidratada
    private double[][] weightsHO; // Pesos entre Hidratada y Salida
    private double[] biasH; // Bias capa oculta
    private double[] biasO; // Bias capa salida

    // Al realizar mini-batch, se actualizan pesos con menos frecuencia, compensamos
    // la tasa
    private double learningRate = 0.5;

    public ParallelNeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
        this.inputNodes = inputNodes;
        this.hiddenNodes = hiddenNodes;
        this.outputNodes = outputNodes;

        this.weightsIH = new double[hiddenNodes][inputNodes];
        this.weightsHO = new double[outputNodes][hiddenNodes];
        this.biasH = new double[hiddenNodes];
        this.biasO = new double[outputNodes];

        randomizeWeights();
    }

    private void randomizeWeights() {
        Random r = new Random(1234); // Semilla fija para consistencia en las pruebas
        for (int i = 0; i < hiddenNodes; i++) {
            for (int j = 0; j < inputNodes; j++)
                weightsIH[i][j] = r.nextDouble() * 2 - 1;
            biasH[i] = r.nextDouble() * 2 - 1;
        }
        for (int i = 0; i < outputNodes; i++) {
            for (int j = 0; j < hiddenNodes; j++)
                weightsHO[i][j] = r.nextDouble() * 2 - 1;
            biasO[i] = r.nextDouble() * 2 - 1;
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double dSigmoid(double y) {
        return y * (1.0 - y);
    }

    public double[] feedforward(double[] inputArr) {
        double[] hidden = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double sum = 0;
            for (int j = 0; j < inputNodes; j++) {
                sum += inputArr[j] * weightsIH[i][j];
            }
            hidden[i] = sigmoid(sum + biasH[i]);
        }

        double[] output = new double[outputNodes];
        for (int i = 0; i < outputNodes; i++) {
            double sum = 0;
            for (int j = 0; j < hiddenNodes; j++) {
                sum += hidden[j] * weightsHO[i][j];
            }
            output[i] = sigmoid(sum + biasO[i]);
        }
        return output;
    }

    // Estructura contenedora para variables temporales (Deltas de pesos) para que
    // cada hilo pueda trabajar independientemente sin competir (Evitar Race
    // Conditions).
    private class ThreadGradients {
        double[][] deltaWeightsIH = new double[hiddenNodes][inputNodes];
        double[][] deltaWeightsHO = new double[outputNodes][hiddenNodes];
        double[] deltaBiasH = new double[hiddenNodes];
        double[] deltaBiasO = new double[outputNodes];
    }

    /**
     * Entrena la red neuronal subdividiendo un lote de imágenes en múltiples hilos
     * nativos (Mini-Batch Data Parallelism sin ExecutorService).
     */
    public void trainBatch(double[][] inputs, double[][] targets, int numThreads) throws InterruptedException {
        int totalSamples = inputs.length;
        int blockSize = (int) Math.ceil((double) totalSamples / numThreads);

        Thread[] threads = new Thread[numThreads];
        ThreadGradients[] results = new ThreadGradients[numThreads];

        for (int t = 0; t < numThreads; t++) {
            final int threadIndex = t;
            final int startIdx = t * blockSize;
            final int endIdx = Math.min(startIdx + blockSize, totalSamples);

            if (startIdx >= endIdx) {
                // Hilo redundante sin trabajo que realizar
                threads[t] = new Thread(() -> {
                });
                results[t] = new ThreadGradients(); // Deltas vacíos (puro ceros)
                threads[t].start();
                continue;
            }

            threads[t] = new Thread(() -> {
                ThreadGradients localGrads = new ThreadGradients();

                // Cada hilo procesa su parte del lote secuencialmente y acumula errores
                // localmente
                for (int s = startIdx; s < endIdx; s++) {
                    double[] inputArr = inputs[s];
                    double[] targetArr = targets[s];

                    // --- FEEDFORWARD LOCAL ---
                    double[] hidden = new double[hiddenNodes];
                    for (int i = 0; i < hiddenNodes; i++) {
                        double sum = 0;
                        for (int j = 0; j < inputNodes; j++)
                            sum += inputArr[j] * weightsIH[i][j];
                        hidden[i] = sigmoid(sum + biasH[i]);
                    }

                    double[] outputs = new double[outputNodes];
                    for (int i = 0; i < outputNodes; i++) {
                        double sum = 0;
                        for (int j = 0; j < hiddenNodes; j++)
                            sum += hidden[j] * weightsHO[i][j];
                        outputs[i] = sigmoid(sum + biasO[i]);
                    }

                    // --- BACKPROPAGATION LOCAL ---
                    double[] outputErrors = new double[outputNodes];
                    for (int i = 0; i < outputNodes; i++) {
                        outputErrors[i] = targetArr[i] - outputs[i];
                    }

                    for (int i = 0; i < outputNodes; i++) {
                        double gradient = outputErrors[i] * dSigmoid(outputs[i]) * learningRate;
                        for (int j = 0; j < hiddenNodes; j++) {
                            localGrads.deltaWeightsHO[i][j] += gradient * hidden[j];
                        }
                        localGrads.deltaBiasO[i] += gradient;
                    }

                    double[] hiddenErrors = new double[hiddenNodes];
                    for (int i = 0; i < hiddenNodes; i++) {
                        double error = 0;
                        for (int j = 0; j < outputNodes; j++) {
                            error += outputErrors[j] * weightsHO[j][i];
                        }
                        hiddenErrors[i] = error;
                    }

                    for (int i = 0; i < hiddenNodes; i++) {
                        double gradient = hiddenErrors[i] * dSigmoid(hidden[i]) * learningRate;
                        for (int j = 0; j < inputNodes; j++) {
                            localGrads.deltaWeightsIH[i][j] += gradient * inputArr[j];
                        }
                        localGrads.deltaBiasH[i] += gradient;
                    }
                }
                // Guardar los resultados en el array compartido asegurando el índice del hilo
                results[threadIndex] = localGrads;
            });
            threads[t].start();
        }

        // Bloquear el hilo principal (Sincronización Barrera) hasta que todos los hilos
        // crudos terminen
        for (int t = 0; t < numThreads; t++) {
            threads[t].join();
        }

        // Juntar los resultados (Deltas promediados) de todos los hilos y aplicar al
        // peso real
        for (int t = 0; t < numThreads; t++) {
            ThreadGradients grad = results[t];
            for (int i = 0; i < hiddenNodes; i++) {
                for (int j = 0; j < inputNodes; j++) {
                    weightsIH[i][j] += grad.deltaWeightsIH[i][j] / totalSamples;
                }
                biasH[i] += grad.deltaBiasH[i] / totalSamples;
            }
            for (int i = 0; i < outputNodes; i++) {
                for (int j = 0; j < hiddenNodes; j++) {
                    weightsHO[i][j] += grad.deltaWeightsHO[i][j] / totalSamples;
                }
                biasO[i] += grad.deltaBiasO[i] / totalSamples;
            }
        }
    }
}

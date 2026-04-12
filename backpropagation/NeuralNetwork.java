import java.util.Random;

public class NeuralNetwork {
    private int inputNodes, hiddenNodes, outputNodes;
    private double[][] weightsIH; // Pesos entre Entrada e Hidratada
    private double[][] weightsHO; // Pesos entre Hidratada y Salida
    private double[] biasH; // Bias capa oculta
    private double[] biasO; // Bias capa salida
    private double learningRate = 0.1;

    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
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
        Random r = new Random();
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

    // Función de activación Sigmoide
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // Derivada de la Sigmoide para el Backpropagation
    private double dSigmoid(double y) {
        return y * (1.0 - y);
    }

    public double[] feedforward(double[] inputArr) {
        // 1. Generar salidas de la capa oculta
        double[] hidden = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double sum = 0;
            for (int j = 0; j < inputNodes; j++) {
                sum += inputArr[j] * weightsIH[i][j];
            }
            hidden[i] = sigmoid(sum + biasH[i]);
        }

        // 2. Generar salida final
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

    public void train(double[] inputArr, double[] targetArr) {
        // --- FEEDFORWARD (necesitamos los valores intermedios) ---
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

        // --- BACKPROPAGATION ---

        // 1. Calcular error de salida (Error = Target - Output)
        double[] outputErrors = new double[outputNodes];
        for (int i = 0; i < outputNodes; i++) {
            outputErrors[i] = targetArr[i] - outputs[i];
        }

        // 2. Calcular gradientes de salida y ajustar pesos HO
        for (int i = 0; i < outputNodes; i++) {
            double gradient = outputErrors[i] * dSigmoid(outputs[i]) * learningRate;
            for (int j = 0; j < hiddenNodes; j++) {
                weightsHO[i][j] += gradient * hidden[j];
            }
            biasO[i] += gradient;
        }

        // 3. Calcular error de la capa oculta (Propagar error hacia atrás)
        double[] hiddenErrors = new double[hiddenNodes];
        for (int i = 0; i < hiddenNodes; i++) {
            double error = 0;
            for (int j = 0; j < outputNodes; j++) {
                error += outputErrors[j] * weightsHO[j][i];
            }
            hiddenErrors[i] = error;
        }

        // 4. Calcular gradientes ocultos y ajustar pesos IH
        for (int i = 0; i < hiddenNodes; i++) {
            double gradient = hiddenErrors[i] * dSigmoid(hidden[i]) * learningRate;
            for (int j = 0; j < inputNodes; j++) {
                weightsIH[i][j] += gradient * inputArr[j];
            }
            biasH[i] += gradient;
        }
    }
}

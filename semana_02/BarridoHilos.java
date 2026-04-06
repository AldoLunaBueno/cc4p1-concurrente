import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;

public class BarridoHilos {

    public static void main(String[] args) {
        int H = 10;
        int numMuestra = 3;
        Scanner sc = new Scanner(System.in);
        System.out.print("Orden de magnitud de N: ");
        int exponente = Integer.parseInt(sc.nextLine());
        String rellenoExponente = String.format("%02d", exponente);
        String fileName = "output_" + rellenoExponente + ".txt";
        try {
            Files.writeString(Paths.get(fileName), "");
        } catch (IOException e) { System.out.println(e); }
        BigInteger resultadoPrevio = BigInteger.valueOf(0L);
        BigInteger resultado = BigInteger.valueOf(0L);
        BigInteger base = BigInteger.valueOf(10L);
        BigInteger N = base.pow(exponente);
        long tTotal = 0L;
        for (int h=1; h<=H; h++) {
            SumaTodosBigInt instancia = new SumaTodosBigInt();
            for (int i=0; i<numMuestra; i++) {
                long tInicial = System.nanoTime();
                resultado = instancia.inicio(h, N);
                long tFinal = System.nanoTime();
                if (i!=0 && h!=1 && !resultado.equals(resultadoPrevio)) {
                    System.exit(-1);
                }
                long tMuestra = tFinal - tInicial;
                tTotal += tMuestra;
                resultadoPrevio = resultado;
            }
            double tPromedio = (double) tTotal / numMuestra;
            double time = tPromedio / 1_000_000_000.0;
            String line = h + "\t" + String.format("%.4f", time);
            System.out.println(line);
            try {
                Files.writeString(Paths.get(fileName), line + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) { System.err.println(e); }
            tTotal = 0L;
        }
        System.out.println(resultado);
    }
}
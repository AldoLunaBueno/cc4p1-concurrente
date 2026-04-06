import java.math.BigInteger;

public class SumaTodosBigInt {
    public BigInteger sum[];

    BigInteger inicio(int numHilos, BigInteger N) {
        int d = (int) (N.divide(BigInteger.valueOf(numHilos)).intValue());
        Thread todos[] = new Thread[numHilos];
        this.sum = new BigInteger[numHilos];
        for (int i = 0; i < (numHilos - 1); i++) {
            todos[i] = new tarea0101((i * d + 1), (i * d + d), i);
            todos[i].start();
        }
        //Thread numHilosilo;
        todos[numHilos - 1] = new tarea0101(((d * (numHilos - 1)) + 1), N.longValue(), numHilos - 1);
        todos[numHilos - 1].start();

        for (int i = 0; i < numHilos; i++) {
            try {
                todos[i].join();
            } catch (InterruptedException ex) {
                // System.out.println("error" + ex);
            }
        }
        BigInteger sumatotal = BigInteger.ZERO;
        for (int i = 0; i < numHilos; i++) {
            sumatotal = sumatotal.add(sum[i]);
        }
        // System.out.println("suma total:" + sumatotal);
        return sumatotal;
    }

    public class tarea0101 extends Thread {
        // Cambiamos a long para soportar rangos mayores
        public long max, min; 
        public int id;

        // Actualizamos los parámetros a long
        tarea0101(long min_, long max_, int id_) { 
            max = max_;
            min = min_;
            id = id_;
            // System.out.println("id " + id + " min: " + min_ + " max " + max_);
        }

        public void run() {
            BigInteger suma = BigInteger.ZERO;
            for (long i = min; i <= max; i++){
                suma = suma.add(BigInteger.valueOf(i));
            }
            sum[id] = suma;
            // System.out.println("id " + id + " suma: " + suma);
        }
    }
}
package uni.cc4p1.labo_02.game;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ManejadorDeConHilos implements Runnable{
    private Socket entrante;
    private int contador;
    String[][] mitabla;
    public ManejadorDeConHilos(Socket i,int c, String[][] tabla){
        entrante = i; contador = c; 
        mitabla = tabla;
    }
    public void run(){
        try {
            try {
                InputStream secuenciaDeEntrada = entrante.getInputStream();
                OutputStream secuenciaDeSalida = entrante.getOutputStream();

                Scanner in = new Scanner(secuenciaDeEntrada);
                PrintWriter out = new PrintWriter(secuenciaDeSalida,true);

                out.println("Hola Jugador:" + contador + " Escriba ADIOS para salir");

                boolean terminado = false;
                while(!terminado && in.hasNextLine()){
                    String linea = in.nextLine();
                    //out.println("Eco"+linea);
                    System.out.println("Eco de:"+contador+" dice:"+linea);
                    String arrayString[] = linea.split("\\s+");
                    int x = Integer.parseInt(arrayString[0]);
                    int y = Integer.parseInt(arrayString[1]);
                    
                    if (contador == 1)
                        mitabla[x][y]="a";
                    else if (contador == 2)
                        mitabla[x][y]="b";
                    else if (contador == 3)
                        mitabla[x][y]="c";   
                    else if (contador == 4)
                        mitabla[x][y]="d";
                    else if (contador == 5)
                        mitabla[x][y]="e";     
                    
                    String rpta ="";
                    rpta = rpta + "\r\n";
                    for(int i=0; i<5; i++){
                        for(int j=0; j<8; j++){
                            System.out.print(mitabla[i][j]);
                            //out.print(mitabla[i][j]);
                            rpta = rpta + mitabla[i][j];
                            
                        }
                        rpta = rpta + "\r\n";
                        System.out.println("");
                    }
                    out.println(rpta);
                    
                    if(linea.trim().equals("ADIOS")){
                        terminado = true;
                    }
                }                
            } finally {
                entrante.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

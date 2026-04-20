package uni.redestalk;

import java.util.Scanner;

public class TareaServidor {
   TCPServer50 mTcpServer;
   Scanner sc;
   public static void main(String[] args) {
       TareaServidor objser = new TareaServidor();
       objser.iniciar();
   }
   void iniciar(){
       new Thread(
            new Runnable() {

                @Override
                public void run() {
                      mTcpServer = new TCPServer50(
                        new TCPServer50.OnMessageReceived(){
                            @Override
                            public void messageReceived(String message){
                                ServidorRecibe(message);
                            }
                        }
                    );
                    mTcpServer.run();                   
                }
            }
        ).start();
        //-----------------
        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Servidor bandera 01");
        while( !salir.equals("s")){
            salir = sc.nextLine();
            ServidorEnvia(salir);
       }
       System.out.println("Servidor bandera 02"); 
   
   }
   void ServidorRecibe(String llego){
       System.out.println("SERVIDOR40 El mensaje:" + llego);
   }
   void ServidorEnvia(String envia){
        if (mTcpServer != null) {
            mTcpServer.sendMessageTCPServer(envia);
        }
   }
}

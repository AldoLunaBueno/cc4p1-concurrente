package uni.redestalk;

import java.util.Scanner;

class TareaCliente
{
    TCPClient50 mTcpClient;
    Scanner sc;
    public static void main(String[] args)  {
        TareaCliente objcli = new TareaCliente();
        objcli.iniciar();
    }
    void iniciar(){
       new Thread(
            new Runnable() {

                @Override
                public void run() {
                    mTcpClient = new TCPClient50("127.0.0.1",
                        new TCPClient50.OnMessageReceived(){
                            @Override
                            public void messageReceived(String message){
                                ClienteRecibe(message);
                                Integer suma = procesarComando(message);
                                if (suma == null) return;
                                ClienteEnvia(suma.toString());
                            }
                        }
                    );
                    mTcpClient.run();                   
                }
            }
        ).start();
        //---------------------------
       
        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Cliente bandera 01");
        while( !salir.equals("s")){
            salir = sc.nextLine();
            ClienteEnvia(salir);
        }
        System.out.println("Cliente bandera 02");
    
    }
    void ClienteRecibe(String llego){
        System.out.println("CLINTE50 El mensaje::" + llego);

    }
    void ClienteEnvia(String envia){
        if (mTcpClient != null) {
            mTcpClient.sendMessage(envia);
        }
    }

    Integer procesarComando(String mensaje) {
        String[] comandoParseado = mensaje.split(" ");
        switch (comandoParseado[0]) {
            case "sumar":
                System.out.println("Sumando...");
                if (comandoParseado.length != 3) {
                    System.out.println("Error: solo se suma dos números, así: sumar 1 2");
                    return null;
                }
                return (int) Integer.valueOf(comandoParseado[1]) + Integer.valueOf(comandoParseado[2]);
        
            default:
                return 0;
        }
    }

}

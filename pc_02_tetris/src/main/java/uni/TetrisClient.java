/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uni;

import java.net.*;
import java.io.*;

public class TetrisClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

        // hilo para escuchar estado
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Estado: " + line);
                }
            } catch (IOException e) {}
        }).start();

        // input del usuario
        BufferedReader keyboard = new BufferedReader(
                new InputStreamReader(System.in));

        String input;
        while ((input = keyboard.readLine()) != null) {
            out.println(input);
        }
    }
}
    
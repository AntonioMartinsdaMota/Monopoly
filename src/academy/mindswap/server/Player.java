package academy.mindswap.server;

import academy.mindswap.positions.Positions;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Player {

    public static void main(String[] args) {

        Player player = new Player();
        player.connectServer();

    }

    private Socket socket;
    private BufferedReader in;
    BufferedWriter out;


    class ConnectionHandler implements Runnable {

        BufferedReader bufferedReader;
        BufferedWriter bufferedWriter;
        private Socket socket;


        public ConnectionHandler(Socket socket, BufferedWriter bufferedWriter) {
            this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.bufferedWriter = bufferedWriter;
            this.socket = socket;
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    if (bufferedReader.ready()) {
                        String choice = bufferedReader.readLine();
                        bufferedWriter.write(choice);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    public void connectServer() {
        try {
            this.socket = new Socket("localhost", 8081);
            in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(new ConnectionHandler(socket, out)).start();
            while (!socket.isClosed()) {
                while (in.ready()) {
                    System.out.println(in.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player() {
    }
}












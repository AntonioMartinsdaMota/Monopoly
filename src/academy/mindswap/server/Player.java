package academy.mindswap.server;

import academy.mindswap.positions.Positions;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Player {

    private String name;
    private int balance;
    private int position;
    private boolean isInJail;
    private boolean isDead;
    private List<Positions> cardsOwned;
    private int roundCounter;
    private int lapCounter;
    private Socket socket;
    private BufferedReader in;
    BufferedWriter out;


    class ConnectionHandler implements Runnable{

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


    public void connectServer(){
        try {
            this.socket = new Socket("localhost", 8081);
            in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new Thread(new ConnectionHandler(socket, out)).start();
            while(!socket.isClosed()){
                while(in.ready()) {
                    System.out.println(in.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player() {
    }

    private String generateName() {
        String names = name + "A";
        return names;
    }

    public int getRoundCounter() {
        return roundCounter;
    }

    public void setRoundCounter(int roundCounter) {
        this.roundCounter = roundCounter;
    }

    public int getLapCounter() {
        return lapCounter;
    }

    public void setLapCounter(int lapCounter) {
        this.lapCounter = lapCounter;
    }

    public List<Positions> getCardsOwned() {
        return cardsOwned;
    }

    public void setCardsOwned(List<Positions> cardsOwned) {
        this.cardsOwned = cardsOwned;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
        }
    public boolean isInJail() {
        return isInJail;
    }

    public void setInJail(boolean inJail) {
        isInJail = inJail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}










package academy.mindswap;

import academy.mindswap.client.Client;
import academy.mindswap.positions.Positions;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Player {

    private String name;
    private int balance;
    private int position;
    private boolean isInJail;
    private boolean isDead;
    private List<Positions> cardsOwned;
    private int roundCounter;
    private int lapCounter;


    public Player(String name) {
        this.name = name;
        this.balance = 1500;
        this.position = 0;
        this.cardsOwned = new LinkedList<>();
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










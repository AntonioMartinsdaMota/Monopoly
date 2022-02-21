package academy.mindwsap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Player {

    private String name;
    private String color;
    private int balance;
    private int position;
    private boolean isInJail;
    private boolean isAlive;
    private List<Positions> cardsOwned;

    public boolean isAlive() {
        return isAlive;
    }

    public List<Positions> getCardsOwned() {
        return cardsOwned;
    }

    public void setCardsOwned(List<Positions> cardsOwned) {
        this.cardsOwned = cardsOwned;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isInJail() {
        return isInJail;
    }

    public void setInJail(boolean inJail) {
        isInJail = inJail;
    }

    public Player(String name, String color) {
        this.name = name;
        this.color = color;
        this.balance = 1500;
        this.position = 0;
        this.cardsOwned = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    public int getPosition() {
        return position;

    }public void setPosition(int position) {
        this.position = position;
    }




}

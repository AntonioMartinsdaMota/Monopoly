package academy.mindswap.positions;

import academy.mindswap.Player;

public class Houses extends Positions {

    protected String color;
    protected int buyPrice;
    protected int rentPrice;
    protected Player owner;
    protected boolean isOwned;


    public Houses(String name, String color, int buyPrice, int rentPrice) {
        super(name);
        this.color = color;
        this.buyPrice = buyPrice;
        this.rentPrice = rentPrice;
        this.isOwned = false;

    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(int rentPrice) {
        this.rentPrice = rentPrice;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }
}
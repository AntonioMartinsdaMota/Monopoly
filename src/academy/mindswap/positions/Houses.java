package academy.mindswap.positions;

import academy.mindswap.server.Game;
import academy.mindswap.server.Player;

public class Houses extends Positions {

    protected String color;
    protected int buyPrice;
    protected int rentPrice;
    protected Game.PlayerHandler owner;
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

    public int getBuyPrice() {
        return buyPrice;
    }

    public int getRentPrice() {
        return rentPrice;
    }

    public Game.PlayerHandler getOwner() {
        return owner;
    }

    public void setOwner(Game.PlayerHandler owner) {
        this.owner = owner;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }
}
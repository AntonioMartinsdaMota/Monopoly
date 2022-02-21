package academy.mindwsap;

public class Positions {

    private String name;
    private String roadName;
    private String color;
    private int buyPrice;
    private int rentPrice;
    private boolean isOwned;

    public Positions(String name) {
        this.name = name;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public String getRoadName() {
        return roadName;
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

    public String getName() {
        return name;

    }

}

package academy.mindwsap;

public class RailRoad extends Positions {

    private String roadName;
    private String color;
    private int buyPrice;
    private int rentPrice;

    @Override
    public String getRoadName() {
        return roadName;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public int getBuyPrice() {
        return buyPrice;
    }

    @Override
    public int getRentPrice() {
        return rentPrice;
    }

    public RailRoad(String roadName) {
        super(roadName);
        this.color = "Black";
        this.buyPrice = 200;
        this.rentPrice = 100;

    }

    @Override
    public String toString() {
        return "RailRoad{}";
    }
}

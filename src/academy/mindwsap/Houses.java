package academy.mindwsap;

public class Houses extends Positions {

    private String roadName;
    private String color;
    private int buyPrice;
    private int rentPrice;

    public Houses(String roadName, String color, int buyPrice, int rentPrice) {
        super("House");
        this.roadName = roadName;
        this.color = color;
        this.buyPrice = buyPrice;
        this.rentPrice = rentPrice;
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

    @Override
    public String toString() {
        return "Houses{" +
                "roadName='" + roadName + '\'' +
                ", color='" + color + '\'' +
                ", buyPrice=" + buyPrice +
                ", rentPrice=" + rentPrice +
                '}';
    }
}

package academy.mindswap.positions;

public class Tax extends Positions {

    private String name;
    private int rentPrice;


    public Tax(String name, int rentPrice) {
        super("Tax");
        this.name = name;
        this.rentPrice = rentPrice;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(int rentPrice) {
        this.rentPrice = rentPrice;
    }
}



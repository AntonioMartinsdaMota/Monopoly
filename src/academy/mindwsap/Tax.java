package academy.mindwsap;

public class Tax extends Positions{

    private int rentPrice;



    public Tax(String name, int rentPrice) {
        super(name);
        this.rentPrice = rentPrice;
    }

    public int getRentPrice() {

        return rentPrice;
    }


    @Override
    public String toString() {
        return "Tax{" +
                "payPrice=" + rentPrice +
                '}';
    }
}




package academy.mindswap.positions;

import java.util.ArrayList;
import java.util.List;

public class Mystery extends Positions {

    private int rentPrice;

    private static final List<String> mysteryCard = new ArrayList<>(List.of("Pay","Collect"));


    public Mystery() {
        super("Mystery Card");
        this.rentPrice = 150;

    }

    public static String generateRandomCard(){

        int i = (int) (Math.random()*2);
       return mysteryCard.get(i);
    }


    public int getRentPrice() {
        return rentPrice;
    }

    @Override
    public String toString() {
        return "Mystery{}";
    }
}

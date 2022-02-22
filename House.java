public class House implements Position {
    private String name;
    private int buyPrice;
    private int rentPrice;
    private Player owner;

    public House(String name, int buyPrice, int rentPrice) {
        this.buyPrice = buyPrice;
        this.name = name;
        this.rentPrice = rentPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private void buy(Player player) {
        if(player.pay(this.buyPrice)){
            this.owner = player;
        }
        //TODO check if player has enough money to buy the house
    }

    private void payRent(Player player) {
        if(player.pay(this.rentPrice)){
            this.owner.addMoney(this.rentPrice);
        } else {
            //TODO goToJail();
        }
    }

    public void event(Player player) {
        if (this.owner == null) {
            /*
            boolean validChoice = false;
            while(!validChoice){
            System.out.println("Do you want to buy the house?")
            //Read user Input
            String input = //readUserInput

            switch(input.toLowerCase()){
                case "y":
                    buy(player);
                    validChoice = true;
                    break;
                case "n":
                    validChoice = true;
                    break;
                    //no-op
                default:
                    System.out.println("Invalid operation")
                    //TODO error handling; maybe repeat question
            }
            }

            */
        } else {
            payRent(player);
        }
    }
}

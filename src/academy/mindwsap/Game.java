package academy.mindwsap;

import java.io.InputStreamReader;

public class Game {

    private static final int gameWallet = 0;
    private Positions[] board = new Positions[40];
    private Player player;
    private int diceCounter = 0;
    private boolean hasSameNumbers;
    private boolean isRoundOver;
    private boolean alreadyMoved;
    private int roundsCounter;

    Dices dice1 = new Dices(1, 6);
    Dices dice2 = new Dices(1, 6);

    public Game(int numberOfPlayers) {
        createBoard();
    }

    public void createBoard() {

        board[0] = new Positions("START!");
        board[1] = new Houses("Mediterranean-Avenue", "Brown", 60, 40);
        board[2] = new Mystery();
        board[3] = new Houses("Baltic-Avenue", "Brown", 60, 40);
        board[4] = new Tax("Income-Tax", 200);
        board[5] = new RailRoad("Reading-RailRoad");
        board[6] = new Houses("Oriental-Avenue", "Silver", 100, 80);
        board[7] = new Mystery();
        board[8] = new Houses("Vermont-Avenue", "Silver", 100, 80);
        board[9] = new Houses("Connecticut-Avenue", "Silver", 120, 100);
        board[10] = new Positions("Jail");
        board[11] = new Houses("St.Charles-Place", "Pink", 140, 120);
        board[12] = new Tax("Electric-Tax", 150);
        board[13] = new Houses("States-Avenue", "Pink", 140, 120);
        board[14] = new Houses("Virginia-Avenue", "Pink", 160, 140);
        board[15] = new RailRoad("Pennsylvania-RailRoad");
        board[16] = new Houses("St.James-Place", "Orange", 180, 160);
        board[17] = new Mystery();
        board[18] = new Houses("Tennessee-Avenue", "Orange", 180, 160);
        board[19] = new Houses("New York-Avenue", "Orange", 200, 180);
        board[20] = new Positions("Free-Parking");
        board[21] = new Houses("Kentucky-Avenue", "Red", 220, 200);
        board[22] = new Mystery();
        board[23] = new Houses("Indiana-Avenue", "Red", 220, 200);
        board[24] = new Houses("Illinois-Avenue", "Red", 240, 220);
        board[25] = new RailRoad("B.& O.-RailRoad");
        board[26] = new Houses("Atlantic-Avenue", "Yellow", 260, 240);
        board[27] = new Houses("Vent-nor-Avenue", "Yellow", 260, 240);
        board[28] = new Tax("Water-Tax", 150);
        board[29] = new Houses("Marvin-Gardens", "Yellow", 280, 260);
        board[30] = new Positions("Go to Jail!");
        board[31] = new Houses("Pacific-Avenue", "Green", 300, 280);
        board[32] = new Houses("North-Carolina-Avenue", "Green", 300, 280);
        board[33] = new Mystery();
        board[34] = new Houses("Pennsylvania-Avenue", "Green", 320, 300);
        board[35] = new RailRoad("Short-Line");
        board[36] = new Mystery();
        board[37] = new Houses("Park-Place", "Blue", 350, 325);
        board[38] = new Tax("Luxury-Tax", 100);
        board[39] = new Houses("Boardwalk", "Blue", 400, 350);
    }


    private int rollDices() {


        int firstDice = dice1.getRandomNumber();
        int secondDice = dice2.getRandomNumber();

        if (firstDice == secondDice) {
            hasSameNumbers = true;
            System.out.println("You can roll again");
            diceCounter++;
        }
        diceCounter = 0;
        hasSameNumbers = false;

        return firstDice + secondDice;
    }

    private void move(int diceValue) {
        int newPosition = player.getPosition() + diceValue;
        player.setPosition(newPosition);
    }

    private void payToPlayer() {

    }

    private void payToGame() {

    }

    private void payBail() {
        if (player.isInJail()) {
            player.setBalance(player.getBalance() - 500);
            System.out.println("You payed 500 € to go out of jail");
            return;
        }
        System.out.println("You cant pay bail, because you are not in jail");
    }

    private void jailChecker() {

    }

    private void checkGoToJail() {

        if (diceCounter == 3) {
            player.setPosition(10);
            player.setInJail(true);
            System.out.println("You are in Jail");
            return;
        }
        if (player.getPosition() == 30) {
            player.setPosition(10);
            player.setInJail(true);
            System.out.println("You are in Jail");

        }

    }

    private boolean checkIfCanBuy(){

        if (alreadyMoved && !board[player.getPosition()].isOwned() && player.getBalance() >= board[player.getPosition()]
                .getBuyPrice() && board[player.getPosition()] instanceof Houses &&
                board[player.getPosition()] instanceof RailRoad ){
            return true;
        }
        return false;
    }

    private void getPositionDetails() {
        int i = player.getPosition();
        Positions card = board[i];

        checkGoToJail();

        if (card instanceof Houses) {

            System.out.println("Card name: " + card.getName());
            System.out.println("Card Color: " + card.getColor());
            System.out.println("Card Buy Price: " + card.getBuyPrice());
            System.out.println("Card Rent Price: " + card.getRentPrice());
            return;
        }

        if (card instanceof Tax) {
            System.out.println("Card name: " + card.getName());
            System.out.println("Card Rent Price: " + card.getRentPrice());
            return;
        }
        if (card instanceof RailRoad) {
            System.out.println("Card name: " + card.getName());
            System.out.println("Card Buy Price: " + card.getBuyPrice());
            System.out.println("Card Rent Price: " + card.getRentPrice());
            return;
        }
        if (card instanceof Mystery) {
            System.out.println("You got a Mystery Card");
            dealMysteryCard();
        }
        if (i == 0){
            System.out.println("You are now in Starting position");
        }

    }

    private void gameSetup(InputStreamReader inputStreamReader) {
        if (inputStreamReader.equals("Pay")) {
            payToPlayer();
        }
        if (inputStreamReader.equals("Roll")) {
            int dicesValue = rollDices();
            System.out.println("You threw: " + dicesValue);
            if (!player.isInJail()) {
                move(dicesValue);
                alreadyMoved = true;
                System.out.println("Your new position is: " + player.getPosition());
                getPositionDetails();

                if (hasSameNumbers && diceCounter < 3) {
                    isRoundOver = false;
                    System.out.println("You can play again");
                    //Dizer ao sistema que esse player pode voltar a escrever roll!
                }
            }
            if (inputStreamReader.equals("Buy")) {
                if (checkIfCanBuy()) {
                    board[player.getPosition()].setOwned(true);
                    player.getCardsOwned().add(board[player.getPosition()]);
                    System.out.println("You bought this property");
                }
            }

        }

        if (inputStreamReader.equals("Pay Bail")) {
            payBail();
        }


    }
}
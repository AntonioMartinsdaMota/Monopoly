package academy.mindswap;

import academy.mindswap.other.Dices;
import academy.mindswap.positions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Game {

    private static int gameWallet = 0;
    private Positions[] board = new Positions[40];
    private boolean isRoundOver;
    private static int numberOfDeadPlayers = 0;
    private final int numberOfPlayers;
    private Player player;

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    Dices dice1 = new Dices(1, 6);
    Dices dice2 = new Dices(1, 6);


    public Game(int numberOfPlayers) throws IOException {
        this.board = createBoard();
        this.isRoundOver = true;
        this.numberOfPlayers = numberOfPlayers;
        this.player = createPlayers();

    }

    public Player createPlayers() throws IOException {
        System.out.println("Write your Name to create a player");
        String name = in.readLine();
        return new Player(name);
    }

    public Positions[] createBoard() {

        board[0] = new Start("START!");
        board[1] = new Houses("Mediterranean-Avenue", "Brown", 60, 40);
        board[2] = new Mystery();
        board[3] = new Houses("Baltic-Avenue", "Brown", 60, 40);
        board[4] = new Tax("Income-Tax", 200);
        board[5] = new RailRoad("Reading-RailRoad");
        board[6] = new Houses("Oriental-Avenue", "Silver", 100, 80);
        board[7] = new Mystery();
        board[8] = new Houses("Vermont-Avenue", "Silver", 100, 80);
        board[9] = new Houses("Connecticut-Avenue", "Silver", 120, 100);
        board[10] = new Jail("Jail");
        board[11] = new Houses("St.Charles-Place", "Pink", 140, 120);
        board[12] = new Tax("Electric-Tax", 150);
        board[13] = new Houses("States-Avenue", "Pink", 140, 120);
        board[14] = new Houses("Virginia-Avenue", "Pink", 160, 140);
        board[15] = new RailRoad("Pennsylvania-RailRoad");
        board[16] = new Houses("St.James-Place", "Orange", 180, 160);
        board[17] = new Mystery();
        board[18] = new Houses("Tennessee-Avenue", "Orange", 180, 160);
        board[19] = new Houses("New York-Avenue", "Orange", 200, 180);
        board[20] = new FreeParking("Free-Parking");
        board[21] = new Houses("Kentucky-Avenue", "Red", 220, 200);
        board[22] = new Mystery();
        board[23] = new Houses("Indiana-Avenue", "Red", 220, 200);
        board[24] = new Houses("Illinois-Avenue", "Red", 240, 220);
        board[25] = new RailRoad("B.& O.-RailRoad");
        board[26] = new Houses("Atlantic-Avenue", "Yellow", 260, 240);
        board[27] = new Houses("Vent-nor-Avenue", "Yellow", 260, 240);
        board[28] = new Tax("Water-Tax", 150);
        board[29] = new Houses("Marvin-Gardens", "Yellow", 280, 260);
        board[30] = new GotoJail("Go to Jail!");
        board[31] = new Houses("Pacific-Avenue", "Green", 300, 280);
        board[32] = new Houses("North-Carolina-Avenue", "Green", 300, 280);
        board[33] = new Mystery();
        board[34] = new Houses("Pennsylvania-Avenue", "Green", 320, 300);
        board[35] = new RailRoad("Short-Line");
        board[36] = new Mystery();
        board[37] = new Houses("Park-Place", "Blue", 350, 325);
        board[38] = new Tax("Luxury-Tax", 100);
        board[39] = new Houses("Boardwalk", "Blue", 400, 350);
        return board;
    }


    public int rollDicesPlayer(Player player) throws IOException {

        if (isRoundOver) {
            while (true) {
                System.out.println("Press r to roll the dices");
                String input = in.readLine();

                if ("r".equalsIgnoreCase(input)) {
                    return rollDices();
                } else {
                    System.out.println("Invalid operation");
                }
            }
        }
        return 0;
    }


    private int rollDices() {
        int firstDice = dice1.getRandomNumber();
        int secondDice = dice2.getRandomNumber();

        int sum = firstDice + secondDice;

        System.out.println("Dice1: " + firstDice + " | " + "Dice2: " + secondDice);
        System.out.println("Sum: " + sum);

        return sum;
    }

    public void move(int diceValue, Player player) {
        int newPosition = player.getPosition() + diceValue;
        if (newPosition > 39) {
            int newNewPosition = newPosition - 40;
            player.setPosition(newNewPosition);
            collectFromCompletedRound(player);
            player.setLapCounter(player.getLapCounter() + 1);
            System.out.println("Your new position is: " + newNewPosition);

        } else {
            player.setPosition(newPosition);
            System.out.println("Your new position is: " + newPosition);
        }
    }

    public void collectFreeParkingMoney(Player player) {
        player.setBalance(player.getBalance() + gameWallet);
        gameWallet = 0;
        System.out.println(player.getName() + " Balance: " + player.getBalance());

    }

    public void collectFromCompletedRound(Player player) {
        player.setBalance(player.getBalance() + 200);
        System.out.println("You completed a Lap");
        System.out.println(player.getName() + " earned 200 €");
        player.setLapCounter(player.getLapCounter() + 1);
        System.out.println("You are in Lap: " + player.getLapCounter());
        System.out.println(player.getName() + " Balance: " + player.getBalance());

    }


    private void payRent(Player player) {
        int amount = ((Houses) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() - amount);
        ((Houses) board[player.getPosition()]).getOwner().setBalance(((Houses) board[player.getPosition()]).getOwner().getBalance() + amount);
        System.out.println(player.getName() + " payed " + amount + " to " + ((Houses) board[player.getPosition()]).getOwner().getName());
        System.out.println(player.getName() + " Balance: " + player.getBalance());
    }

    private void payMystery(Player player) {
        int amount = ((Mystery) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() - amount);
        gameWallet += amount;
        System.out.println(player.getName() + " payed: " + amount + " to the game!");
        System.out.println(player.getName() + " Balance: " + player.getBalance());

    }

    private void payTax(Player player) {
        int amount = ((Tax) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() - amount);
        gameWallet += amount;
        System.out.println(player.getName() + " payed: " + amount + " to the game!");
        System.out.println(player.getName() + " Balance: " + player.getBalance());

    }

    private void receiveMystery(Player player) {
        int amount = ((Mystery) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() + amount);
        System.out.println(player.getName() + " received: " + amount + " from the game!");
        System.out.println(player.getName() + " Balance: " + player.getBalance());

    }

    private void buy(Player player) throws IOException {
        if (checkIfCanBuy(player)) {
            ((Houses) board[player.getPosition()]).setOwned(true);
            player.getCardsOwned().add(board[player.getPosition()]);
            ((Houses) board[player.getPosition()]).setOwner(player);
            player.setBalance(player.getBalance() - ((Houses) board[player.getPosition()]).getBuyPrice());
            System.out.println(player.getName() + ": You have bought this property for : " + ((Houses) board[player.getPosition()]).getBuyPrice() + " € " );
            System.out.println(player.getName() + " Balance: " + player.getBalance() + " €");
            return;
        }
        System.out.println("You don't have enough money to buy this property");
        System.out.println(player.getBalance());
        event(player);
    }

   /* private void payBail() {
        if (player.isInJail()) {
            player.setBalance(player.getBalance() - 50);
            System.out.println("You pay 500 € to go out of jail");
            return;
        }
        System.out.println("You cant pay bail, because you are not in jail");
    }*/

    private void checkGoToJail(Player player) {

        if (player.getPosition() == 30) {
            player.setPosition(10);
            player.setInJail(true);
            System.out.println("You are in Jail");

        }
    }

    private boolean checkIfHasBalanceTax(Player player) {
        return player.getBalance() >= ((Tax) board[player.getPosition()]).getRentPrice();
    }

    private boolean checkIfHasBalanceMystery(Player player) {
        return player.getBalance() >= ((Mystery) board[player.getPosition()]).getRentPrice();
    }

    private boolean checkIfHasBalanceRent(Player player) {
        return player.getBalance() >= ((Houses) board[player.getPosition()]).getRentPrice();
    }

    private boolean checkIfCanBuy(Player player) {
        if (board[player.getPosition()] instanceof Houses) {
            return !((Houses) board[player.getPosition()]).isOwned() && player.getBalance() >= ((Houses) board[player.getPosition()]).getBuyPrice();
        }
        return false;
    }

    private void getPositionDetails(Player player) {
        if (board[player.getPosition()] instanceof Houses) {

            System.out.println("You got a House Card");
            System.out.println("Card name: " + board[player.getPosition()].getName());
            System.out.println("Card Color: " + ((Houses) board[player.getPosition()]).getColor());
            System.out.println("Card Buy Price: " + ((Houses) board[player.getPosition()]).getBuyPrice());
            System.out.println("Card Rent Price: " + ((Houses) board[player.getPosition()]).getRentPrice());
            return;
        }

        if (board[player.getPosition()] instanceof Tax) {

            System.out.println("You got a Tax Card");
            System.out.println("Card name: " + board[player.getPosition()].getName());
            System.out.println("Card Rent Price: " + ((Tax) board[player.getPosition()]).getRentPrice());
            return;
        }

        if (board[player.getPosition()] instanceof Mystery) {
            System.out.println("You got a Mystery Card");
            return;
        }
        if (board[player.getPosition()] instanceof Start) {
            System.out.println("You are in Starting position");
            return;
        }
        if (board[player.getPosition()] instanceof Jail) {
            System.out.println("You are visiting Jail");
            return;
        }
        if (board[player.getPosition()] instanceof FreeParking) {
            System.out.println("You are in Free Parking");
        }
    }

    public void event(Player player) throws IOException {

        checkGoToJail(player);

        if (board[player.getPosition()] instanceof Houses) {

            if (!((Houses) board[player.getPosition()]).isOwned()) {
                boolean validChoice = false;
                while (!validChoice) {
                    System.out.println("Do you want to buy the house?");
                    System.out.println("Press y to buy or press n to skip");
                    String input = in.readLine();

                    switch (input.toLowerCase()) {
                        case "y" -> {
                            buy(player);
                            validChoice = true;
                        }
                        case "n" -> validChoice = true;

                        //no-op
                        default -> System.out.println("Invalid operation");

                        //TODO error handling; maybe repeat question
                    }
                }
                return;
            }

            if (player != ((Houses) board[player.getPosition()]).getOwner()) {
                if (checkIfHasBalanceRent(player)) {
                    payRent(player);
                    return;
                }
                System.out.println("You don't have money to pay");
                player.setDead(true);
                System.out.println("You lost");
                numberOfDeadPlayers++;
                return;
            }
            System.out.println("You are in your own property");
            return;
        }

        if (board[player.getPosition()] instanceof Tax) {
            if (checkIfHasBalanceTax(player)) {
                payTax(player);
                return;
            }
            System.out.println("You don't have money to pay");
            player.setDead(true);
            System.out.println("You lost");
            numberOfDeadPlayers++;
            return;
        }

        if (board[player.getPosition()] instanceof Mystery) {
            switch (Mystery.generateRandomCard()) {
                case "Pay" -> {
                    if (checkIfHasBalanceMystery(player)) {
                        payMystery(player);
                        return;
                    }
                    System.out.println("You don't have money to pay");
                    player.setDead(true);
                    System.out.println("You lost");
                    numberOfDeadPlayers++;
                }
                case "Collect" -> receiveMystery(player);

            }

            if (player.getPosition() == 20) {
                getPositionDetails(player);
                collectFreeParkingMoney(player);
            }
        }
    }


    public void start() throws IOException {

        System.out.println("Welcome to Mindswaps's Monopoly");
        System.out.println(" ");

        while (!isGameOver()) {

            int diceValue = rollDicesPlayer(player);
            System.out.println(" ");
            if (!player.isInJail()) {
                move(diceValue, player);
                System.out.println(" ");
                getPositionDetails(player);
                System.out.println(" ");
                System.out.println(player.getName() + " Balance: " + player.getBalance() + " €");
                System.out.println(" ");
                event(player);
                isRoundOver = true;
                System.out.println("-----------------");
            } else {
                System.out.println("You cant play this round because you were arrested. Next round you can play");
                player.setInJail(false);
                isRoundOver = true;
            }
        }
    }

    private boolean isGameOver() {

        if (numberOfDeadPlayers == numberOfPlayers - 1) {
            System.out.println("Game is over");
            return true;

        }
        return false;
    }
}



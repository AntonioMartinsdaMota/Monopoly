package academy.mindswap;

import academy.mindswap.other.Dices;
import academy.mindswap.positions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable {

    private static int gameWallet = 0;
    private Positions[] board = new Positions[40];
    private boolean isRoundOver;
    private static int numberOfDeadPlayers = 0;
    private final int numberOfPlayers;
    private List<Player> playerList;
    private boolean isPlayerListFull;
    private int numberOfPlayersCreated;
    private boolean canContinue;

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    Dices dice1 = new Dices(1, 6);
    Dices dice2 = new Dices(1, 6);


    public Game(int numberOfPlayers) throws IOException {
        this.board = createBoard();
        this.isRoundOver = true;
        this.numberOfPlayers = numberOfPlayers;
        this.playerList = new ArrayList<>();


    }

    public List<Player> createPlayers() throws IOException {
        System.out.println("Write your Name to create a player");
        String name = in.readLine();
        playerList.add(new Player(name.toUpperCase()));

        if (playerList.size() == numberOfPlayers) {
            isPlayerListFull = true;
            return playerList;
        }

        return playerList;
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
                System.out.println("Press: r to roll the dices...");
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
        System.out.println("");
        System.out.println("Dice1: " + firstDice + "\n" + "Dice2: " + secondDice);
        System.out.println("You can move: " + sum + " positions");

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
        System.out.println("You got all the money from the free parking!!!!!!!");
        System.out.println("");
        System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");

    }

    public void collectFromCompletedRound(Player player) {
        player.setBalance(player.getBalance() + 200);
        System.out.println("You completed a Lap");
        System.out.println("@@@@@@@@@@@@@@@@@@@@");
        System.out.println(player.getName() + " earned 200 $");
        player.setLapCounter(player.getLapCounter() + 1);
        System.out.println("You are in Lap: " + player.getLapCounter());
        System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");

    }


    private void payRent(Player player) {
        int amount = ((Houses) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() - amount);
        ((Houses) board[player.getPosition()]).getOwner().setBalance(((Houses) board[player.getPosition()]).getOwner().getBalance() + amount);
        System.out.println("You are in a " + ((Houses) board[player.getPosition()]).getOwner().getName() + "'s property");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(player.getName() + " payed " + amount + " $ to " + ((Houses) board[player.getPosition()]).getOwner().getName());
        System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");
    }

    private void payMystery(Player player) {
        int amount = ((Mystery) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() - amount);
        gameWallet += amount;
        System.out.println("Card was: Oh, No ... You have to pay!");
        System.out.println("");
        System.out.println(player.getName() + " pay: " + amount + " to the game!");
        System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");
        System.out.println("Free parking balance is: " + gameWallet + " $");

    }

    private void payTax(Player player) {
        int amount = ((Tax) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() - amount);
        gameWallet += amount;
        System.out.println("You must pay your taxes!!!");
        System.out.println("");
        System.out.println(player.getName() + " pay: " + amount + " to the game!");
        System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");
        System.out.println("Free parking balance is: " + gameWallet + " $");

    }

    private void receiveMystery(Player player) {
        int amount = ((Mystery) board[player.getPosition()]).getRentPrice();
        player.setBalance(player.getBalance() + amount);
        System.out.println("Card was: It's your lucky day");
        System.out.println("");
        System.out.println(player.getName() + " received: " + amount + " from the game!");
        System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");

    }

    private void buy(Player player) throws IOException {
        if (checkIfCanBuy(player)) {
            ((Houses) board[player.getPosition()]).setOwned(true);
            player.getCardsOwned().add(board[player.getPosition()]);
            ((Houses) board[player.getPosition()]).setOwner(player);
            player.setBalance(player.getBalance() - ((Houses) board[player.getPosition()]).getBuyPrice());
            System.out.println(player.getName() + ": You have bought " + ((Houses) board[player.getPosition()]).getName() + " for " + ((Houses) board[player.getPosition()]).getBuyPrice() + " $");
            System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");
            return;
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("You don't have enough money to buy this property");
        System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");
        System.out.println("");
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
            System.out.println(player.getName() + " you are in Jail");
            System.out.println("You cant move for 1 round");
            System.out.println("You moved to position 10");

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

            System.out.println("You arrived at property...");
            System.out.println("");
            System.out.println("Name: " + board[player.getPosition()].getName());
            System.out.println("Color: " + ((Houses) board[player.getPosition()]).getColor());
            System.out.println("Buy Price: " + ((Houses) board[player.getPosition()]).getBuyPrice() + " $");
            System.out.println("Rent Price: " + ((Houses) board[player.getPosition()]).getRentPrice() + " $");
            return;
        }

        if (board[player.getPosition()] instanceof Tax) {

            System.out.println("You got a Tax Card...");
            System.out.println("");
            System.out.println("Name: " + board[player.getPosition()].getName());
            System.out.println("Tax Price: " + ((Tax) board[player.getPosition()]).getRentPrice() + " $");
            return;
        }

        if (board[player.getPosition()] instanceof Mystery) {
            System.out.println("You got a Mystery Card...");
            System.out.println("??????????????????????");
            return;
        }
        if (board[player.getPosition()] instanceof Start) {
            System.out.println("You are in START! position...");
            System.out.println("GO GO GO GO GO GO GO GO");
            return;
        }
        if (board[player.getPosition()] instanceof Jail) {
            System.out.println("You are visiting Jail...");
            System.out.println("[][][][][][][][][][][");;
            return;
        }
        if (board[player.getPosition()] instanceof FreeParking) {
            System.out.println("You are in Free Parking...");
            System.out.println("FREE CASH ?????????????");
        }
    }

    public void event(Player player) throws IOException {

        checkGoToJail(player);

        if (board[player.getPosition()] instanceof Houses) {

            if (!((Houses) board[player.getPosition()]).isOwned()) {
                boolean validChoice = false;
                while (!validChoice) {
                    System.out.println("Do you want to buy the house?");
                    System.out.println("Press: y to buy\nPress: s to skip");
                    String input = in.readLine();

                    switch (input.toLowerCase()) {
                        case "y" -> {
                            buy(player);
                            validChoice = true;
                        }
                        case "s" -> validChoice = true;

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
                System.out.println(":(");
                numberOfDeadPlayers++;
                playerList.remove(player);
                return;
            }
            System.out.println("You are at your own property...");
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
            System.out.println(":(");
            numberOfDeadPlayers++;
            playerList.remove(player);
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
                    System.out.println(":(");
                    numberOfDeadPlayers++;
                    playerList.remove(player);
                }
                case "Collect" -> receiveMystery(player);

            }

            if (player.getPosition() == 20) {
                getPositionDetails(player);
                collectFreeParkingMoney(player);
            }
        }
    }

    private void roundMaker() throws IOException {

        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            System.out.println("");
            System.out.println("--> " + player.getName() + " is Your turn <--");
            System.out.println("");
            while (!canContinue){
                otherEvent(player);
            }
            int diceValue = rollDicesPlayer(player);
            System.out.println(" ");
            if (!player.isInJail()) {
                move(diceValue, player);
                System.out.println(" ");
                getPositionDetails(player);
                System.out.println(" ");
                event(player);
                isRoundOver = true;
                System.out.println();
                System.out.println(player.getName() + " your turn is over");
                System.out.println("----------------------");
                System.out.println("");
                canContinue=false;

            } else {
                System.out.println("You cant play this round because you were arrested. Next round you can play");
                player.setInJail(false);
                isRoundOver = true;
                System.out.println(player.getName() + " your turn is over");
                System.out.println("----------------------");
                System.out.println("");

            }
        }
    }

    public void start() throws IOException {

        System.out.println("Welcome to Mindswaps's Monopoly");
        System.out.println("        !!!!!!!!!!!!!!!!!      ");

        while (!isPlayerListFull) {
            createPlayers();
        }
        System.out.println("");
        System.out.println("The Players are all ready");
        while (!isGameOver()) {
            roundMaker();
        }
    }

    private boolean isGameOver() {

        if (playerList.size() == 1) {
            System.out.println("Game is over");
            System.out.println("-_-_-_-_-_-_-");
            System.out.println("The winner is " + playerList.get(0).getName());
            System.out.println("######################################################");
            return true;

        }
        return false;
    }

    public void otherEvent(Player player) throws IOException {
        canContinue = false;
        boolean validChoice = false;
        while (!validChoice) {
            System.out.println("What is your next move?");
            System.out.println("Press: b to check your Balance... \nPress: l to check your Properties... \nPress: s to skip this Menu...");
            String input = in.readLine();
            switch (input.toLowerCase()) {
                case "b" -> {
                    System.out.println(player.getName() + " Balance: " + player.getBalance() + " $");
                    System.out.println("");
                    validChoice = true;
                    break;
                }
                case "l" -> {
                    System.out.println("You own the following properties:");
                    for (Positions p : player.getCardsOwned()) {
                        System.out.println(p.getName());
                        validChoice = true;
                    }
                    System.out.println("");
                }
                case "s" -> {
                    System.out.println("");
                    canContinue = true;
                    validChoice = true;

                }
                //no-op
                default -> System.out.println("Invalid operation");
            }
        }
    }


    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



package academy.mindswap.server;

import academy.mindswap.other.ColorCodes;
import academy.mindswap.other.Dices;
import academy.mindswap.positions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Game implements Runnable {

    private List<PlayerHandler> listOfPlayers;
    private final int PORT = 8081;
    private ServerSocket serverSocket;
    private ExecutorService service;
    private static int gameWallet = 0;
    private Positions[] board = new Positions[40];
    private boolean isRoundOver;
    private final int numberOfPlayers;
    private boolean canContinue;
    private Dices dice1;
    private Dices dice2;


    public Game(int numberOfPlayers) throws IOException {
        this.board = createBoard();
        this.numberOfPlayers = numberOfPlayers;
        this.dice1 = new Dices(1, 6);
        this.dice2 = new Dices(1, 6);
        this.listOfPlayers = new ArrayList<>();


    }


    /**
     * Server Launcher
     */

    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        System.out.println("started. Waiting for players to connect...");
        service = Executors.newCachedThreadPool();

        while (listOfPlayers.size() < numberOfPlayers) {
            Socket clientSocket = serverSocket.accept();
            PlayerHandler ph = new PlayerHandler(clientSocket);
            listOfPlayers.add(ph);
            service.submit(ph);
        }
        new Thread(this).start();
        broadcast(ColorCodes.RED_BACKGROUND + ColorCodes.WHITE + "The Players are all ready" + ColorCodes.RESET);
        broadcast(" ");

    }


    /**
     * Create Board
     */

    private Positions[] createBoard() {

        board[0] = new Start("START!");
        board[1] = new Houses("000", ColorCodes.YELLOW_BACKGROUND, 110, 100);
        board[2] = new Mystery();
        board[3] = new Houses("001", ColorCodes.YELLOW_BACKGROUND, 110, 100);
        board[4] = new Tax("Income-Tax", 200);
        board[5] = new RailRoad("002");
        board[6] = new Houses("003", ColorCodes.BLACK_BACKGROUND_BRIGHT, 120, 100);
        board[7] = new Mystery();
        board[8] = new Houses("004", ColorCodes.BLACK_BACKGROUND_BRIGHT, 120, 100);
        board[9] = new Houses("005", ColorCodes.BLACK_BACKGROUND_BRIGHT, 140, 120);
        board[10] = new Jail("Jail");
        board[11] = new Houses("006", ColorCodes.RED_BACKGROUND_BRIGHT, 160, 140);
        board[12] = new Tax("Electric-Tax", 150);
        board[13] = new Houses("007", ColorCodes.GREEN_BACKGROUND_BRIGHT, 160, 140);
        board[14] = new Houses("008", ColorCodes.GREEN_BACKGROUND_BRIGHT, 180, 160);
        board[15] = new RailRoad("009");
        board[16] = new Houses("010", ColorCodes.CYAN_BACKGROUND, 200, 180);
        board[17] = new Mystery();
        board[18] = new Houses("011", ColorCodes.CYAN_BACKGROUND, 200, 180);
        board[19] = new Houses("012", ColorCodes.CYAN_BACKGROUND, 220, 200);
        board[20] = new FreeParking("Free-Parking");
        board[21] = new Houses("013", ColorCodes.RED_BACKGROUND, 240, 220);
        board[22] = new Mystery();
        board[23] = new Houses("014", ColorCodes.RED_BACKGROUND, 240, 220);
        board[24] = new Houses("015", ColorCodes.RED_BACKGROUND, 260, 240);
        board[25] = new RailRoad("016");
        board[26] = new Houses("017", ColorCodes.YELLOW_BACKGROUND_BRIGHT, 280, 260);
        board[27] = new Houses("018", ColorCodes.YELLOW_BACKGROUND_BRIGHT, 280, 260);
        board[28] = new Tax("Water-Tax", 150);
        board[29] = new Houses("019", ColorCodes.YELLOW_BACKGROUND_BRIGHT, 300, 280);
        board[30] = new GotoJail("Go -> Jail!");
        board[31] = new Houses("020", ColorCodes.GREEN_BACKGROUND, 320, 280);
        board[32] = new Houses("021", ColorCodes.GREEN_BACKGROUND, 320, 280);
        board[33] = new Mystery();
        board[34] = new Houses("022", ColorCodes.GREEN_BACKGROUND, 340, 320);
        board[35] = new RailRoad("023");
        board[36] = new Mystery();
        board[37] = new Houses("024", ColorCodes.BLUE_BACKGROUND, 400, 350);
        board[38] = new Tax("Luxury-Tax", 100);
        board[39] = new Houses("025", ColorCodes.BLUE_BACKGROUND, 500, 450);
        return board;
    }


    /**
     * Earn Methods
     */

    private void collectFreeParkingMoney(PlayerHandler playerHandler) {
        playerHandler.balance += gameWallet;
        gameWallet = 0;
        System.out.println(playerHandler.name + " got all the money from the free parking!!!!!!!");
        playerHandler.sendMessage("You got all the money from the free parking!!!!!!!");
        playerHandler.sendMessage("");
        playerHandler.sendMessage(" Your balance is: " + playerHandler.balance + " $");

    }

    private void collectFromCompletedRound(PlayerHandler playerHandler) {
        playerHandler.balance += 200;
        playerHandler.sendMessage("You completed a Lap");
        playerHandler.sendMessage("@@@@@@@@@@@@@@@@@@@@");
        System.out.println(playerHandler.name + " earned 200 $");
        playerHandler.sendMessage("You earned 200 $");
        playerHandler.lapCounter += 1;
        System.out.println("You are in Lap: " + playerHandler.lapCounter);
        playerHandler.sendMessage(" Your balance is: " + playerHandler.balance + " $");

    }


    /**
     * Pay Methods
     */

    private void payRent(PlayerHandler playerHandler) {
        int amount = ((Houses) board[playerHandler.position]).getRentPrice();

        playerHandler.balance -= amount;

        ((Houses) board[playerHandler.position]).getOwner().setBalance(((Houses) board[playerHandler.position]).getOwner().getBalance() + amount);

        playerHandler.sendMessage("");
        playerHandler.sendMessage("You are at " + ((Houses) board[playerHandler.position]).getOwner().name + "'s property");

        ((Houses) board[playerHandler.position]).getOwner().sendMessage(playerHandler.name + " is in your property");
        playerHandler.sendMessage("");
        playerHandler.sendMessage("You pay: " + amount + " $ to " + ((Houses) board[playerHandler.position]).getOwner().name);
        ((Houses) board[playerHandler.position]).getOwner().sendMessage(playerHandler.name + " pay you " + amount + " $");
        ((Houses) board[playerHandler.position]).getOwner().sendMessage("");

        //playerHandler.sendMessage(playerHandler.name + " Balance: " + playerHandler.balance + " $");
    }

    private void payMystery(PlayerHandler playerHandler) {
        int amount = ((Mystery) board[playerHandler.position]).getRentPrice();
        playerHandler.balance -= amount;
        gameWallet += amount;
        playerHandler.sendMessage("");
        playerHandler.sendMessage("Oh, No!!!You have to pay!");
        playerHandler.sendMessage("");
        playerHandler.sendMessage("You pay: " + amount);

    }

    private void payTax(PlayerHandler playerHandler) {
        int amount = ((Tax) board[playerHandler.position]).getRentPrice();
        playerHandler.balance -= amount;
        gameWallet += amount;

        playerHandler.sendMessage("");
        playerHandler.sendMessage("You must pay your taxes!!!");
        playerHandler.sendMessage("");
        playerHandler.sendMessage("You pay: " + amount);

    }

    private void buy(PlayerHandler playerHandler){

        if (checkIfCanBuy(playerHandler)) {
            ((Houses) board[playerHandler.position]).setOwned(true);
            ((Houses) board[playerHandler.position]).setOwner(playerHandler);
            playerHandler.cardsOwned.add(board[playerHandler.position]);
            playerHandler.balance -= ((Houses) board[playerHandler.position]).getBuyPrice();
            playerHandler.sendMessage("You have bought " +
                    "\"" + board[playerHandler.position].getName() + "\"" + " for " +
                    ((Houses) board[playerHandler.position]).getBuyPrice() + " $");
            playerHandler.sendMessage("");

            return;
        }
        playerHandler.sendMessage("                   !!!ALERT!!!                  ");
        playerHandler.sendMessage("You don't have enough money to buy this property");
        playerHandler.sendMessage("");

        event(playerHandler);
    }

    private void receiveMystery(PlayerHandler playerHandler) {
        int amount = ((Mystery) board[playerHandler.position]).getRentPrice();
        playerHandler.balance += amount;
        playerHandler.sendMessage(" ");
        playerHandler.sendMessage("Yes! It's your lucky day!!");
        playerHandler.sendMessage("");
        playerHandler.sendMessage("You received: " + amount);

    }


    /**
     * Checking Methods
     */

    private void checkGoToJail(PlayerHandler playerHandler) {

        if (playerHandler.position == 30) {
            playerHandler.position = 10;
            playerHandler.isInJail = true;
            playerHandler.sendMessage(playerHandler.name + " you are in Jail");
            playerHandler.sendMessage("You cant move for 1 round");
            playerHandler.sendMessage("You moved to playerHandler.position 10");

        }
    }

    private boolean checkIfHasBalanceTax(PlayerHandler playerHandler) {
        return playerHandler.balance >= ((Tax) board[playerHandler.position]).getRentPrice();
    }

    private boolean checkIfHasBalanceMystery(PlayerHandler playerHandler) {
        return playerHandler.balance >= ((Mystery) board[playerHandler.position]).getRentPrice();
    }

    private boolean checkIfHasBalanceRent(PlayerHandler playerHandler) {
        return playerHandler.balance >= ((Houses) board[playerHandler.position]).getRentPrice();
    }

    private boolean checkIfCanBuy(PlayerHandler playerHandler) {
        if (board[playerHandler.position] instanceof Houses) {
            return !((Houses) board[playerHandler.position]).isOwned() && playerHandler.balance >= ((Houses) board[playerHandler.position]).getBuyPrice();
        }
        return false;
    }

    private boolean isGameOver() {

        if (listOfPlayers.size() == 1) {
            System.out.println("Game is over");
            System.out.println("-_-_-_-_-_-_-");
            System.out.println("The winner is " + listOfPlayers.get(0).name);
            System.out.println("######################################################");
            broadcast("Game is over");
            broadcast("-_-_-_-_-_-_-");
            broadcast("The winner is " + listOfPlayers.get(0).name);
            broadcast("######################################################");

            return true;

        }
        return false;
    }


    /**
     * Properties Methods
     */
    private void getPositionDetails(PlayerHandler playerHandler) {

        if (board[playerHandler.position] instanceof Houses) {

            String color = ((Houses) board[playerHandler.position]).getColor();
            String nam = board[playerHandler.position].getName();
            int buy = ((Houses) board[playerHandler.position]).getBuyPrice();
            int pay = ((Houses) board[playerHandler.position]).getRentPrice();
            String i = "Buy: " + buy + " $";
            String j = "Pay: " + pay + " $";

            String a = color + ColorCodes.BLACK_BOLD + "         HOUSE        " + ColorCodes.RESET;
            String b = color + ColorCodes.BLACK_UNDERLINED + "          " + nam + "         " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + "                      " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "      " + i + "      " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "      " + j + "      " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + "                      " + ColorCodes.RESET;

            playerHandler.sendMessage(a);
            playerHandler.sendMessage(b);
            playerHandler.sendMessage(c);
            playerHandler.sendMessage(c);
            playerHandler.sendMessage(d);
            playerHandler.sendMessage(e);
            playerHandler.sendMessage(f);
            playerHandler.sendMessage(f);

            return;
        }

        if (board[playerHandler.position] instanceof Tax) {

            String k = ((Tax) board[playerHandler.position]).getRentPrice() + " $";

            String a = ColorCodes.BLACK_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "      TAX CARD      " + ColorCodes.RESET;
            String h = ColorCodes.BLACK_BACKGROUND_BRIGHT + ColorCodes.BLACK_UNDERLINED + "                    " + ColorCodes.RESET;
            String b = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.RED_BOLD +                         "      Pay: " + k + "    " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD +                         "  _____ _   __  __  " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD +                         " |_   _/_\\  \\ \\/ /  " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD +                       "   | |/ _ \\  >  <   " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD +                         "   |_/_/ \\_\\/_/\\_\\  " + ColorCodes.RESET;
            String g = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD +                         "                    " + ColorCodes.RESET;


            playerHandler.sendMessage(a);
            playerHandler.sendMessage(h);
            playerHandler.sendMessage(b);
            playerHandler.sendMessage(c);
            playerHandler.sendMessage(d);
            playerHandler.sendMessage(e);
            playerHandler.sendMessage(f);
            playerHandler.sendMessage(g);

            playerHandler.sendMessage("");
            playerHandler.sendMessage("Name: " + board[playerHandler.position].getName());

            return;
        }

        if (board[playerHandler.position] instanceof Mystery) {

            String a = ColorCodes.PURPLE_BACKGROUND + ColorCodes.BLACK_BOLD +        "    MYSTERY CARD    " + ColorCodes.RESET;
            String b = ColorCodes.PURPLE_BACKGROUND + ColorCodes.BLACK_UNDERLINED +  "                    " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.PURPLE_BOLD + "        ####        " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.PURPLE_BOLD + "       ##  ##       " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.PURPLE_BOLD + "          ##        " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.PURPLE_BOLD + "         ##         " + ColorCodes.RESET;
            String g = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.PURPLE_BOLD + "                    " + ColorCodes.RESET;
            String h = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.PURPLE_BOLD + "         ##         " + ColorCodes.RESET;
            String i = ColorCodes.WHITE_BACKGROUND_BRIGHT +                          "                    " + ColorCodes.RESET;

            playerHandler.sendMessage(a);
            playerHandler.sendMessage(b);
            playerHandler.sendMessage(c);
            playerHandler.sendMessage(d);
            playerHandler.sendMessage(e);
            playerHandler.sendMessage(f);
            playerHandler.sendMessage(g);
            playerHandler.sendMessage(h);
            playerHandler.sendMessage(i);


            return;
        }
        if (board[playerHandler.position] instanceof Start) {
            playerHandler.sendMessage("You are in START! position...");
            playerHandler.sendMessage("GO GO GO GO GO GO GO GO");
            playerHandler.sendMessage("");
            return;
        }
        if (board[playerHandler.position] instanceof Jail) {
            playerHandler.sendMessage("You are visiting Jail...");
            playerHandler.sendMessage("[][][][][][][][][][][");
            playerHandler.sendMessage("");
            ;
            return;
        }
        if (board[playerHandler.position] instanceof FreeParking) {
            playerHandler.sendMessage("You are in Free Parking...");
            playerHandler.sendMessage("FREE CASH ?????????????");
            playerHandler.sendMessage("");
        }
    }

    private void resetProperties(PlayerHandler playerHandler) {
        for (int i = 0; i < playerHandler.cardsOwned.size(); i++) {
            ((Houses) playerHandler.cardsOwned.get(i)).setOwned(false);
            playerHandler.cardsOwned.remove(i);
            System.out.println(playerHandler.name + " left the game");
        }
    }


    /**
     * Events
     */


    public synchronized int rollDicesPlayer(PlayerHandler playerHandler) {

        if (!playerHandler.roundCompleted) {
            playerHandler.sendMessage("R -> roll the dices");

            try {
                if ("r".equalsIgnoreCase(playerHandler.in.readLine())) {
                    return rollDices(playerHandler);
                } else {
                    playerHandler.sendMessage("Invalid operation");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private int rollDices(PlayerHandler playerHandler) {
        synchronized (dice1) {
            synchronized (dice2) {
                int firstDice = dice1.getRandomNumber();
                int secondDice = dice2.getRandomNumber();
                int sum = firstDice + secondDice;
                playerHandler.sendMessage("");
                playerHandler.sendMessage("Dice1: " + firstDice + "\n" + "Dice2: " + secondDice);
                return sum;
            }
        }
    }

    public void move(int diceValue, PlayerHandler playerHandler) {
        int newPosition = playerHandler.position + diceValue;
        if (newPosition > 39) {
            int newNewPosition = newPosition - 40;
            playerHandler.position = newNewPosition;
            collectFromCompletedRound(playerHandler);
            playerHandler.lapCounter += 1;
            playerHandler.sendMessage("You moved to position: " + newNewPosition);
            playerHandler.sendMessage("");

        } else {
            playerHandler.position = newPosition;
            playerHandler.sendMessage("You moved to position: " + newPosition);
            playerHandler.sendMessage("");
        }
    }

    public void event(PlayerHandler playerHandler) {

        checkGoToJail(playerHandler);

        if (board[playerHandler.position] instanceof Houses) {

            if (!((Houses) board[playerHandler.position]).isOwned()) {
                boolean validChoice = false;
                while (!validChoice) {
                    playerHandler.sendMessage("");
                    playerHandler.sendMessage("Do you want to buy the house?");
                    playerHandler.sendMessage("   Y -> YES   ||   N -> NO   ");

                    String input = null;
                    try {
                        input = playerHandler.in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    switch (input.toLowerCase()) {
                        case "y" -> {
                            buy(playerHandler);
                            validChoice = true;
                        }
                        case "n" -> validChoice = true;

//no-op
                        default -> playerHandler.sendMessage("Invalid operation");

                        //TODO error handling; maybe repeat question
                    }
                }
                return;
            }


            if (playerHandler.cardsOwned.contains(board[playerHandler.position])) {
                playerHandler.sendMessage("You are at your own property...");
                playerHandler.sendMessage("");
                return;
            }

            if (checkIfHasBalanceRent(playerHandler)) {
                payRent(playerHandler);
                return;
            }

            playerHandler.sendMessage("You don't have money to pay");
            playerHandler.isDead = true;
            resetProperties(playerHandler);
            playerHandler.sendMessage("You lost");
            playerHandler.sendMessage(":(");
            broadcast(playerHandler.name + " Left the game");
            listOfPlayers.remove(playerHandler);
            return;
        }
        if (board[playerHandler.position] instanceof Tax) {
            if (checkIfHasBalanceTax(playerHandler)) {
                payTax(playerHandler);
                return;
            }
            playerHandler.sendMessage("You don't have money to pay");
            playerHandler.isDead = true;
            resetProperties(playerHandler);
            playerHandler.sendMessage("You lost");
            playerHandler.sendMessage(":(");
            broadcast(playerHandler.name + " Left the game");
            listOfPlayers.remove(playerHandler);
            return;
        }

        if (board[playerHandler.position] instanceof Mystery) {
            switch (Mystery.generateRandomCard()) {
                case "Pay" -> {
                    if (checkIfHasBalanceMystery(playerHandler)) {
                        payMystery(playerHandler);
                        return;
                    }
                    System.out.println("You don't have money to pay");
                    playerHandler.isDead = true;
                    resetProperties(playerHandler);
                    playerHandler.sendMessage("You lost");
                    playerHandler.sendMessage(":(");
                    broadcast(playerHandler.name + " Left the game");
                    listOfPlayers.remove(playerHandler);
                }
                case "Collect" -> receiveMystery(playerHandler);

            }

            if (playerHandler.position == 20) {
                getPositionDetails(playerHandler);
                collectFreeParkingMoney(playerHandler);
            }
        }
    }

    public void otherEvent(PlayerHandler playerHandler) throws IOException {

        canContinue = false;

        while (!canContinue) {
            playerHandler.sendMessage("What is your next move?");
            playerHandler.sendMessage("S -> SKIP MENU || B -> BALANCE || L -> VIEW PROPERTIES ");
            String input = playerHandler.in.readLine();
            switch (input.toLowerCase()) {
                case "b" -> {
                    playerHandler.sendMessage("");
                    playerHandler.sendMessage("Your balance is: " + playerHandler.balance + " $");
                    playerHandler.sendMessage("");
                }
                case "l" -> {
                    playerHandler.sendMessage("You own the following properties:");

                    if (!playerHandler.cardsOwned.isEmpty()) {
                        for (Positions p : playerHandler.cardsOwned) {
                            playerHandler.sendMessage(p.getName());
                        }
                    }
                    playerHandler.sendMessage("");
                }
                case "s" -> {
                    playerHandler.sendMessage("");
                    canContinue = true;
                }

//no-op
                default -> playerHandler.sendMessage("Invalid operation");
            }

        }

    }


    /**
     * Round Maker
     */

    public void roundMaker(PlayerHandler playerHandler) throws IOException {

        playerHandler.sendMessage("--> " + playerHandler.name + " is Your turn <--");
        playerHandler.sendMessage("");
        System.out.println(playerHandler.name + " is playing...");

        for (PlayerHandler ph : listOfPlayers) {
            if (ph != playerHandler) {
                ph.sendMessage(playerHandler.name + " is playing...");
                ph.sendMessage("");
            }
        }

        otherEvent(playerHandler);

        if (!playerHandler.isDead) {
            int diceValue = rollDicesPlayer(playerHandler);
            playerHandler.sendMessage(" ");
            if (!playerHandler.isInJail) {

                move(diceValue, playerHandler);

                getPositionDetails(playerHandler);

                event(playerHandler);

                isRoundOver = true;
                playerHandler.sendMessage("");
                playerHandler.sendMessage(playerHandler.name + " your turn is over");
                playerHandler.sendMessage("----------------------");
                playerHandler.sendMessage("");
                for (PlayerHandler ph : listOfPlayers) {
                    if (ph != playerHandler) {
                        ph.sendMessage(playerHandler.name + " finished his turn");
                        ph.sendMessage("");
                    }
                }
                System.out.println(playerHandler.name + " finished his turn");
                System.out.println("");
                //canContinue = false;

            } else {
                System.out.println("You cant play this round because you were arrested. Next round you can play");
                System.out.println("");
                playerHandler.isInJail = false;
                isRoundOver = true;
                playerHandler.sendMessage("");
                playerHandler.sendMessage(playerHandler.name + " your turn is over");
                playerHandler.sendMessage("----------------------");
                playerHandler.sendMessage("");
                for (PlayerHandler ph : listOfPlayers) {
                    if (ph != playerHandler) {
                        ph.sendMessage(playerHandler.name + " finished his turn");
                        ph.sendMessage("");
                    }
                }
            }
        }
    }


    /**
     * Run
     */


    @Override
    public void run() {
        System.out.println("");
        System.out.println("Game started");
        broadcast("Game started");
        broadcast("");
        System.out.println("");
        while (!isGameOver()) {
            for (int i = 0; i < listOfPlayers.size(); i++) {
                try {
                    listOfPlayers.get(i).roundCompleted = false;
                    roundMaker(listOfPlayers.get(i));
                    listOfPlayers.get(i).roundCompleted = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * Messages
     */


    public void broadcast(String message) {
        for (PlayerHandler ph : listOfPlayers) {
            ph.sendMessage(message);
        }
    }


    /**
     * #############################################
     */


    public class PlayerHandler extends Player implements Runnable {


        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;
        private String name;
        private int balance;
        private int position;
        private boolean isInJail;
        private boolean isDead;
        private List<Positions> cardsOwned;
        private int lapCounter;
        private boolean roundCompleted;


        public PlayerHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.balance = 1500;
            this.position = 0;
            this.cardsOwned = new LinkedList<>();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = generateName();
        }

        public String generateName() throws IOException {
            sendMessage("");
            sendMessage("Welcome to Mindswap's Monopoly");
            sendMessage("");
            sendMessage("Write your Username");
            String name = in.readLine();
            return name;
        }

        private void sendMessage(String message) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        @Override
        public void run() {

        }
    }

}






package academy.mindswap.server;

import academy.mindswap.other.ColorCodes;
import academy.mindswap.other.Dices;
import academy.mindswap.positions.*;

import javax.swing.plaf.TreeUI;
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
    private boolean isGameOver;


    public Game(int numberOfPlayers) throws IOException {
        this.board = createBoard();
        this.numberOfPlayers = numberOfPlayers;
        this.dice1 = new Dices(1, 6);
        this.dice2 = new Dices(1, 6);
        this.listOfPlayers = new ArrayList<>();
        this.isGameOver = false;


    }

    /**
     * Server Launcher
     */

    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
        System.out.println("");
        System.out.println("Server Started. Waiting for players to connect...");
        service = Executors.newCachedThreadPool();

        while (listOfPlayers.size() < numberOfPlayers) {
            Socket clientSocket = serverSocket.accept();
            PlayerHandler ph = new PlayerHandler(clientSocket);
            listOfPlayers.add(ph);
            service.submit(ph);
        }
        new Thread(this).start();
        System.out.println(" ");
        System.out.println(ColorCodes.RED_BACKGROUND + ColorCodes.WHITE + "The Players are all ready" + ColorCodes.RESET);
        System.out.println(" ");

    }

    /**
     * Create Board
     */

    private Positions[] createBoard() {

        board[0] = new Start("START!");
        board[1] = new Houses(" Rua do Tejos ", ColorCodes.YELLOW_BACKGROUND, 110, 100);
        board[2] = new Mystery();
        board[3] = new Houses(" Rua do Trevo ", ColorCodes.YELLOW_BACKGROUND, 110, 100);
        board[4] = new Tax("Income-Tax", 200);
        board[5] = new RailRoad("  Rua do Tua  ");
        board[6] = new Houses("  Rua do Vez  ", ColorCodes.BLACK_BACKGROUND_BRIGHT, 120, 100);
        board[7] = new Mystery();
        board[8] = new Houses(" Rua do Vouga ", ColorCodes.BLACK_BACKGROUND_BRIGHT, 120, 100);
        board[9] = new Houses(" Rua do Zaire ", ColorCodes.BLACK_BACKGROUND_BRIGHT, 140, 120);
        board[10] = new Jail("Jail");
        board[11] = new Houses("Rua do Zambeze", ColorCodes.RED_BACKGROUND_BRIGHT, 160, 140);
        board[12] = new Tax("Electric-Tax", 150);
        board[13] = new Houses("Rua do Zêzeros", ColorCodes.GREEN_BACKGROUND_BRIGHT, 160, 140);
        board[14] = new Houses(" Rua do Leite ", ColorCodes.GREEN_BACKGROUND_BRIGHT, 180, 160);
        board[15] = new RailRoad(" Rua do Lírio ");
        board[16] = new Houses("Rua do Covelos", ColorCodes.CYAN_BACKGROUND, 200, 180);
        board[17] = new Mystery();
        board[18] = new Houses(" Rua do Paiol ", ColorCodes.CYAN_BACKGROUND, 200, 180);
        board[19] = new Houses("Rua do Regados", ColorCodes.CYAN_BACKGROUND, 220, 200);
        board[20] = new FreeParking("Free-Parking");
        board[21] = new Houses("Rua do Ribeiro", ColorCodes.RED_BACKGROUND, 240, 220);
        board[22] = new Mystery();
        board[23] = new Houses("  Rua do Rio  ", ColorCodes.RED_BACKGROUND, 240, 220);
        board[24] = new Houses("Rua do Velosos", ColorCodes.RED_BACKGROUND, 260, 240);
        board[25] = new RailRoad("Rua da Bataria");
        board[26] = new Houses(" Rua da China ", ColorCodes.YELLOW_BACKGROUND_BRIGHT, 280, 260);
        board[27] = new Houses(" Rua da Lomba ", ColorCodes.YELLOW_BACKGROUND_BRIGHT, 280, 260);
        board[28] = new Tax("Water-Tax", 150);
        board[29] = new Houses("Rua da Formiga", ColorCodes.YELLOW_BACKGROUND_BRIGHT, 300, 280);
        board[30] = new GotoJail("Go -> Jail!");
        board[31] = new Houses(" Rua da Póvoa ", ColorCodes.GREEN_BACKGROUND, 320, 280);
        board[32] = new Houses("Rua de Naulila", ColorCodes.GREEN_BACKGROUND, 320, 280);
        board[33] = new Mystery();
        board[34] = new Houses(" Rua de Godim ", ColorCodes.GREEN_BACKGROUND, 340, 320);
        board[35] = new RailRoad("Rua da Alegria");
        board[36] = new Mystery();
        board[37] = new Houses("Rua da Barrosa", ColorCodes.BLUE_BACKGROUND, 400, 350);
        board[38] = new Tax("Luxury-Tax", 100);
        board[39] = new Houses("Rua da Belinha", ColorCodes.BLUE_BACKGROUND, 500, 450);
        return board;
    }


    /**
     * Earn Methods
     */

    private void collectFreeParkingMoney(PlayerHandler playerHandler) {
        playerHandler.balance += gameWallet;
        playerHandler.sendMessage(ColorCodes.GREEN_BOLD + "You earned " + gameWallet + " from free parking" + ColorCodes.RESET);
        broadcastOthers(playerHandler.name + "earned" + gameWallet + " from free parking", playerHandler);
        playerHandler.sendMessage("");
        broadcastOthers("", playerHandler);

    }

    private void collectFromCompletedRound(PlayerHandler playerHandler) {
        playerHandler.balance += 200;
        playerHandler.sendMessage("You completed a Lap");
        broadcastOthers(playerHandler.name + " completed a Lap and earned 200 $", playerHandler);
        playerHandler.sendMessage("");
        playerHandler.sendMessage(ColorCodes.GREEN_BOLD + "You earned 200 $" + ColorCodes.RESET);
    }


    /**
     * Pay Methods
     */

    private void payRent(PlayerHandler playerHandler) {
        int amount = ((Houses) board[playerHandler.position]).getRentPrice();

        playerHandler.balance -= amount;

        ((Houses) board[playerHandler.position]).getOwner().balance = (((Houses) board[playerHandler.position]).getOwner().balance + amount);

        playerHandler.sendMessage("");
        playerHandler.sendMessage("You are at " + ((Houses) board[playerHandler.position]).getOwner().name + "'s property");

        ((Houses) board[playerHandler.position]).getOwner().sendMessage(playerHandler.name + " is in your property");
        playerHandler.sendMessage("");
        playerHandler.sendMessage(ColorCodes.RED_BOLD + "You pay: " + amount + " $ to " + ((Houses) board[playerHandler.position]).getOwner().name + ColorCodes.RESET);
        ((Houses) board[playerHandler.position]).getOwner().sendMessage(ColorCodes.GREEN_BOLD + playerHandler.name + " pay you " + amount + " $" + ColorCodes.RESET);
        broadcastOthers(playerHandler.name + " pay: " + amount + " $ to " + ((Houses) board[playerHandler.position]).getOwner().name, playerHandler);
        //((Houses) board[playerHandler.position]).getOwner().sendMessage("");

        //playerHandler.sendMessage(playerHandler.name + " Balance: " + playerHandler.balance + " $");
    }

    private void payMystery(PlayerHandler playerHandler) {
        int amount = ((Mystery) board[playerHandler.position]).getRentPrice();
        playerHandler.balance -= amount;
        gameWallet += amount;
        playerHandler.sendMessage("");
        playerHandler.sendMessage("Oh, No!!!You have to pay!");
        playerHandler.sendMessage("");
        playerHandler.sendMessage(ColorCodes.RED_BOLD + "You pay: " + amount + " $" + ColorCodes.RESET);
        broadcastOthers(playerHandler.name + " pay " + amount + " $ from a Mystery Card", playerHandler);

    }

    private void payTax(PlayerHandler playerHandler) {
        int amount = ((Tax) board[playerHandler.position]).getRentPrice();
        playerHandler.balance -= amount;
        gameWallet += amount;

        playerHandler.sendMessage("");
        playerHandler.sendMessage("You must pay your taxes!!!");
        playerHandler.sendMessage("");
        playerHandler.sendMessage(ColorCodes.RED_BOLD + "You pay: " + amount + " $" + ColorCodes.RESET);
        broadcastOthers(playerHandler.name + " pay " + amount + " $ from a Tax Card", playerHandler);
    }

    private void buy(PlayerHandler playerHandler) {

        if (checkIfCanBuy(playerHandler)) {
            ((Houses) board[playerHandler.position]).setOwned(true);
            ((Houses) board[playerHandler.position]).setOwner(playerHandler);
            playerHandler.cardsOwned.add(board[playerHandler.position]);
            playerHandler.balance -= ((Houses) board[playerHandler.position]).getBuyPrice();
            playerHandler.sendMessage("You have bought " +
                    "\"" + board[playerHandler.position].getName() + "\"" + " for " +
                    ((Houses) board[playerHandler.position]).getBuyPrice() + " $");
            playerHandler.sendMessage("");
            broadcastOthers(playerHandler.name + " bought " +
                    "\"" + board[playerHandler.position].getName() + "\"" + " for " +
                    ((Houses) board[playerHandler.position]).getBuyPrice() + " $", playerHandler);

            return;
        }
        playerHandler.sendMessage(ColorCodes.YELLOW_BOLD + "                   !!!ALERT!!!                  " + ColorCodes.RESET);
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
        playerHandler.sendMessage(ColorCodes.GREEN_BOLD + "You received: " + amount + " $" + ColorCodes.RESET);
        broadcastOthers(playerHandler.name + " received " + amount + " $ from a Mystery Card", playerHandler);

    }


    /**
     * Checking Methods
     */

    private void checkGoToJail(PlayerHandler playerHandler) {

        if (playerHandler.position == 30) {
            playerHandler.position = 10;
            playerHandler.isInJail = true;

            String a = ColorCodes.BLUE_BACKGROUND + ColorCodes.BLACK_UNDERLINED + "      GO TO JAIL     " + ColorCodes.RESET;
            String b = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "                     " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "    __  _.-\"` `'-.   " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   /||\\'._ __{}_(    " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   ||||  |'--.__\\    " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   |  L.(   ^_\\^     " + ColorCodes.RESET;
            String g = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   \\ .-' |   _ |     " + ColorCodes.RESET;
            String h = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   | |   )\\___/      " + ColorCodes.RESET;
            String i = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   |  \\-'`:._]       " + ColorCodes.RESET;
            String j = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   \\__/;      '-.    " + ColorCodes.RESET;


            playerHandler.sendMessage(a);
            playerHandler.sendMessage(b);
            playerHandler.sendMessage(c);
            playerHandler.sendMessage(d);
            playerHandler.sendMessage(e);
            playerHandler.sendMessage(f);
            playerHandler.sendMessage(g);
            playerHandler.sendMessage(h);
            playerHandler.sendMessage(i);
            playerHandler.sendMessage(j);

            playerHandler.sendMessage("");

            playerHandler.sendMessage("You are in Jail");
            broadcastOthers(playerHandler.name + " is in Jail", playerHandler);
            playerHandler.sendMessage("You can't move for 1 round");

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

    private void gameOver() {

        System.out.println(ColorCodes.RED + "The Game is Over" + ColorCodes.RESET);

        System.out.println(ColorCodes.GREEN_BOLD + "The winner is: " + listOfPlayers.get(0).name + ColorCodes.RESET);
        broadcast(ColorCodes.GREEN_BOLD + "The winner is: " + listOfPlayers.get(0).name + ColorCodes.RESET);

        broadcast("");
        broadcast(ColorCodes.RED_BOLD + "Game is over" + ColorCodes.RESET);
        broadcast("");
        broadcast(ColorCodes.GREEN_BOLD + " __  __                   _    \n" +
                " \\ \\/ /__  __ __  _    __(_)__ \n" +
                "  \\  / _ \\/ // / | |/|/ / / _ \\\n" +
                "  /_/\\___/\\_,_/  |__,__/_/_//_/" + ColorCodes.RESET);
        isGameOver = true;


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
            String b = color + ColorCodes.BLACK_UNDERLINED + "    " + nam + "    " + ColorCodes.RESET;
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
            String b = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.RED_BOLD + "      Pay: " + k + "    " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "  _____ _   __  __  " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " |_   _/_\\  \\ \\/ /  " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   | |/ _ \\  >  <   " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   |_/_/ \\_\\/_/\\_\\  " + ColorCodes.RESET;
            String g = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "                    " + ColorCodes.RESET;


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

            String a = ColorCodes.PURPLE_BACKGROUND + ColorCodes.BLACK_BOLD + "    MYSTERY CARD    " + ColorCodes.RESET;
            String b = ColorCodes.PURPLE_BACKGROUND + ColorCodes.BLACK_UNDERLINED + "                    " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "        ####        " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       ##  ##       " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "          ##        " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "         ##         " + ColorCodes.RESET;
            String g = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "                    " + ColorCodes.RESET;
            String h = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "         ##         " + ColorCodes.RESET;
            String i = ColorCodes.WHITE_BACKGROUND_BRIGHT + "                    " + ColorCodes.RESET;

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
            playerHandler.sendMessage("You are in START! position");
            playerHandler.sendMessage("");
            playerHandler.sendMessage("GO GO GO GO GO GO GO GO");
            playerHandler.sendMessage("");
            return;
        }
        if (board[playerHandler.position] instanceof Jail) {


            String a = ColorCodes.BLACK_BACKGROUND_BRIGHT + ColorCodes.BLACK_UNDERLINED + "      VISIT JAIL     " + ColorCodes.RESET;
            String b = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||   ||, , ,||   || " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||  (||/|/(\\||/  || " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||  ||| _'_`|||  || " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||   || o o ||   || " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||  (||  - `||)  || " + ColorCodes.RESET;
            String g = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||   ||  =  ||   || " + ColorCodes.RESET;
            String h = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||   ||\\__/||    || " + ColorCodes.RESET;
            String i = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||___||) , (||___|| " + ColorCodes.RESET;
            String j = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ||---||-\\_/||---|| " + ColorCodes.RESET;


            playerHandler.sendMessage(a);
            playerHandler.sendMessage(b);
            playerHandler.sendMessage(c);
            playerHandler.sendMessage(d);
            playerHandler.sendMessage(e);
            playerHandler.sendMessage(f);
            playerHandler.sendMessage(g);
            playerHandler.sendMessage(h);
            playerHandler.sendMessage(i);
            ;

            playerHandler.sendMessage("");
            playerHandler.sendMessage("You are visiting Jail...");
            broadcastOthers(playerHandler.name + "is visiting jail",playerHandler);
            playerHandler.sendMessage("");
            ;
            return;
        }
        if (board[playerHandler.position] instanceof FreeParking) {

            String a = ColorCodes.BLACK_BACKGROUND_BRIGHT + ColorCodes.BLACK_UNDERLINED + "      FREE PARKING    " + ColorCodes.RESET;
            String b = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "         ██  ██       " + ColorCodes.RESET;
            String c = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "         ██  ██       " + ColorCodes.RESET;
            String d = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "      ████████████    " + ColorCodes.RESET;
            String e = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "    ████████████████  " + ColorCodes.RESET;
            String f = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + " ████    ██  ██  ████ " + ColorCodes.RESET;
            String g = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + " ████    ██  ██  ████ " + ColorCodes.RESET;
            String h = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + " ████    ██  ██       " + ColorCodes.RESET;
            String i = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "  ██████████████      " + ColorCodes.RESET;
            String j = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "      ██████████████  " + ColorCodes.RESET;
            String k = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "         ██  ██  ████ " + ColorCodes.RESET;
            String l = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + " ████    ██  ██  ████ " + ColorCodes.RESET;
            String m = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "   ██████████████████ " + ColorCodes.RESET;
            String n = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "     ██████████████   " + ColorCodes.RESET;
            String o = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "         ██  ██       " + ColorCodes.RESET;
            String p = ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.GREEN_BOLD + "         ██  ██       " + ColorCodes.RESET;


            playerHandler.sendMessage(a);
            playerHandler.sendMessage(b);
            playerHandler.sendMessage(c);
            playerHandler.sendMessage(d);
            playerHandler.sendMessage(e);
            playerHandler.sendMessage(f);
            playerHandler.sendMessage(g);
            playerHandler.sendMessage(h);
            playerHandler.sendMessage(i);
            playerHandler.sendMessage(j);
            playerHandler.sendMessage(k);
            playerHandler.sendMessage(l);
            playerHandler.sendMessage(m);
            playerHandler.sendMessage(n);
            playerHandler.sendMessage(o);
            playerHandler.sendMessage(p);

            playerHandler.sendMessage("");
            playerHandler.sendMessage("You are at Free Parking");
            playerHandler.sendMessage("");
        }
    }

    private void resetProperties(PlayerHandler playerHandler) {
        for (int i = 0; i < playerHandler.cardsOwned.size(); i++) {
            ((Houses) playerHandler.cardsOwned.get(i)).setOwned(false);
            playerHandler.cardsOwned.remove(i);
            //System.out.println(playerHandler.name + " left the game");
        }
    }


    /**
     * Events
     */


    public synchronized int rollDicesPlayer(PlayerHandler playerHandler) {

        canContinue = false;

        while (!canContinue)

            if (!playerHandler.roundCompleted) {
                playerHandler.sendMessage(ColorCodes.WHITE_UNDERLINED + "Please Roll the Dices" + ColorCodes.RESET);
                playerHandler.sendMessage("");
                playerHandler.sendMessage(ColorCodes.GREEN_BOLD + "R -> roll the dices" + ColorCodes.RESET);


                try {
                    if ("r".equalsIgnoreCase(playerHandler.in.readLine())) {
                        broadcastOthers(playerHandler.name + " rolled the dices", playerHandler);
                        canContinue = true;
                        return rollDices(playerHandler);
                    } else {

                        playerHandler.sendMessage(ColorCodes.PURPLE_BOLD + "Invalid operation" + ColorCodes.RESET);
                        playerHandler.sendMessage("");
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

                switch (firstDice) {

                    case 1 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   ●   " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 2 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●     " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "     ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 3 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●     " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   ●   " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "     ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 4 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 5 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   ●   " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 6 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }


                }

                switch (secondDice) {

                    case 1 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   ●   " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 2 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●     " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "     ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 3 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●     " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   ●   " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "     ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 4 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "       " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 5 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + "   ●   " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }
                    case 6 -> {
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage(ColorCodes.WHITE_BACKGROUND_BRIGHT + ColorCodes.BLACK_BOLD + " ●   ● " + ColorCodes.RESET);
                        playerHandler.sendMessage("");
                    }

                }


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
            playerHandler.sendMessage("You moved to position: " + newNewPosition);
            broadcastOthers(playerHandler.name + " moved to position " + newNewPosition, playerHandler);
            playerHandler.sendMessage("");

        } else {
            playerHandler.position = newPosition;
            playerHandler.sendMessage("You moved to position: " + newPosition);
            broadcastOthers(playerHandler.name + " moved to position " + newPosition, playerHandler);
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
                    playerHandler.sendMessage(ColorCodes.WHITE_UNDERLINED + "Do you want to buy the house?" + ColorCodes.RESET);
                    playerHandler.sendMessage(ColorCodes.GREEN_BOLD + "Y -> YES" + ColorCodes.RESET + " | " +
                            ColorCodes.RED_BOLD + "N -> NO" + ColorCodes.RESET);

                    String input = null;
                    try {
                        input = playerHandler.in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    switch (input.toLowerCase()) {
                        case "y" -> {
                            playerHandler.sendMessage("");
                            buy(playerHandler);
                            validChoice = true;
                        }
                        case "n" -> {
                            validChoice = true;
                            broadcastOthers(playerHandler.name + " didn't buy the property", playerHandler);

//no-op
                        }
                        default -> playerHandler.sendMessage(ColorCodes.PURPLE_BOLD + "Invalid operation" + ColorCodes.RESET + "\n");

                        //TODO error handling; maybe repeat question
                    }
                }
                return;
            }


            if (playerHandler.cardsOwned.contains(board[playerHandler.position])) {
                playerHandler.sendMessage("You are at your own property");
                playerHandler.sendMessage("");
                return;
            }

            if (checkIfHasBalanceRent(playerHandler)) {
                payRent(playerHandler);
                return;
            }

            playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You don't have money to pay" + ColorCodes.RESET);
            playerHandler.isDead = true;
            resetProperties(playerHandler);
            playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You lost\n" + ColorCodes.RESET);
            playerHandler.sendMessage("\nYou left the game");
            broadcastOthers(ColorCodes.WHITE_BOLD_BRIGHT + playerHandler.name + " Left the game" + ColorCodes.RESET, playerHandler);
        }
        if (board[playerHandler.position] instanceof Tax) {
            if (checkIfHasBalanceTax(playerHandler)) {
                payTax(playerHandler);
                return;
            }
            playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You don't have money to pay" + ColorCodes.RESET);
            playerHandler.isDead = true;
            resetProperties(playerHandler);
            playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You lost\n" + ColorCodes.RESET);
            playerHandler.sendMessage("\nYou left the game");
            broadcastOthers(ColorCodes.WHITE_BOLD_BRIGHT + playerHandler.name + " Left the game" + ColorCodes.RESET, playerHandler);
            return;
        }

        if (board[playerHandler.position] instanceof Mystery) {
            switch (Mystery.generateRandomCard()) {
                case "Pay" -> {
                    if (checkIfHasBalanceMystery(playerHandler)) {
                        payMystery(playerHandler);
                        return;
                    }
                    playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You don't have money to pay" + ColorCodes.RESET);
                    playerHandler.isDead = true;
                    playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You lost\n" + ColorCodes.RESET);
                    playerHandler.sendMessage("\nYou left the game");
                    broadcastOthers(ColorCodes.WHITE_BOLD_BRIGHT + playerHandler.name + " Left the game" + ColorCodes.RESET, playerHandler);
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
        isRoundOver = false;
        canContinue = false;

        while (!canContinue) {
            playerHandler.sendMessage(ColorCodes.WHITE_UNDERLINED + "What Will Be Your Next Move?" + ColorCodes.RESET);
            playerHandler.sendMessage(" ");
            playerHandler.sendMessage(ColorCodes.YELLOW_BOLD + "V -> VIEW PROPERTIES" + " | " + ColorCodes.CYAN_BOLD +
                    "B -> CHECK BALANCE " + ColorCodes.RESET);
            playerHandler.sendMessage(ColorCodes.GREEN_BOLD + "C -> CONTINUE PLAYING" + ColorCodes.RESET + " | " +
                    ColorCodes.RED_BOLD + "E -> EXIT GAME" + ColorCodes.RESET);

            String input = playerHandler.in.readLine();

            switch (input.toLowerCase()) {
                case "b" -> {
                    playerHandler.sendMessage("");
                    playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "Your balance is: " + playerHandler.balance + " $" + ColorCodes.RESET);
                    playerHandler.sendMessage("");
                }
                case "v" -> {
                    playerHandler.sendMessage("");
                    playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You own the following properties:" + ColorCodes.RESET);
                    playerHandler.sendMessage("");

                    if (!playerHandler.cardsOwned.isEmpty()) {
                        for (Positions p : playerHandler.cardsOwned) {
                            playerHandler.sendMessage((playerHandler.cardsOwned.indexOf(p) + 1) + "-> " + p.getName());
                        }
                    }
                    playerHandler.sendMessage("");
                }
                case "c" -> {
                    playerHandler.sendMessage("");
                    canContinue = true;
                }
                case "e" -> {
                    playerHandler.sendMessage("");
                    playerHandler.isDead = true;
                    resetProperties(playerHandler);
                    playerHandler.sendMessage(ColorCodes.WHITE_BOLD_BRIGHT + "You lost\n" + ColorCodes.RESET);
                    playerHandler.sendMessage("You left the game");
                    broadcastOthers(ColorCodes.WHITE_BOLD_BRIGHT + playerHandler.name + " Left the game" + ColorCodes.RESET, playerHandler);
                    canContinue = true;
                    isRoundOver = true;
                    return;
                }

//no-op
                default -> {
                    playerHandler.sendMessage("Invalid operation");
                    playerHandler.sendMessage("");
                }
            }

        }
    }


    /**
     * Round Maker
     */

    public void roundMaker(PlayerHandler playerHandler) throws IOException {

        if (!isGameOver) {
            //playerHandler.sendMessage("");
            playerHandler.sendMessage("● " + ColorCodes.GREEN_BOLD + playerHandler.name + ColorCodes.RESET + " is your turn");
            playerHandler.sendMessage("");
            System.out.println(ColorCodes.GREEN_BOLD + playerHandler.name + " is playing..." + ColorCodes.RESET);
            broadcastOthers(ColorCodes.GREEN_BOLD + playerHandler.name + " is playing..." + ColorCodes.RESET, playerHandler);


            otherEvent(playerHandler);


            if (!isRoundOver) {

                if (!playerHandler.isDead) {
                    int diceValue = rollDicesPlayer(playerHandler);
                    playerHandler.sendMessage(" ");
                    if (!playerHandler.isInJail) {

                        move(diceValue, playerHandler);

                        getPositionDetails(playerHandler);

                        event(playerHandler);

                        playerHandler.sendMessage("Your turn is over!");
                        playerHandler.sendMessage("-------------------");
                        playerHandler.sendMessage("");


                        System.out.println(ColorCodes.RED_BOLD + playerHandler.name + " finished his turn" + ColorCodes.RESET);
                        System.out.println("");
                        broadcastOthers(ColorCodes.RED_BOLD + playerHandler.name + " finished his turn" + ColorCodes.RESET, playerHandler);
                        broadcastOthers("", playerHandler);

                        ;
                        isRoundOver = true;


                    } else {
                        playerHandler.sendMessage(ColorCodes.BLUE_BOLD + "You cant play this round because you were arrested. Next round you can play" + ColorCodes.RESET);
                        broadcastOthers(ColorCodes.BLUE_BOLD + playerHandler.name + " cant play this round because was arrested. " +
                                "Next round can play" + ColorCodes.RESET,playerHandler);
                        playerHandler.sendMessage("");
                        playerHandler.isInJail = false;
                        playerHandler.sendMessage("Your turn is over!");
                        playerHandler.sendMessage("-------------------");
                        playerHandler.sendMessage("");
                        System.out.println(ColorCodes.RED_BOLD + playerHandler.name + " finished his turn" + ColorCodes.RESET);
                        System.out.println("");
                        broadcastOthers("", playerHandler);
                        broadcastOthers(ColorCodes.RED_BOLD + playerHandler.name + " finished his turn" + ColorCodes.RESET, playerHandler);

                        isRoundOver = true;

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
        broadcast("");
        broadcast("Game started");
        broadcast("");
        System.out.println("");

        while (!isGameOver) {
            for (int i = 0; i < listOfPlayers.size(); i++) {

                try {
                    listOfPlayers.get(i).isPlaying = true;
                    listOfPlayers.get(i).roundCompleted = false;

                    roundMaker(listOfPlayers.get(i));

                    if (listOfPlayers.get(i).isDead) {
                        listOfPlayers.get(i).roundCompleted = true;
                       // System.out.println(listOfPlayers.get(i).name + " left the game");
                        listOfPlayers.remove(listOfPlayers.get(i));
                        i--;
                        if (listOfPlayers.size() == 1) {
                            gameOver();
                        }
                    }

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

    public void broadcastOthers(String message, PlayerHandler playerHandler) {
        for (PlayerHandler ph : listOfPlayers) {
            if (ph != playerHandler)
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
    private boolean roundCompleted;
    private boolean isPlaying;


    public PlayerHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.balance = 1500;
        this.position = 0;
        this.cardsOwned = new LinkedList<>();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.name = generateName();
        this.isPlaying = false;

    }

    public String generateName() throws IOException {
        sendMessage(ColorCodes.RED + "+-+-+-+-+-+-+-+-+" + ColorCodes.RESET);
        sendMessage(ColorCodes.RED + "|M|o|n|o|p|o|l|y|" + ColorCodes.RESET);
        sendMessage(ColorCodes.RED + "+-+-+-+-+-+-+-+-+" + ColorCodes.RESET);
        sendMessage("");

        sendMessage("WELCOME TO THE GAME!!!");

        sendMessage("");
        sendMessage(ColorCodes.WHITE_UNDERLINED + "PLEASE WRITE YOUR USERNAME:" + ColorCodes.RESET);
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






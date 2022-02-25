package academy.mindswap.server;

import academy.mindswap.server.Game;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Game game = new Game(2);

        game.startServer();



    }
}

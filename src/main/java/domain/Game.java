package domain;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final int numberOfPlayers;

    public Game(int numberOfPlayers) {
        if (numberOfPlayers < 3 || numberOfPlayers > 4) {
            throw new IllegalArgumentException(
                    "Player count must be 3 or 4");
        }
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
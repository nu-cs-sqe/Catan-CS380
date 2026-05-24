package domain;

public final class Game {

    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;

    private final int numberOfPlayers;

    public Game(int numberOfPlayers) {
        if (numberOfPlayers < MIN_PLAYERS
                || numberOfPlayers > MAX_PLAYERS) {
            throw new IllegalArgumentException(
                    "Player count must be between "
                            + MIN_PLAYERS + " and " + MAX_PLAYERS);
        }
        this.numberOfPlayers = numberOfPlayers;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
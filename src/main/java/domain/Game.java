package domain;


import board.Board;
import board.Edge;
import board.Vertex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Game {

    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;
    private static final int SETUP_ROUNDS = 2;

    private final List<Player> players;
    private final int firstPlayerIndex;
    private final int[] turnOrder;
    private int setupRound = 1;
    private int setupIndex;
    private int mainTurnIndex;

    public Game(List<Player> players) {
        this(players, new RandomDiceRoller());
    }

    public Game(List<Player> players, DiceRoller diceRoller) {
        if (players.size() < MIN_PLAYERS
                || players.size() > MAX_PLAYERS) {
            throw new IllegalArgumentException(
                    "Player count must be between "
                            + MIN_PLAYERS + " and " + MAX_PLAYERS);
        }
        this.players = new ArrayList<>(players);
        this.firstPlayerIndex = determineFirstPlayer(diceRoller);
        this.turnOrder = buildTurnOrder();
    }

    private int determineFirstPlayer(DiceRoller diceRoller) {
        int[] rolls = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            rolls[i] = diceRoller.roll();
        }
        return resolveHighestRoller(rolls, diceRoller);
    }

    private int resolveHighestRoller(int[] rolls,
                                     DiceRoller diceRoller) {
        while (true) {
            int maxRoll = -1;
            int maxIndex = -1;
            boolean tied = false;

            for (int i = 0; i < rolls.length; i++) {
                if (rolls[i] > maxRoll) {
                    maxRoll = rolls[i];
                    maxIndex = i;
                    tied = false;
                } else if (rolls[i] == maxRoll) {
                    tied = true;
                }
            }

            if (!tied) {
                return maxIndex;
            }

            for (int i = 0; i < rolls.length; i++) {
                if (rolls[i] == maxRoll) {
                    rolls[i] = diceRoller.roll();
                } else {
                    rolls[i] = -1;
                }
            }
        }
    }

    private int[] buildTurnOrder() {
        int[] order = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            order[i] = (firstPlayerIndex + i) % players.size();
        }
        return order;
    }

    public boolean isSetupComplete() {
        return setupRound > SETUP_ROUNDS;
    }

    public Player getCurrentSetupPlayer() {
        if (isSetupComplete()) {
            throw new IllegalStateException("Setup is complete");
        }
        int[] order = (setupRound == 1) ? turnOrder : getRoundTwoOrder();
        return players.get(order[setupIndex]);
    }

    public void placeSetupSettlement(Vertex vertex, Edge road,
                                     Board board, Bank bank) {
        Player player = getCurrentSetupPlayer();
        TurnFlow turnFlow = new TurnFlow(players, bank);
        turnFlow.buildSetupSettlement(player, vertex, board);
        turnFlow.buildSetupRoad(player, road, board);
        if (setupRound == SETUP_ROUNDS) {
            turnFlow.grantSetupResources(player, vertex);
        }
        advanceSetupCursor();
    }

    private void advanceSetupCursor() {
        setupIndex++;
        if (setupIndex >= players.size()) {
            setupIndex = 0;
            setupRound++;
        }
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public int getCurrentPlayerIndex() {
        return turnOrder[mainTurnIndex];
    }

    public void endTurn() {
        if (!isSetupComplete()) {
            throw new IllegalStateException(
                    "Cannot end a turn before setup is complete");
        }
        mainTurnIndex = (mainTurnIndex + 1) % players.size();
    }

    public int[] getTurnOrder() {
        return Arrays.copyOf(turnOrder, turnOrder.length);
    }

    public int[] getRoundTwoOrder() {
        int[] reverse = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            reverse[i] = turnOrder[players.size() - 1 - i];
        }
        return reverse;
    }
}
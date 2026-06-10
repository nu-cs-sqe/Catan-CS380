package domain;

import board.Board;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Game {

    private static final int MIN_PLAYERS = 3;
    private static final int MAX_PLAYERS = 4;
    private static final int LARGEST_ARMY_VP = 2;
    private static final int LONGEST_ROAD_VP = 2;
    private static final int MIN_KNIGHTS_FOR_ARMY = 3;

    private final List<Player> players;
    private final int firstPlayerIndex;
    private final int[] turnOrder;
    private int largestArmyHolder;
    private int longestRoadHolder;
    private static final int MIN_ROAD_LENGTH = 5;

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
        this.largestArmyHolder = -1;
        this.longestRoadHolder = -1;
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

    public void executeSetupRoundOne() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(turnOrder[i]);
            player.placeSettlement();
            player.placeRoad();
        }
    }

    public void executeSetupRoundTwo() {
        executeSetupRoundTwo(null);
    }

    public void executeSetupRoundTwo(Resource[][] resources) {
        int[] reverseOrder = getRoundTwoOrder();
        for (int i = 0; i < players.size(); i++) {
            int playerIndex = reverseOrder[i];
            Player player = players.get(playerIndex);
            player.placeSettlement();
            player.placeRoad();
            grantResources(player, playerIndex, resources);
        }
    }

    private void grantResources(Player player, int playerIndex,
                                Resource[][] resources) {
        if (resources != null && resources[playerIndex] != null) {
            for (Resource resource : resources[playerIndex]) {
                player.addResource(resource, 1);
            }
        }
    }

    public void updateLongestRoad(Board board) {
        // TODO: implement
    }

    public int getLongestRoadHolder() {
        return longestRoadHolder;
    }

    public int[] getRoundTwoOrder() {
        int[] reverse = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            reverse[i] = turnOrder[players.size() - 1 - i];
        }
        return reverse;
    }

    public void updateLargestArmy() {
        int maxKnights = MIN_KNIGHTS_FOR_ARMY - 1;
        if (largestArmyHolder >= 0) {
            maxKnights = players.get(largestArmyHolder)
                    .getKnightsPlayed();
        }
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getKnightsPlayed() > maxKnights) {
                largestArmyHolder = i;
                return;
            }
        }
    }

    public int getLargestArmyHolder() {
        return largestArmyHolder;
    }

    public int getVictoryPoints(int playerIndex) {
        int vp = players.get(playerIndex).getVictoryPoints();
        if (playerIndex == largestArmyHolder) {
            vp += LARGEST_ARMY_VP;
        }
        if (playerIndex == longestRoadHolder) {
            vp += LONGEST_ROAD_VP;
        }
        return vp;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public int getCurrentPlayerIndex() {
        return firstPlayerIndex;
    }

    public int[] getTurnOrder() {
        return Arrays.copyOf(turnOrder, turnOrder.length);
    }
}
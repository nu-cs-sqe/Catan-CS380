package domain;

import board.Board;
import java.util.ArrayList;
import java.util.List;

public final class TurnFlow {

    private static final int LARGEST_ARMY_VP = 2;
    private static final int LONGEST_ROAD_VP = 2;
    private static final int MIN_KNIGHTS_FOR_ARMY = 3;
    private static final int MIN_ROAD_LENGTH = 5;

    private final List<Player> players;
    private int largestArmyHolder;
    private int longestRoadHolder;

    public TurnFlow(List<Player> players) {
        this.players = new ArrayList<>(players);
        this.largestArmyHolder = -1;
        this.longestRoadHolder = -1;
    }

    public void updateLongestRoad(Board board) {
        int maxLength = MIN_ROAD_LENGTH - 1;
        int newHolder = -1;
        for (int i = 0; i < players.size(); i++) {
            int length = LongestRoadCalculator.calculateForPlayer(
                    board, players.get(i));
            if (length > maxLength) {
                maxLength = length;
                newHolder = i;
            }
        }
        longestRoadHolder = newHolder;
    }

    public int getLongestRoadHolder() {
        return longestRoadHolder;
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


}
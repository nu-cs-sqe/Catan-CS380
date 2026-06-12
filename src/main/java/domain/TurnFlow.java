package domain;

import board.Board;
import java.util.ArrayList;
import java.util.List;

import board.Robber;
import board.Tile;
import board.TileType;
import board.Vertex;

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

    public void rollForProduction(Board board, Robber robber,
                                  int roll) {
        for (Vertex vertex : board.getVertices()) {
            if (vertex.getOwner() == null) {
                continue;
            }
            distributeForVertex(vertex, robber, roll);
        }
    }

    private void distributeForVertex(Vertex vertex,
                                     Robber robber, int roll) {
        Player owner = findMatchingPlayer(vertex.getOwner());
        if (owner == null) {
            return;
        }
        for (Tile tile : vertex.getAdjacentTiles()) {
            if (tile.getNumberToken() == roll
                    && !isRobberOnTile(robber, tile)) {
                Resource resource = tileTypeToResource(
                        tile.getTileType());
                if (resource != null) {
                    owner.addResource(resource, 1);
                }
            }
        }
    }

    private boolean isRobberOnTile(Robber robber, Tile tile) {
        Tile robberTile = robber.getTile();
        if (robberTile == null) {
            return false;
        }
        return robberTile.getQ() == tile.getQ()
                && robberTile.getR() == tile.getR();
    }

    private Player findMatchingPlayer(Player vertexOwner) {
        for (Player player : players) {
            if (player.equals(vertexOwner)) {
                return player;
            }
        }
        return null;
    }

    private Resource tileTypeToResource(TileType tileType) {
        switch (tileType) {
            case FOREST: return Resource.WOOD;
            case PASTURE: return Resource.SHEEP;
            case FIELDS: return Resource.WHEAT;
            case HILLS: return Resource.BRICK;
            case MOUNTAINS: return Resource.ORE;
            default: return null;
        }
    }


}
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
    private final List<DevelopmentCard> pendingCards;

    private final List<Player> players;
    private int largestArmyHolder;
    private int longestRoadHolder;

    private boolean devCardPlayedThisTurn;

    public TurnFlow(List<Player> players) {
        this.players = new ArrayList<>(players);
        this.largestArmyHolder = -1;
        this.longestRoadHolder = -1;
        this.pendingCards = new ArrayList<>();
        this.devCardPlayedThisTurn = false;
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
                    int amount = getProductionAmount(vertex);
                    owner.addResource(resource, amount);
                }
            }
        }
    }

    private int getProductionAmount(Vertex vertex) {
        if (vertex.getSettlement() != null
                && vertex.getSettlement().isCity()) {
            return 2;
        }
        return 1;
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

    public int getDiscardCount(int playerIndex) {
        return players.get(playerIndex).discardOnSevenCount();
    }

    public void moveRobber(Robber robber, Tile targetTile) {
        Tile currentTile = robber.getTile();
        if (currentTile != null
                && currentTile.getQ() == targetTile.getQ()
                && currentTile.getR() == targetTile.getR()) {
            throw new IllegalArgumentException(
                    "Robber must move to a different tile");
        }
        robber.setTile(targetTile);
    }

    public void stealResource(Player thief, Player victim) {
        if (thief.equals(victim)) {
            throw new IllegalArgumentException(
                    "Cannot steal from yourself");
        }
        for (Resource resource : Resource.values()) {
            if (resource == Resource.GENERIC) {
                continue;
            }
            if (victim.getResourceCount(resource) > 0) {
                victim.removeResource(resource, 1);
                thief.addResource(resource, 1);
                return;
            }
        }
    }

    public void buyDevelopmentCard(Player player, Bank bank) {
        player.removeResource(Resource.ORE, 1);
        player.removeResource(Resource.WHEAT, 1);
        player.removeResource(Resource.SHEEP, 1);
        DevelopmentCard card = bank.drawDevelopmentCard();
        pendingCards.add(card);
    }

    public int getPendingCardCount() {
        return pendingCards.size();
    }

    public void playDevelopmentCard(Player player,
                                    DevelopmentCard card) {
        if (card == DevelopmentCard.VICTORY_POINT) {
            throw new IllegalArgumentException(
                    "Victory point cards cannot be played");
        }
        if (!player.getDevelopmentCards().contains(card)) {
            throw new IllegalStateException(
                    "Player does not have this card in hand");
        }
        if (devCardPlayedThisTurn) {
            throw new IllegalStateException(
                    "Already played a dev card this turn");
        }
        devCardPlayedThisTurn = true;
    }

    public void playKnightCard(Player player, Robber robber,
                               Tile targetTile, Player victim) {
        playDevelopmentCard(player, DevelopmentCard.KNIGHT);
        moveRobber(robber, targetTile);
        player.playKnight();
        stealResource(player, victim);
        updateLargestArmy();
    }

    public void playMonopolyCard(Player player, Resource resource) {
        if (resource == Resource.GENERIC) {
            throw new IllegalArgumentException(
                    "Cannot monopolize GENERIC resource");
        }
        playDevelopmentCard(player, DevelopmentCard.MONOPOLY);
        for (Player other : players) {
            if (!other.equals(player)) {
                int amount = other.getResourceCount(resource);
                if (amount > 0) {
                    other.removeResource(resource, amount);
                    player.addResource(resource, amount);
                }
            }
        }
    }

    public void endTurn(Player player) {
        player.addDevelopmentCards(pendingCards);
        pendingCards.clear();
    }


}
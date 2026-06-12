package domain;

import board.Board;
import board.Edge;
import board.Robber;
import board.Tile;
import board.TileType;
import board.Vertex;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class TurnFlow {

    private static final int LARGEST_ARMY_VP = 2;
    private static final int LONGEST_ROAD_VP = 2;
    private static final int MIN_KNIGHTS_FOR_ARMY = 3;
    private static final int MIN_ROAD_LENGTH = 5;
    private static final int WIN_THRESHOLD = 10;
    private static final Map<Resource, Integer> SETTLEMENT_COST =
            settlementCost();

    private static Map<Resource, Integer> settlementCost() {
        Map<Resource, Integer> cost = new EnumMap<>(Resource.class);
        cost.put(Resource.WOOD, 1);
        cost.put(Resource.BRICK, 1);
        cost.put(Resource.SHEEP, 1);
        cost.put(Resource.WHEAT, 1);
        return cost;
    }

    private final List<Player> players;
    private final Bank bank;
    private final List<DevelopmentCard> pendingCards;
    private int largestArmyHolder;
    private int longestRoadHolder;
    private boolean devCardPlayedThisTurn;
    private boolean gameOver;
    private int currentPlayerIndex;

    public TurnFlow(List<Player> players) {
        this(players, null);
    }

    public TurnFlow(List<Player> players, Bank bank) {
        this.players = new ArrayList<>(players);
        this.bank = bank;
        this.largestArmyHolder = -1;
        this.longestRoadHolder = -1;
        this.pendingCards = new ArrayList<>();
        this.devCardPlayedThisTurn = false;
        this.gameOver = false;
        this.currentPlayerIndex = 0;
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

    public void buyDevelopmentCard(Player player) {
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

    public void playRoadBuildingCard(Player player,
                                     Edge edge1, Edge edge2) {
        playDevelopmentCard(player, DevelopmentCard.ROAD_BUILDING);
        player.placeRoad(edge1);
        player.placeRoad(edge2);
    }

    public void playYearOfPlentyCard(Player player,
                                     Resource res1, Resource res2) {
        playDevelopmentCard(player, DevelopmentCard.YEAR_OF_PLENTY);
        bank.distributeResource(res1, 1);
        player.addResource(res1, 1);
        bank.distributeResource(res2, 1);
        player.addResource(res2, 1);
    }

    public void maritimeTrade(Player player, Resource give,
                              int giveCount, Resource receive) {
        player.removeResource(give, giveCount);
        bank.maritimeTrade(give, giveCount, receive);
        player.addResource(receive, 1);
    }

    public void endTurn(Player player) {
        if (gameOver) {
            throw new IllegalStateException(
                    "Cannot end turn after game is over");
        }
        player.addDevelopmentCards(pendingCards);
        pendingCards.clear();
        devCardPlayedThisTurn = false;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void buildSettlement(Player player, Vertex vertex) {
        buildSettlement(player, vertex, null);
    }

    public void buildSettlement(Player player, Vertex vertex,
                                Board board) {
        if (!player.hasResources(SETTLEMENT_COST)) {
            throw new IllegalStateException(
                    "Insufficient resources for settlement");
        }
        if (board != null) {
            checkDistanceRule(vertex, board);
        }
        player.placeSettlement(vertex);
        payCost(player, SETTLEMENT_COST);
        checkForWinner();
    }

    private void checkDistanceRule(Vertex vertex, Board board) {
        for (Edge edge : board.getEdges()) {
            String[] verts = edge.getId().split("\\|");
            if (verts[0].equals(vertex.getId())
                    || verts[1].equals(vertex.getId())) {
                String otherKey = verts[0].equals(vertex.getId())
                        ? verts[1] : verts[0];
                Vertex neighbor = board.getVertex(otherKey);
                if (neighbor != null
                        && neighbor.getSettlement() != null) {
                    throw new IllegalStateException(
                            "Violates distance rule");
                }
            }
        }
    }

    private void payCost(Player player, Map<Resource, Integer> cost) {
        for (Map.Entry<Resource, Integer> entry : cost.entrySet()) {
            player.removeResource(entry.getKey(), entry.getValue());
        }
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
        checkForWinner();
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
                break;
            }
        }
        checkForWinner();
    }

    private void checkForWinner() {
        for (int i = 0; i < players.size(); i++) {
            if (checkWin(i)) {
                gameOver = true;
                return;
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
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

    public boolean checkWin(int playerIndex) {
        return getVictoryPoints(playerIndex) >= WIN_THRESHOLD;
    }
}
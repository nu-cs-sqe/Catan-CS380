package domain;

import board.Board;
import board.Edge;
import board.Robber;
import board.Tile;
import board.TileType;
import board.Vertex;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TurnFlow {

    private static final int LARGEST_ARMY_VP = 2;
    private static final int LONGEST_ROAD_VP = 2;
    private static final int MIN_KNIGHTS_FOR_ARMY = 3;
    private static final int MIN_ROAD_LENGTH = 5;
    private static final int WIN_THRESHOLD = 10;
    private static final int ROBBER_ROLL = 7;
    private static final Map<Resource, Integer> SETTLEMENT_COST =
            settlementCost();
    private static final Map<Resource, Integer> CITY_COST =
            cityCost();

    private static final int CITY_ORE_COST = 3;


    private static Map<Resource, Integer> settlementCost() {
        Map<Resource, Integer> cost = new EnumMap<>(Resource.class);
        cost.put(Resource.WOOD, 1);
        cost.put(Resource.BRICK, 1);
        cost.put(Resource.SHEEP, 1);
        cost.put(Resource.WHEAT, 1);
        return cost;
    }

    private static Map<Resource, Integer> cityCost() {
        Map<Resource, Integer> cost = new EnumMap<>(Resource.class);
        cost.put(Resource.ORE, CITY_ORE_COST);
        cost.put(Resource.WHEAT, 2);
        return cost;
    }
    private static final Map<Resource, Integer> ROAD_COST =
            roadCost();

    private static Map<Resource, Integer> roadCost() {
        Map<Resource, Integer> cost = new EnumMap<>(Resource.class);
        cost.put(Resource.WOOD, 1);
        cost.put(Resource.BRICK, 1);
        return cost;
    }


    private final List<Player> players;
    private final Bank bank;
    private final List<DevelopmentCard> pendingCards;
    private int largestArmyHolder;
    private int longestRoadHolder;
    private boolean devCardPlayedThisTurn;
    private boolean gameOver;
    private boolean robberPending;
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
        Map<Player, Map<Resource, Integer>> gains = new HashMap<>();
        Map<Resource, Integer> demand = new EnumMap<>(Resource.class);
        for (Vertex vertex : board.getVertices()) {
            if (vertex.getOwner() == null) {
                continue;
            }
            collectProduction(vertex, robber, roll, gains, demand);
        }
        distributeProduction(gains, demand);
    }

    private void collectProduction(Vertex vertex, Robber robber, int roll,
                                   Map<Player, Map<Resource, Integer>> gains,
                                   Map<Resource, Integer> demand) {
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
                    gains.computeIfAbsent(owner,
                            p -> new EnumMap<>(Resource.class))
                            .merge(resource, amount, Integer::sum);
                    demand.merge(resource, amount, Integer::sum);
                }
            }
        }
    }

    private void distributeProduction(
            Map<Player, Map<Resource, Integer>> gains,
            Map<Resource, Integer> demand) {
        Set<Resource> payable = EnumSet.noneOf(Resource.class);
        for (Map.Entry<Resource, Integer> entry : demand.entrySet()) {
            if (entry.getValue() <= bank.getStock(entry.getKey())) {
                payable.add(entry.getKey());
            }
        }
        for (Map.Entry<Player, Map<Resource, Integer>> playerGains
                : gains.entrySet()) {
            Player player = playerGains.getKey();
            for (Map.Entry<Resource, Integer> gain
                    : playerGains.getValue().entrySet()) {
                if (payable.contains(gain.getKey())) {
                    bank.distributeResource(gain.getKey(), gain.getValue());
                    player.addResource(gain.getKey(), gain.getValue());
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

    public void discard(Player player, Map<Resource, Integer> chosen) {
        int required = player.discardOnSevenCount();
        int total = 0;
        for (Map.Entry<Resource, Integer> entry : chosen.entrySet()) {
            if (player.getResourceCount(entry.getKey()) < entry.getValue()) {
                throw new IllegalArgumentException(
                        "Cannot discard resources not held");
            }
            total += entry.getValue();
        }
        if (total != required) {
            throw new IllegalArgumentException(
                    "Must discard exactly " + required + " cards");
        }
        for (Map.Entry<Resource, Integer> entry : chosen.entrySet()) {
            player.removeResource(entry.getKey(), entry.getValue());
            bank.returnResource(entry.getKey(), entry.getValue());
        }
    }

    public void moveRobber(Robber robber, Tile targetTile, Board board) {
        if (board.getTile(targetTile.getQ(), targetTile.getR()) == null) {
            throw new IllegalArgumentException(
                    "Robber target is not a board tile");
        }
        Tile currentTile = robber.getTile();
        if (currentTile != null
                && currentTile.getQ() == targetTile.getQ()
                && currentTile.getR() == targetTile.getR()) {
            throw new IllegalArgumentException(
                    "Robber must move to a different tile");
        }
        robber.setTile(targetTile);
    }

    public void resolveRoll(Board board, Robber robber, int roll) {
        if (roll == ROBBER_ROLL) {
            robberPending = true;
        } else {
            rollForProduction(board, robber, roll);
        }
    }

    public boolean isRobberPending() {
        return robberPending;
    }

    public void moveRobberAndSteal(Robber robber, Tile targetTile,
                                   Player thief, Player victim,
                                   Board board) {
        moveRobber(robber, targetTile, board);
        if (victim != null) {
            stealResource(thief, victim, robber, board);
        }
        robberPending = false;
    }

    private void requireRobberResolved() {
        if (robberPending) {
            throw new IllegalStateException(
                    "Move the robber before taking other actions");
        }
    }

    public void stealResource(Player thief, Player victim,
                              Robber robber, Board board) {
        if (thief.equals(victim)) {
            throw new IllegalArgumentException(
                    "Cannot steal from yourself");
        }
        if (!stealCandidates(robber, board).contains(victim)) {
            throw new IllegalArgumentException(
                    "Victim does not border the robber");
        }
        transferStolenResource(thief, victim);
    }

    private void transferStolenResource(Player thief, Player victim) {
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

    public List<Player> stealCandidates(Robber robber, Board board) {
        List<Player> candidates = new ArrayList<>();
        Tile robberTile = robber.getTile();
        if (robberTile == null) {
            return candidates;
        }
        for (Vertex vertex : board.getVertices()) {
            if (vertex.getOwner() == null
                    || !vertexBordersTile(vertex, robberTile)) {
                continue;
            }
            Player owner = findMatchingPlayer(vertex.getOwner());
            if (owner != null && !candidates.contains(owner)) {
                candidates.add(owner);
            }
        }
        return candidates;
    }

    private boolean vertexBordersTile(Vertex vertex, Tile tile) {
        for (Tile adjacent : vertex.getAdjacentTiles()) {
            if (adjacent.getQ() == tile.getQ()
                    && adjacent.getR() == tile.getR()) {
                return true;
            }
        }
        return false;
    }

    public void buyDevelopmentCard(Player player) {
        requireRobberResolved();
        if (player.getResourceCount(Resource.ORE) < 1
                || player.getResourceCount(Resource.WHEAT) < 1
                || player.getResourceCount(Resource.SHEEP) < 1) {
            throw new IllegalStateException(
                    "Insufficient resources for development card");
        }
        DevelopmentCard card = bank.drawDevelopmentCard();
        player.removeResource(Resource.ORE, 1);
        player.removeResource(Resource.WHEAT, 1);
        player.removeResource(Resource.SHEEP, 1);
        pendingCards.add(card);
    }

    public int getPendingCardCount() {
        return pendingCards.size();
    }

    public void playDevelopmentCard(Player player,
                                    DevelopmentCard card) {
        requireRobberResolved();
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
                               Tile targetTile, Player victim,
                               Board board) {
        playDevelopmentCard(player, DevelopmentCard.KNIGHT);
        moveRobber(robber, targetTile, board);
        player.playKnight();
        stealResource(player, victim, robber, board);
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
                                     Edge edge1, Edge edge2,
                                     Board board) {
        playDevelopmentCard(player, DevelopmentCard.ROAD_BUILDING);
        placeConnectedRoad(player, edge1, board);
        placeConnectedRoad(player, edge2, board);
        updateLongestRoad(board);
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
        requireRobberResolved();
        player.removeResource(give, giveCount);
        bank.maritimeTrade(give, giveCount, receive);
        player.addResource(receive, 1);
    }

    public void endTurn(Player player) {
        requireRobberResolved();
        if (gameOver) {
            throw new IllegalStateException(
                    "Cannot end turn after game is over");
        }
        player.addDevelopmentCards(pendingCards);
        pendingCards.clear();
        devCardPlayedThisTurn = false;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        checkForWinner();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void buildSettlement(Player player, Vertex vertex) {
        buildSettlement(player, vertex, null);
    }

    public void buildSettlement(Player player, Vertex vertex,
                                Board board) {
        requireRobberResolved();
        if (!player.hasResources(SETTLEMENT_COST)) {
            throw new IllegalStateException(
                    "Insufficient resources for settlement");
        }
        if (board != null) {
            checkDistanceRule(vertex, board);
            checkAdjacentRoad(vertex, player, board);
        }
        player.placeSettlement(vertex);
        payCost(player, SETTLEMENT_COST);
        checkForWinner();
    }

    public void buildSetupSettlement(Player player, Vertex vertex,
                                     Board board) {
        checkDistanceRule(vertex, board);
        player.placeSettlement(vertex);
    }

    private void checkAdjacentRoad(Vertex vertex, Player player,
                                   Board board) {
        for (Edge edge : board.getEdges()) {
            String[] verts = edge.getId().split("\\|");
            if (verts[0].equals(vertex.getId())
                    || verts[1].equals(vertex.getId())) {
                if (player.equals(edge.getOwner())) {
                    return;
                }
            }
        }
        throw new IllegalStateException(
                "No adjacent road owned by player");
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

    public void buildCity(Player player, Vertex vertex) {
        requireRobberResolved();
        if (!player.hasResources( CITY_COST )) {

            throw new IllegalStateException(
                    "Insufficient resources to build city");
        }
        player.upgradeSettlementToCity(vertex);
        payCost(player, CITY_COST);
        checkForWinner();
    }

    public void buildRoad(Player player, Edge edge, Board board) {
        requireRobberResolved();
        if (!player.hasResources( ROAD_COST )) {
            throw new IllegalStateException (
                    "Insufficient resources to build road");
        }
        placeConnectedRoad(player, edge, board);
        payCost(player, ROAD_COST);
        updateLongestRoad(board);
    }

    public void buildSetupRoad(Player player, Edge edge, Board board) {
        placeConnectedRoad(player, edge, board);
    }

    public void grantSetupResources(Player player, Vertex vertex) {
        for (Tile tile : vertex.getAdjacentTiles()) {
            Resource resource = tileTypeToResource(tile.getTileType());
            if (resource != null && bank.canDistribute(resource, 1)) {
                bank.distributeResource(resource, 1);
                player.addResource(resource, 1);
            }
        }
    }

    private void placeConnectedRoad(Player player, Edge edge,
                                    Board board) {
        checkRoadConnectivity(player, edge, board);
        player.placeRoad(edge);
    }

    private void checkRoadConnectivity(Player player, Edge edge,
                                       Board board) {
        String[] verts = edge.getId().split("\\|");
        if (touchesOwnBuilding(player, verts, board)
                || touchesOwnRoad(player, verts, edge, board)) {
            return;
        }
        throw new IllegalStateException(
                "Road must connect to your own road or settlement");
    }

    private boolean touchesOwnBuilding(Player player, String[] verts,
                                       Board board) {
        for (String vertexKey : verts) {
            Vertex vertex = board.getVertex(vertexKey);
            if (vertex != null && player.equals(vertex.getOwner())) {
                return true;
            }
        }
        return false;
    }

    private boolean touchesOwnRoad(Player player, String[] verts,
                                   Edge edge, Board board) {
        for (Edge other : board.getEdges()) {
            if (other.getId().equals(edge.getId())
                    || !player.equals(other.getOwner())) {
                continue;
            }
            if (sharesVertex(verts, other.getId().split("\\|"))) {
                return true;
            }
        }
        return false;
    }

    private boolean sharesVertex(String[] verts, String[] otherVerts) {
        for (String v : verts) {
            for (String other : otherVerts) {
                if (v.equals(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateLongestRoad(Board board) {
        int maxLength = MIN_ROAD_LENGTH - 1;
        int newHolder = -1;
        if (longestRoadHolder >= 0) {
            int holderLength = LongestRoadCalculator.calculateForPlayer(
                    board, players.get(longestRoadHolder));
            if (holderLength >= MIN_ROAD_LENGTH) {
                maxLength = holderLength;
                newHolder = longestRoadHolder;
            }
        }
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
        if (checkWin(currentPlayerIndex)) {
            gameOver = true;
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
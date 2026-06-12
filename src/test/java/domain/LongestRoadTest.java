package domain;

import board.Board;
import board.Edge;
import board.Shuffler;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class LongestRoadTest {

    private static final String EDGE_1 = "0,2|1,1";
    private static final String EDGE_2 = "1,-1|1,1";
    private static final String EDGE_3 = "0,-2|1,-1";
    private static final String EDGE_4 = "-1,-1|0,-2";
    private static final String EDGE_5 = "-1,-1|-1,1";
    private static final String EDGE_6 = "-2,2|-1,1";

    // TC5 – No player has Longest Road with fewer than 5 segments
    @Test
    public void testNoLongestRoadUnder5Segments() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        setEdgeOwner(board, EDGE_1, players.get(0));
        setEdgeOwner(board, EDGE_2, players.get(0));
        setEdgeOwner(board, EDGE_3, players.get(0));
        setEdgeOwner(board, EDGE_4, players.get(0));

        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(-1, turnFlow.getLongestRoadHolder());
    }

    // TC6 – First player to build 5 continuous roads gets Longest Road
    @Test
    public void testFirstPlayerWith5RoadsGetsLongestRoad() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        setEdgeOwner(board, EDGE_1, players.get(0));
        setEdgeOwner(board, EDGE_2, players.get(0));
        setEdgeOwner(board, EDGE_3, players.get(0));
        setEdgeOwner(board, EDGE_4, players.get(0));
        setEdgeOwner(board, EDGE_5, players.get(0));

        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(0, turnFlow.getLongestRoadHolder());
    }

    // TC7 – Another player with a longer road takes Longest Road
    @Test
    public void testPlayerWithLongerRoadTakesLongestRoad() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        setEdgeOwner(board, EDGE_1, players.get(0));
        setEdgeOwner(board, EDGE_2, players.get(0));
        setEdgeOwner(board, EDGE_3, players.get(0));
        setEdgeOwner(board, EDGE_4, players.get(0));
        setEdgeOwner(board, EDGE_5, players.get(0));
        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(0, turnFlow.getLongestRoadHolder());

        setEdgeOwner(board, "3,-1|3,1", players.get(1));
        setEdgeOwner(board, "2,2|3,1", players.get(1));
        setEdgeOwner(board, "2,2|2,4", players.get(1));
        setEdgeOwner(board, "2,4|3,5", players.get(1));
        setEdgeOwner(board, "3,5|3,7", players.get(1));
        setEdgeOwner(board, "2,8|3,7", players.get(1));
        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(1, turnFlow.getLongestRoadHolder());
    }

    // TC8 – Tied road length does not change Longest Road holder
    @Test
    public void testTiedRoadLengthKeepsCurrentHolder() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        setEdgeOwner(board, EDGE_1, players.get(0));
        setEdgeOwner(board, EDGE_2, players.get(0));
        setEdgeOwner(board, EDGE_3, players.get(0));
        setEdgeOwner(board, EDGE_4, players.get(0));
        setEdgeOwner(board, EDGE_5, players.get(0));
        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(0, turnFlow.getLongestRoadHolder());

        setEdgeOwner(board, "3,-1|3,1", players.get(1));
        setEdgeOwner(board, "2,2|3,1", players.get(1));
        setEdgeOwner(board, "2,2|2,4", players.get(1));
        setEdgeOwner(board, "2,4|3,5", players.get(1));
        setEdgeOwner(board, "3,5|3,7", players.get(1));
        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(0, turnFlow.getLongestRoadHolder());
    }

    // TC9 – Only the longest branch counts, not total roads
    @Test
    public void testOnlyLongestBranchCounts() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        setEdgeOwner(board, EDGE_4, players.get(0));
        setEdgeOwner(board, EDGE_3, players.get(0));
        setEdgeOwner(board, EDGE_2, players.get(0));
        setEdgeOwner(board, EDGE_1, players.get(0));
        setEdgeOwner(board, "1,1|2,2", players.get(0));

        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(-1, turnFlow.getLongestRoadHolder());
    }

    // TC10 – Opponent settlement breaks a road
    @Test
    public void testOpponentSettlementBreaksRoad() {
        List<Player> players = createPlayers();
        TurnFlow turnFlow = new TurnFlow(players);
        Board board = createBoard();

        setEdgeOwner(board, EDGE_1, players.get(0));
        setEdgeOwner(board, EDGE_2, players.get(0));
        setEdgeOwner(board, EDGE_3, players.get(0));
        setEdgeOwner(board, EDGE_4, players.get(0));
        setEdgeOwner(board, EDGE_5, players.get(0));
        setEdgeOwner(board, EDGE_6, players.get(0));
        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(0, turnFlow.getLongestRoadHolder());

        board.getVertex("0,-2").setOwner(players.get(1));
        turnFlow.updateLongestRoad(board);
        Assertions.assertEquals(-1, turnFlow.getLongestRoadHolder());
    }

    private Board createBoard() {
        Shuffler noOp = new Shuffler() {
            @Override
            public <T> void shuffle(List<T> list) {
                // no-op
            }
        };
        Board board = new Board(noOp);
        board.create();
        return board;
    }

    private void setEdgeOwner(Board board, String edgeKey,
                              Player player) {
        Edge edge = board.getEdge(edgeKey);
        if (edge != null) {
            edge.setOwner(player);
        }
    }

    private List<Player> createPlayers() {
        return Arrays.asList(
                new Player("Alice", PlayerColor.RED),
                new Player("Bob", PlayerColor.BLUE),
                new Player("Carol", PlayerColor.WHITE)
        );
    }
}
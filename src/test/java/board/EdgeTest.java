package board;

import domain.Player;
import domain.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdgeTest {

  private static final int MAX_ADJACENT_TILES = 2;
  private static final int MIN_ADJACENT_TILES = 1;
  private static final int COASTAL_Q = -2;

  private final Player player = new Player("Alice", PlayerColor.RED);

  // TC1 – Constructor stores id correctly
  @Test
  void constructor_storesId() {
    String edgeId = "42";
    Edge edge = new Edge(edgeId);

    assertEquals(edgeId, edge.getId());
  }

  // TC2 – Constructor initializes adjacentTiles as empty
  @Test
  void constructor_initializesAdjacentTilesEmpty() {
    Edge edge = new Edge("0");

    assertTrue(edge.getAdjacentTiles().isEmpty());
  }

  // TC3 – Interior edge has exactly 2 adjacent tiles after adding two
  // BVA: 2 is the maximum adjacent tile count; boundary from below: 1
  @Test
  void addTile_interiorEdge_hasTwoAdjacentTiles() {
    Edge edge = new Edge("0");
    edge.addTile(new Tile(TileType.FOREST, 0, 0));
    edge.addTile(new Tile(TileType.HILLS, 1, 0));

    assertEquals(MAX_ADJACENT_TILES, edge.getAdjacentTiles().size());
  }

  // TC4 – Coastal edge has exactly 1 adjacent tile after adding one
  // BVA: 1 is the minimum adjacent tile count; boundary from above: 2
  @Test
  void addTile_coastalEdge_hasOneAdjacentTile() {
    Edge edge = new Edge("0");
    edge.addTile(new Tile(TileType.FOREST, COASTAL_Q, 0));

    assertEquals(MIN_ADJACENT_TILES, edge.getAdjacentTiles().size());
  }

  // TC5 – addTile stores the correct tile reference
  @Test
  void addTile_storesCorrectTileReference() {
    Edge edge = new Edge("0");
    Tile tile = new Tile(TileType.FOREST, 0, 0);
    edge.addTile(tile);

    assertEquals(List.of(tile), edge.getAdjacentTiles());
  }

  // TC6 – Unowned edge returns null for getOwner
  // BVA: null boundary - unowned edge
  @Test
  void getOwner_newEdge_returnsNull() {
    Edge edge = new Edge("0");

    assertNull(edge.getOwner());
  }

  // TC7 – setOwner stores and getOwner retrieves the player
  @Test
  void setOwner_nonNullPlayer_returnsPlayer() {
    Edge edge = new Edge("0");
    edge.setOwner(player);

    assertEquals(player, edge.getOwner());
  }
}

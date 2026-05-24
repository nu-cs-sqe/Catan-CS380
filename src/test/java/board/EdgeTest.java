package board;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EasyMockExtension.class)
class EdgeTest {

  @Mock private Player player;

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

    assertEquals(2, edge.getAdjacentTiles().size());
  }

  // TC4 – Coastal edge has exactly 1 adjacent tile after adding one
  // BVA: 1 is the minimum adjacent tile count; boundary from above: 2
  @Test
  void addTile_coastalEdge_hasOneAdjacentTile() {
    Edge edge = new Edge("0");
    edge.addTile(new Tile(TileType.FOREST, -2, 0));

    assertEquals(1, edge.getAdjacentTiles().size());
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
    replay(player);
    Edge edge = new Edge("0");
    edge.setOwner(player);

    assertEquals(player, edge.getOwner());
  }
}

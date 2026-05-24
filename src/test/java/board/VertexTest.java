package board;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.easymock.EasyMock.replay;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EasyMockExtension.class)
class VertexTest {

  @Mock private Player player;

  // TC1 – Constructor stores id correctly
  @Test
  void constructor_storesId() {
    String vertexId = "7";
    Vertex vertex = new Vertex(vertexId);

    assertEquals(vertexId, vertex.getId());
  }

  // TC2 – Constructor initializes adjacentTiles as empty
  @Test
  void constructor_initializesAdjacentTilesEmpty() {
    Vertex vertex = new Vertex("0");

    assertTrue(vertex.getAdjacentTiles().isEmpty());
  }

  // TC3 – Interior vertex has exactly 3 adjacent tiles after adding three
  // BVA: 3 is the maximum adjacent tile count; boundary from below: 2
  @Test
  void addTile_interiorVertex_hasThreeAdjacentTiles() {
    Vertex vertex = new Vertex("0");
    vertex.addTile(new Tile(TileType.FOREST, 0, 0));
    vertex.addTile(new Tile(TileType.HILLS, 1, -1));
    vertex.addTile(new Tile(TileType.PASTURE, 1, 0));

    assertEquals(3, vertex.getAdjacentTiles().size());
  }

  // TC4 – Coastal vertex has exactly 1 adjacent tile after adding one
  // BVA: 1 is the minimum adjacent tile count; boundary from above: 2
  @Test
  void addTile_coastalVertex_hasOneAdjacentTile() {
    Vertex vertex = new Vertex("0");
    vertex.addTile(new Tile(TileType.FOREST, -2, 0));

    assertEquals(1, vertex.getAdjacentTiles().size());
  }

  // TC5 – addTile stores the correct tile reference
  @Test
  void addTile_storesCorrectTileReference() {
    Vertex vertex = new Vertex("0");
    Tile tile = new Tile(TileType.FOREST, 0, 0);
    vertex.addTile(tile);

    assertEquals(List.of(tile), vertex.getAdjacentTiles());
  }

  // TC6 – Unowned vertex returns null for getOwner
  // BVA: null boundary - unowned vertex
  @Test
  void getOwner_newVertex_returnsNull() {
    Vertex vertex = new Vertex("0");

    assertNull(vertex.getOwner());
  }

  // TC7 – setOwner stores and getOwner retrieves the player
  @Test
  void setOwner_nonNullPlayer_returnsPlayer() {
    replay(player);
    Vertex vertex = new Vertex("0");
    vertex.setOwner(player);

    assertEquals(player, vertex.getOwner());
  }

  // TC8 – New vertex has no settlement
  // BVA: null boundary - no settlement
  @Test
  void getSettlement_newVertex_returnsNull() {
    Vertex vertex = new Vertex("0");

    assertNull(vertex.getSettlement());
  }

  // TC9 - setSettlement stores and getSettlement retrieves the settlement
  @Test
  void setSettlement_nonNullSettlement_returnsSettlement() {
    Vertex vertex = new Vertex("0");
    Settlement settlement = new Settlement();
    vertex.setSettlement(settlement);

    assertEquals(settlement, vertex.getSettlement());
  }

  // TC10 – New vertex has no harbor
  // BVA: null boundary - vertex not on a harbor
  @Test
  void getHarbor_newVertex_returnsNull() {
    Vertex vertex = new Vertex("0");

    assertNull(vertex.getHarbor());
  }

  // TC11 – setHarbor stores and getHarbor retrieves the harbor
  @Test
  void setHarbor_nonNullHarbor_returnsHarbor() {
    Vertex vertex = new Vertex("0");
    Harbor harbor = new Harbor(ResourceType.GENERIC, 3, "v1", "v2");
    vertex.setHarbor(harbor);

    assertEquals(harbor, vertex.getHarbor());
  }
}

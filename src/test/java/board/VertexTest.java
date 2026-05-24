package board;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VertexTest {

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
}

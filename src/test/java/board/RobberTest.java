package board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RobberTest {

  // TC1 – getTile() returns null immediately after construction
  @Test
  void constructor_tileDefaultsToNull() {
    Robber robber = new Robber();

    assertNull(robber.getTile());
  }

  // TC2 – getPlayer() returns null immediately after construction
  @Test
  void constructor_playerDefaultsToNull() {
    Robber robber = new Robber();

    assertNull(robber.getPlayer());
  }

  // TC3 – setTile() stores the tile and getTile() returns it
  @Test
  void setTile_validTile_getTileReturnsSameTile() {
    Robber robber = new Robber();
    Tile tile = new Tile(TileType.FOREST, 0, 0);

    robber.setTile(tile);

    assertEquals(tile, robber.getTile());
  }

  // TC4 – setTile(null) after a prior assignment clears the tile
  // BVA: null boundary - robber returned to unplaced state
  @Test
  void setTile_null_afterPriorAssignment_getTileReturnsNull() {
    Robber robber = new Robber();
    robber.setTile(new Tile(TileType.FOREST, 0, 0));

    robber.setTile(null);

    assertNull(robber.getTile());
  }
}

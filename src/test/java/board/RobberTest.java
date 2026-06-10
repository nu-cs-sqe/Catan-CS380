package board;

import domain.Player;
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

  // TC3 – setTile() stores the tile and getTile() returns an equal-valued copy
  @Test
  void setTile_validTile_getTileReturnsSameTile() {
    Robber robber = new Robber();
    Tile tile = new Tile(TileType.FOREST, 0, 0);

    robber.setTile(tile);

    assertEquals(tile.getTileType(), robber.getTile().getTileType());
    assertEquals(tile.getQ(), robber.getTile().getQ());
    assertEquals(tile.getR(), robber.getTile().getR());
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

  // TC5 – setPlayer() stores the player and getPlayer() returns it
  @Test
  void setPlayer_validPlayer_getPlayerReturnsSamePlayer() {
    Robber robber = new Robber();
    Player player = new Player();

    robber.setPlayer(player);

    assertEquals(player, robber.getPlayer());
  }

  // TC6 – setPlayer(null) after a prior assignment clears the player
  @Test
  void setPlayer_null_afterPriorAssignment_getPlayerReturnsNull() {
    Robber robber = new Robber();
    robber.setPlayer(new Player());

    robber.setPlayer(null);

    assertNull(robber.getPlayer());
  }
}

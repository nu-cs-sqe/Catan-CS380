package board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TileTest {

  private static final int MIN_Q = -2;
  private static final int MIN_R = 1;
  private static final int MIN_TOKEN = 2;
  private static final int MAX_TOKEN = 12;

  // TC1 – Constructor sets TileType correctly
  @Test
  void constructor_setsTileType() {
    Tile tile = new Tile(TileType.FOREST, 0, 0);

    assertEquals(TileType.FOREST, tile.getTileType());
  }

  // TC2 – Constructor sets q and r coordinates correctly
  @Test
  void constructor_setsCoordinates() {
    Tile tile = new Tile(TileType.HILLS, MIN_Q, MIN_R);

    assertEquals(MIN_Q, tile.getQ());
    assertEquals(MIN_R, tile.getR());
  }

  // TC3 – Number token defaults to 0 (no token assigned)
  @Test
  void constructor_numberTokenDefaultsToZero() {
    Tile tile = new Tile(TileType.PASTURE, 0, 0);

    assertEquals(0, tile.getNumberToken());
  }

  // TC4 – setNumberToken(2) stores the minimum valid token
  // BVA: 2 is the lower boundary of the token range
  @Test
  void setNumberToken_minimumValidToken_returnsTwo() {
    Tile tile = new Tile(TileType.FOREST, 0, 0);
    tile.setNumberToken(MIN_TOKEN);

    assertEquals(MIN_TOKEN, tile.getNumberToken());
  }

  // TC5 – setNumberToken(12) stores the maximum valid token
  // BVA: 12 is the upper boundary of the token range
  @Test
  void setNumberToken_maximumValidToken_returnsTwelve() {
    Tile tile = new Tile(TileType.FOREST, 0, 0);
    tile.setNumberToken(MAX_TOKEN);

    assertEquals(MAX_TOKEN, tile.getNumberToken());
  }
}
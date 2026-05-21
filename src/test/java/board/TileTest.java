package board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    // TC1 – Constructor sets TileType correctly
    @Test
    void constructor_setsTileType() {
        Tile tile = new Tile(TileType.FOREST, 0, 0);

        assertEquals(TileType.FOREST, tile.getTileType());
    }

    // TC2 – Constructor sets q and r coordinates correctly
    @Test
    void constructor_setsCoordinates() {
        Tile tile = new Tile(TileType.HILLS, -2, 1);

        assertEquals(-2, tile.getQ());
        assertEquals(1, tile.getR());
    }

    // TC3 – Number token defaults to 0 (no token assigned)
    @Test
    void constructor_numberTokenDefaultsToZero() {
        Tile tile = new Tile(TileType.PASTURE, 0, 0);

        assertEquals(0, tile.getNumberToken());
    }

    // TC4 – Robber defaults to false (not present)
    @Test
    void constructor_hasRobberDefaultsToFalse() {
        Tile tile = new Tile(TileType.DESERT, 0, 0);

        assertFalse(tile.hasRobber());
    }
}
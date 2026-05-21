package board;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    // TC1 – Key is derived from sorted tile coordinates
    @Test
    void constructor_derivesKeyFromTileCoordinates() {
        List<Tile> tiles = Arrays.asList(
                new Tile(TileType.HILLS, 1, 0),
                new Tile(TileType.FOREST, 0, 0)
        );
        Edge edge = new Edge(tiles);

        assertEquals("0,0|1,0", edge.getKey());
    }

    // TC2 – Constructor sets adjacentTiles correctly
    @Test
    void constructor_setsAdjacentTiles() {
        List<Tile> tiles = Arrays.asList(
                new Tile(TileType.FOREST, 0, 0),
                new Tile(TileType.HILLS, 1, 0)
        );
        Edge edge = new Edge(tiles);

        assertEquals(tiles, edge.getAdjacentTiles());
    }

    // TC3 – Interior edge has exactly 2 adjacent tiles
    // BVA: 2 is the maximum adjacent tile count; boundary from below: 1
    @Test
    void constructor_interiorEdge_hasTwoAdjacentTiles() {
        List<Tile> tiles = Arrays.asList(
                new Tile(TileType.FOREST, 0, 0),
                new Tile(TileType.HILLS, 1, 0)
        );
        Edge edge = new Edge(tiles);

        assertEquals(2, edge.getAdjacentTiles().size());
    }

    // TC4 – Coastal edge has exactly 1 adjacent tile
    // BVA: 1 is the minimum adjacent tile count; boundary from above: 2
    @Test
    void constructor_coastalEdge_hasOneAdjacentTile() {
        List<Tile> tiles = Collections.singletonList(new Tile(TileType.FOREST, -2, 0));
        Edge edge = new Edge(tiles);

        assertEquals(1, edge.getAdjacentTiles().size());
    }
}
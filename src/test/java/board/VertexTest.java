package board;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VertexTest {

    // TC1 – Key is derived from sorted tile coordinates
    @Test
    void constructor_derivesKeyFromTileCoordinates() {
        List<Tile> tiles = Arrays.asList(
                new Tile(TileType.FOREST, 1, 0),
                new Tile(TileType.HILLS, 0, 0),
                new Tile(TileType.PASTURE, 1, -1)
        );
        Vertex vertex = new Vertex(tiles);

        assertEquals("0,0|1,-1|1,0", vertex.getKey());
    }

    // TC2 – Constructor sets adjacentTiles correctly
    @Test
    void constructor_setsAdjacentTiles() {
        List<Tile> tiles = Arrays.asList(
                new Tile(TileType.FOREST, 0, 0),
                new Tile(TileType.HILLS, 1, -1),
                new Tile(TileType.PASTURE, 1, 0)
        );
        Vertex vertex = new Vertex(tiles);

        assertEquals(tiles, vertex.getAdjacentTiles());
    }

    // TC3 – Interior vertex has exactly 3 adjacent tiles
    // BVA: 3 is the maximum adjacent tile count; boundary from below: 2
    @Test
    void constructor_interiorVertex_hasThreeAdjacentTiles() {
        List<Tile> tiles = Arrays.asList(
                new Tile(TileType.FOREST, 0, 0),
                new Tile(TileType.HILLS, 1, -1),
                new Tile(TileType.PASTURE, 1, 0)
        );
        Vertex vertex = new Vertex(tiles);

        assertEquals(3, vertex.getAdjacentTiles().size());
    }

    // TC4 – Coastal vertex has exactly 1 adjacent tile
    // BVA: 1 is the minimum adjacent tile count; boundary from above: 2
    @Test
    void constructor_coastalVertex_hasOneAdjacentTile() {
        List<Tile> tiles = Collections.singletonList(new Tile(TileType.FOREST, -2, 0));
        Vertex vertex = new Vertex(tiles);

        assertEquals(1, vertex.getAdjacentTiles().size());
    }
}
package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private static final int[][] NEIGHBOR_OFFSETS = {
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {-1, 1}
    };

    private static final int[][] POSITIONS = {
        {-2, 0}, {-2, 1}, {-2, 2},
        {-1, -1}, {-1, 0}, {-1, 1}, {-1, 2},
        {0, -2}, {0, -1}, {0, 0}, {0, 1}, {0, 2},
        {1, -2}, {1, -1}, {1, 0}, {1, 1},
        {2, -2}, {2, -1}, {2, 0}
    };

    private static final List<TileType> TILE_DISTRIBUTION = Arrays.asList(
        TileType.FOREST, TileType.FOREST, TileType.FOREST, TileType.FOREST,
        TileType.PASTURE, TileType.PASTURE, TileType.PASTURE, TileType.PASTURE,
        TileType.FIELDS, TileType.FIELDS, TileType.FIELDS, TileType.FIELDS,
        TileType.HILLS, TileType.HILLS, TileType.HILLS,
        TileType.MOUNTAINS, TileType.MOUNTAINS, TileType.MOUNTAINS,
        TileType.DESERT
    );

    private static final List<Integer> TOKEN_DISTRIBUTION = Arrays.asList(
        2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
    );

    private final Shuffler shuffler;
    private Map<String, Tile> tiles;
    private Map<String, Vertex> vertices;
    private Map<String, Edge> edges;

    public Board(Shuffler shuffler) {
        this.shuffler = shuffler;
    }

    public void create() {
        List<TileType> types = new ArrayList<>(TILE_DISTRIBUTION);
        shuffler.shuffle(types);

        tiles = new HashMap<>();
        for (int i = 0; i < POSITIONS.length; i++) {
            int q = POSITIONS[i][0];
            int r = POSITIONS[i][1];
            tiles.put(key(q, r), new Tile(types.get(i), q, r));
        }

        List<Integer> tokens = new ArrayList<>(TOKEN_DISTRIBUTION);
        shuffler.shuffle(tokens);

        int tokenIndex = 0;
        for (Tile tile : tiles.values()) {
            if (tile.getTileType() == TileType.DESERT) {
                tile.setHasRobber(true);
            } else {
                tile.setNumberToken(tokens.get(tokenIndex++));
            }
        }
    }

    public Collection<Tile> getTiles() {
        return tiles.values();
    }

    public Tile getTile(int q, int r) {
        return tiles.get(key(q, r));
    }

    public List<Tile> getNeighbors(int q, int r) {
        List<Tile> neighbors = new ArrayList<>();
        for (int[] offset : NEIGHBOR_OFFSETS) {
            Tile neighbor = tiles.get(key(q + offset[0], r + offset[1]));
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    public Collection<Vertex> getVertices() {
        return vertices.values();
    }

    public Vertex getVertex(String key) {
        return vertices.get(key);
    }

    public Collection<Edge> getEdges() {
        return edges.values();
    }

    public Edge getEdge(String key) {
        return edges.get(key);
    }

    private static String key(int q, int r) {
        return q + "," + r;
    }
}
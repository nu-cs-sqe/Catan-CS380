package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
  private static final int HEX_TO_PIXEL_Q_SCALE = 2;
  private static final int HEX_TO_PIXEL_R_SCALE = -3;
  private static final int VERTICES_PER_TILE = 6;

  private static final int[][] NEIGHBOR_OFFSETS = {
    {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {-1, 1}
  };

  private static final int[][] POSITIONS = {
    {-2, 0}, {-2, 1}, {-2, 2}, {-1, -1}, {-1, 0}, {-1, 1}, {-1, 2}, {0, -2}, {0, -1}, {0, 0},
    {0, 1}, {0, 2}, {1, -2}, {1, -1}, {1, 0}, {1, 1}, {2, -2}, {2, -1}, {2, 0}
  };

  private static final int[][] CORNER_OFFSETS = {
    {1, 1}, {0, 2}, {-1, 1},
    {-1, -1}, {0, -2}, {1, -1}
  };

  private static final List<TileType> TILE_DISTRIBUTION =
      Arrays.asList(
          TileType.FOREST,
          TileType.FOREST,
          TileType.FOREST,
          TileType.FOREST,
          TileType.PASTURE,
          TileType.PASTURE,
          TileType.PASTURE,
          TileType.PASTURE,
          TileType.FIELDS,
          TileType.FIELDS,
          TileType.FIELDS,
          TileType.FIELDS,
          TileType.HILLS,
          TileType.HILLS,
          TileType.HILLS,
          TileType.MOUNTAINS,
          TileType.MOUNTAINS,
          TileType.MOUNTAINS,
          TileType.DESERT);

  private static final List<Integer> TOKEN_DISTRIBUTION =
      Arrays.asList(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11);

  private static final List<Harbor> HARBOR_DEFINITIONS =
      Arrays.asList(
          new Harbor(ResourceType.GENERIC, 3, "-5,-1", "-5,1"),
          new Harbor(ResourceType.WHEAT, 2, "-4,-4", "-4,-2"),
          new Harbor(ResourceType.GENERIC, 3, "-3,-7", "-3,-5"),
          new Harbor(ResourceType.WOOD, 2, "-1,-5", "0,-4"),
          new Harbor(ResourceType.GENERIC, 3, "2,-4", "3,-5"),
          new Harbor(ResourceType.BRICK, 2, "3,-1", "4,-2"),
          new Harbor(ResourceType.ORE, 2, "3,5", "3,7"),
          new Harbor(ResourceType.GENERIC, 3, "-2,4", "-1,5"),
          new Harbor(ResourceType.SHEEP, 2, "-4,2", "-3,1"));

  private final Shuffler shuffler;
  private Map<String, Tile> tiles;
  private Map<String, Vertex> vertices;
  private Map<String, Edge> edges;
  private Map<String, Harbor> harbors;

  public Board(Shuffler shuffler) {
    this.shuffler = shuffler;
  }

  public void create() {
    createTiles();
    createVerticesAndEdges();
    createHarbors();
  }

  private void createTiles() {
    List<TileType> types = new ArrayList<>(TILE_DISTRIBUTION);
    shuffler.shuffle(types);
    tiles = new HashMap<>();
    for (int i = 0; i < POSITIONS.length; i++) {
      int q = POSITIONS[i][0];
      int r = POSITIONS[i][1];
      tiles.put(key(q, r), new Tile(types.get(i), q, r));
    }
  }

  private void createVerticesAndEdges() {
    vertices = new HashMap<>();
    edges = new HashMap<>();
    int tokenIndex = 0;
    for (int[] pos : POSITIONS) {
      Tile tile = tiles.get(key(pos[0], pos[1]));
      tokenIndex = assignToken(tile, tokenIndex);
      int cx = HEX_TO_PIXEL_Q_SCALE * tile.getQ() + tile.getR();
      int cy = HEX_TO_PIXEL_R_SCALE * tile.getR();
      addVerticesForTile(tile, cx, cy);
      addEdgesForTile(tile, cx, cy);
    }
  }

  private int assignToken(Tile tile, int tokenIndex) {
    if (tile.getTileType() != TileType.DESERT) {
      tile.setNumberToken(TOKEN_DISTRIBUTION.get(tokenIndex++));
    }
    return tokenIndex;
  }

  private void addVerticesForTile(Tile tile, int cx, int cy) {
    for (int[] offset : CORNER_OFFSETS) {
      int vx = cx + offset[0];
      int vy = cy + offset[1];
      String key = vx + "," + vy;
      Vertex v = vertices.computeIfAbsent(key, k -> new Vertex(key));
      v.addTile(tile);
    }
  }

  private void addEdgesForTile(Tile tile, int cx, int cy) {
    for (int i = 0; i < VERTICES_PER_TILE; i++) {
      int next = (i + 1) % VERTICES_PER_TILE;
      int x1 = cx + CORNER_OFFSETS[i][0];
      int y1 = cy + CORNER_OFFSETS[i][1];
      int x2 = cx + CORNER_OFFSETS[next][0];
      int y2 = cy + CORNER_OFFSETS[next][1];
      String edgeKey = generateEdgeKey(x1, y1, x2, y2);
      Edge e = edges.computeIfAbsent(edgeKey, k -> new Edge(edgeKey));
      e.addTile(tile);
    }
  }

  private void createHarbors() {
    harbors = new HashMap<>();
    for (Harbor harbor : HARBOR_DEFINITIONS) {
      String key = harbor.getVertex1Id() + "|" + harbor.getVertex2Id();
      harbors.put(key, harbor);
      Vertex v1 = vertices.get(harbor.getVertex1Id());
      Vertex v2 = vertices.get(harbor.getVertex2Id());
      if (v1 != null) {
        v1.setHarbor(harbor);
      }
      if (v2 != null) {
        v2.setHarbor(harbor);
      }
    }
  }

  private String generateEdgeKey(int x1, int y1, int x2, int y2) {
    if (x1 < x2 || (x1 == x2 && y1 < y2)) {
      return x1 + "," + y1 + "|" + x2 + "," + y2;
    } else {
      return x2 + "," + y2 + "|" + x1 + "," + y1;
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

  public Collection<Harbor> getHarbors() {
    return harbors.values();
  }

  public Harbor getHarbor(String vertex1Id, String vertex2Id) {
    return harbors.get(vertex1Id + "|" + vertex2Id);
  }

  private static String key(int q, int r) {
    return q + "," + r;
  }
}

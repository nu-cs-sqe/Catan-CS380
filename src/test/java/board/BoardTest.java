package board;

import domain.Resource;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EasyMockExtension.class)
class BoardTest {

  private static final int TOTAL_TILES = 19;
  private static final int FOREST_COUNT = 4;
  private static final int PASTURE_COUNT = 4;
  private static final int FIELDS_COUNT = 4;
  private static final int HILLS_COUNT = 3;
  private static final int MOUNTAINS_COUNT = 3;
  private static final int TOTAL_TOKENS = 18;
  private static final int TOTAL_VERTICES = 54;
  private static final int INTERIOR_VERTEX_COUNT = 24;
  private static final int COASTAL_VERTEX_COUNT = 30;
  private static final int MAX_ADJACENT_TILES_VERTEX = 3;
  private static final int TOTAL_EDGES = 72;
  private static final int INTERIOR_EDGE_COUNT = 42;
  private static final int COASTAL_EDGE_COUNT = 30;
  private static final int MAX_ADJACENT_TILES_EDGE = 2;
  private static final int TOTAL_HARBORS = 9;
  private static final int GENERIC_HARBOR_COUNT = 4;
  private static final int HARBOR_VERTEX_COUNT = 18;
  private static final int RESOURCE_HARBOR_RATE = 2;
  private static final int GENERIC_HARBOR_RATE = 3;
  private static final int MIN_TOKEN = 2;
  private static final int MAX_TOKEN = 12;
  private static final int FIRST_TOKEN = 5;
  private static final int LAST_TOKEN = 11;
  private static final int CENTER_NEIGHBOR_COUNT = 6;
  private static final int CORNER_NEIGHBOR_COUNT = 3;
  private static final int MIN_COORD = -2;
  private static final int MIN_Q = MIN_COORD;
  private static final int MAX_Q = 2;
  private static final int OUTSIDE_Q = 3;
  private static final int OFF_BOARD_COORD = 5;
  private static final int TOKEN_THREE = 3;
  private static final int TOKEN_FOUR = 4;
  private static final int TOKEN_SIX = 6;
  private static final int TOKEN_EIGHT = 8;
  private static final int TOKEN_NINE = 9;
  private static final int TOKEN_TEN = 10;

  @Mock private Shuffler shuffler;
  private Board board;

  @BeforeEach
  public void setUp() {
    board = new Board(shuffler);
  }

  // TC1 – Total tile count is exactly 19
  @Test
  void create_tileCount_exactlyNineteen() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(TOTAL_TILES, board.getTiles().size());

    verify(shuffler);
  }

  // TC2 – No tile in the list is null
  @Test
  void create_allTilesNonNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    for (Tile tile : board.getTiles()) {
      assertNotNull(tile);
    }

    verify(shuffler);
  }

  // TC3 – FOREST tile count is exactly 4
  @Test
  void create_resourceDistribution_fourForestTiles() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = countByResource(TileType.FOREST);
    assertEquals(FOREST_COUNT, count);

    verify(shuffler);
  }

  // TC4 – PASTURE tile count is exactly 4
  @Test
  void create_resourceDistribution_fourPastureTiles() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = countByResource(TileType.PASTURE);
    assertEquals(PASTURE_COUNT, count);

    verify(shuffler);
  }

  // TC5 – FIELDS tile count is exactly 4
  @Test
  void create_resourceDistribution_fourFieldsTiles() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = countByResource(TileType.FIELDS);
    assertEquals(FIELDS_COUNT, count);

    verify(shuffler);
  }

  // TC6 – HILLS tile count is exactly 3
  @Test
  void create_resourceDistribution_threeHillsTiles() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = countByResource(TileType.HILLS);
    assertEquals(HILLS_COUNT, count);

    verify(shuffler);
  }

  // TC7 – MOUNTAINS tile count is exactly 3
  @Test
  void create_resourceDistribution_threeMountainsTiles() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = countByResource(TileType.MOUNTAINS);
    assertEquals(MOUNTAINS_COUNT, count);

    verify(shuffler);
  }

  // TC8 – DESERT tile count is exactly 1
  @Test
  void create_resourceDistribution_oneDesertTile() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = countByResource(TileType.DESERT);
    assertEquals(1, count);

    verify(shuffler);
  }

  // TC9 – Desert tile has no number token (token = 0)
  @Test
  void create_desertTile_hasNoNumberToken() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    Tile desert = null;
    for (Tile t : board.getTiles()) {
      if (t.getTileType() == TileType.DESERT) {
        desert = t;
        break;
      }
    }
    assertNotNull(desert);
    assertEquals(0, desert.getNumberToken());

    verify(shuffler);
  }

  // TC10 – All non-desert tiles have a number token >= 2
  @Test
  void create_nonDesertTiles_allHaveNumberToken() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    for (Tile tile : board.getTiles()) {
      if (tile.getTileType() != TileType.DESERT) {
        assertTrue(
            tile.getNumberToken() >= 2, "Expected token >= 2 but was " + tile.getNumberToken());
      }
    }

    verify(shuffler);
  }

  // TC11 – Token sequence for non-desert tiles in board position order matches fixed distribution
  @Test
  void create_tokenSequence_matchesFixedDistribution() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    List<Integer> expected =
        Arrays.asList(
            FIRST_TOKEN,
            MIN_TOKEN,
            TOKEN_THREE,
            TOKEN_SIX,
            TOKEN_EIGHT,
            TOKEN_TEN,
            TOKEN_NINE,
            MAX_TOKEN,
            LAST_TOKEN,
            TOKEN_FOUR,
            TOKEN_EIGHT,
            TOKEN_TEN,
            TOKEN_NINE,
            TOKEN_FOUR,
            FIRST_TOKEN,
            TOKEN_SIX,
            TOKEN_THREE,
            LAST_TOKEN);
    assertEquals(expected, tokenSequence());

    verify(shuffler);
  }

  // TC12 – First token in sequence is 5
  // BVA: 5 is the value at position 0 of TOKEN_DISTRIBUTION; boundary from below: no token, from
  // above: next value (2)
  @Test
  void create_tokenSequence_firstTokenIsFive() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(FIRST_TOKEN, tokenSequence().get(0));

    verify(shuffler);
  }

  // TC13 – Last token in sequence is 11
  // BVA: 11 is the value at position 17 of TOKEN_DISTRIBUTION; boundary from above: no token, from
  // below: previous value (3)
  @Test
  void create_tokenSequence_lastTokenIsEleven() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    List<Integer> seq = tokenSequence();
    assertEquals(LAST_TOKEN, seq.get(seq.size() - 1));

    verify(shuffler);
  }

  // TC14 – Total number token count across all tiles is exactly 18
  @Test
  void create_numberTokens_totalIsEighteen() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Tile t : board.getTiles()) {
      if (t.getNumberToken() > 0) {
        count++;
      }
    }
    assertEquals(TOTAL_TOKENS, count);

    verify(shuffler);
  }

  // TC15 – Total vertex count is exactly 54
  // BVA: 54 is the only valid count (boundary from below: 53, from above: 55)
  @Test
  void create_vertexCount_exactlyFiftyFour() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(TOTAL_VERTICES, board.getVertices().size());

    verify(shuffler);
  }

  // TC16 – Interior vertex count (3 adjacent tiles) is exactly 24
  // BVA: boundary from below: 23, from above: 25
  @Test
  void create_interiorVertexCount_exactlyTwentyFour() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Vertex v : board.getVertices()) {
      if (v.getAdjacentTiles().size() == MAX_ADJACENT_TILES_VERTEX) {
        count++;
      }
    }
    assertEquals(INTERIOR_VERTEX_COUNT, count);

    verify(shuffler);
  }

  // TC17 – Coastal vertex count (fewer than 3 adjacent tiles) is exactly 30
  // BVA: boundary from below: 29, from above: 31
  @Test
  void create_coastalVertexCount_exactlyThirty() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Vertex v : board.getVertices()) {
      if (v.getAdjacentTiles().size() < MAX_ADJACENT_TILES_VERTEX) {
        count++;
      }
    }
    assertEquals(COASTAL_VERTEX_COUNT, count);

    verify(shuffler);
  }

  // TC18 – Total edge count is exactly 72
  // BVA: 72 is the only valid count (boundary from below: 71, from above: 73)
  @Test
  void create_edgeCount_exactlySeventyTwo() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(TOTAL_EDGES, board.getEdges().size());

    verify(shuffler);
  }

  // TC19 – Interior edge count (2 adjacent tiles) is exactly 42
  // BVA: boundary from below: 41, from above: 43
  @Test
  void create_interiorEdgeCount_exactlyFortyTwo() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Edge e : board.getEdges()) {
      if (e.getAdjacentTiles().size() == MAX_ADJACENT_TILES_EDGE) {
        count++;
      }
    }
    assertEquals(INTERIOR_EDGE_COUNT, count);

    verify(shuffler);
  }

  // TC20 – Coastal edge count (1 adjacent tile) is exactly 30
  // BVA: boundary from below: 29, from above: 31
  @Test
  void create_coastalEdgeCount_exactlyThirty() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Edge e : board.getEdges()) {
      if (e.getAdjacentTiles().size() == 1) {
        count++;
      }
    }
    assertEquals(COASTAL_EDGE_COUNT, count);

    verify(shuffler);
  }

  // TC21 – Harbor count is exactly 9
  // BVA: boundaries 8, 9, 10
  @Test
  void create_harborCount_exactlyNine() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(TOTAL_HARBORS, board.getHarbors().size());

    verify(shuffler);
  }

  // TC22 – Exactly 4 harbors have type GENERIC
  @Test
  void create_harborDistribution_fourGenericHarbors() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Harbor h : board.getHarbors()) {
      if (h.getHarborType() == Resource.GENERIC) {
        count++;
      }
    }
    assertEquals(GENERIC_HARBOR_COUNT, count);

    verify(shuffler);
  }

  // TC23 – Exactly 1 harbor each for WOOD, BRICK, SHEEP, WHEAT, ORE
  @Test
  void create_harborDistribution_oneEachResourceHarbor() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    for (Resource type :
        new Resource[] {
          Resource.WOOD, Resource.BRICK, Resource.SHEEP, Resource.WHEAT, Resource.ORE
        }) {
      long count = 0;
      for (Harbor h : board.getHarbors()) {
        if (h.getHarborType() == type) {
          count++;
        }
      }
      assertEquals(1, count, "Expected exactly 1 harbor of type " + type);
    }

    verify(shuffler);
  }

  // TC24 – Exactly 18 vertices have a non-null harbor
  // BVA: boundaries 17, 18, 19
  @Test
  void create_harborVertexCount_exactlyEighteen() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Vertex v : board.getVertices()) {
      if (v.getHarbor() != null) {
        count++;
      }
    }
    assertEquals(HARBOR_VERTEX_COUNT, count);

    verify(shuffler);
  }

  // TC25 – Each resource-specific harbor has exchange rate 2
  // BVA: min exchange rate
  @Test
  void create_resourceHarbors_exchangeRateIsTwo() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    for (Harbor h : board.getHarbors()) {
      if (h.getHarborType() != Resource.GENERIC) {
        assertEquals(
            RESOURCE_HARBOR_RATE,
            h.getExchangeRate(),
            "Expected exchange rate 2 for resource harbor " + h.getHarborType());
      }
    }

    verify(shuffler);
  }

  // TC26 – Each GENERIC harbor has exchange rate 3
  // BVA: boundary between 2 and 3
  @Test
  void create_genericHarbors_exchangeRateIsThree() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    for (Harbor h : board.getHarbors()) {
      if (h.getHarborType() == Resource.GENERIC) {
        assertEquals(GENERIC_HARBOR_RATE, h.getExchangeRate());
      }
    }

    verify(shuffler);
  }

  // TC27 – Token value 2 appears exactly once across all tiles
  // BVA: lower boundary of token value range
  @Test
  void create_tokenValue_twoAppearsExactlyOnce() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Tile t : board.getTiles()) {
      if (t.getNumberToken() == MIN_TOKEN) {
        count++;
      }
    }
    assertEquals(1, count);

    verify(shuffler);
  }

  // TC28 – Token value 12 appears exactly once across all tiles
  // BVA: upper boundary of token value range
  @Test
  void create_tokenValue_twelveAppearsExactlyOnce() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Tile t : board.getTiles()) {
      if (t.getNumberToken() == MAX_TOKEN) {
        count++;
      }
    }
    assertEquals(1, count);

    verify(shuffler);
  }

  // TC29 – getTile(0, 0) returns a non-null tile at center position
  @Test
  void getTile_centerPosition_returnsNonNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNotNull(board.getTile(0, 0));

    verify(shuffler);
  }

  // TC30 – getTile(-2, 0) returns a non-null tile at min-q boundary
  // BVA: lower boundary of q range
  @Test
  void getTile_minQBoundary_returnsNonNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNotNull(board.getTile(MIN_Q, 0));

    verify(shuffler);
  }

  // TC31 – getTile(2, 0) returns a non-null tile at max-q boundary
  // BVA: upper boundary of q range
  @Test
  void getTile_maxQBoundary_returnsNonNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNotNull(board.getTile(MAX_Q, 0));

    verify(shuffler);
  }

  // TC32 – getTile(3, 0) returns null just outside max-q boundary
  // BVA: one past upper boundary
  @Test
  void getTile_justOutsideMaxQ_returnsNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNull(board.getTile(OUTSIDE_Q, 0));

    verify(shuffler);
  }

  // TC33 – getNeighbors(0, 0) returns 6 neighbors for center tile
  // BVA: max neighbor count
  @Test
  void getNeighbors_centerTile_returnsSixNeighbors() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(CENTER_NEIGHBOR_COUNT, board.getNeighbors(0, 0).size());

    verify(shuffler);
  }

  // TC34 – getNeighbors(-2, 0) returns 3 neighbors for corner tile
  // BVA: min neighbor count for a valid board tile
  @Test
  void getNeighbors_cornerTile_returnsThreeNeighbors() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(CORNER_NEIGHBOR_COUNT, board.getNeighbors(MIN_Q, 0).size());

    verify(shuffler);
  }

  // TC35 – getNeighbors(5, 5) returns empty list for off-board position
  // BVA: minimum = 0 neighbors
  @Test
  void getNeighbors_offBoardPosition_returnsEmpty() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertTrue(board.getNeighbors(OFF_BOARD_COORD, OFF_BOARD_COORD).isEmpty());

    verify(shuffler);
  }

  // TC36 – getVertex with a known key returns a non-null Vertex
  @Test
  void getVertex_knownKey_returnsNonNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNotNull(board.getVertex("-1,1"));

    verify(shuffler);
  }

  // TC37 – getVertex with an unknown key returns null
  // BVA: null boundary - vertex does not exist
  @Test
  void getVertex_unknownKey_returnsNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNull(board.getVertex("99,99"));

    verify(shuffler);
  }

  // TC38 – getEdge with a known key returns a non-null Edge
  @Test
  void getEdge_knownKey_returnsNonNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNotNull(board.getEdge("-1,-1|-1,1"));

    verify(shuffler);
  }

  // TC39 – getEdge with an unknown key returns null
  // BVA: null boundary - edge does not exist
  @Test
  void getEdge_unknownKey_returnsNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNull(board.getEdge("99,99|100,100"));

    verify(shuffler);
  }

  // TC40 – getHarbor with a known vertex pair returns a non-null Harbor of type GENERIC
  @Test
  void getHarbor_knownVertexPair_returnsGenericHarbor() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    Harbor harbor = board.getHarbor("-3,-7", "-2,-8");
    assertNotNull(harbor);
    assertEquals(Resource.GENERIC, harbor.getHarborType());

    verify(shuffler);
  }

  // TC31 – harbor whose vertex2Id matches no vertex: harbor still registered, v2 null branch taken
  @Test
  void createHarbors_vertex2NotFound_harborStillRegistered() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    Harbor testHarbor = new Harbor(Resource.GENERIC, 3, "-5,-1", "99,99");
    Board boardWithCustomHarbors = new Board(shuffler, Collections.singletonList(testHarbor));
    boardWithCustomHarbors.create();

    assertTrue(boardWithCustomHarbors.getHarbors().contains(testHarbor));
    assertNull(boardWithCustomHarbors.getVertex("99,99"));

    verify(shuffler);
  }

  // TC30 – harbor whose vertex1Id matches no vertex: harbor still registered, v1 null branch taken
  @Test
  void createHarbors_vertex1NotFound_harborStillRegistered() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    Harbor testHarbor = new Harbor(Resource.GENERIC, 3, "99,99", "-5,1");
    Board boardWithCustomHarbors = new Board(shuffler, Collections.singletonList(testHarbor));
    boardWithCustomHarbors.create();

    assertTrue(boardWithCustomHarbors.getHarbors().contains(testHarbor));
    assertNull(boardWithCustomHarbors.getVertex("99,99"));

    verify(shuffler);
  }

  // TC41 – getHarbor with an interior vertex pair returns null
  // BVA: null boundary - no harbor at this location
  @Test
  void getHarbor_interiorVertexPair_returnsNull() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertNull(board.getHarbor("0,0", "2,0"));

    verify(shuffler);
  }

  // Helpers

  private long countByResource(TileType type) {
    long count = 0;
    for (Tile t : board.getTiles()) {
      if (t.getTileType() == type) {
        count++;
      }
    }
    return count;
  }

  private List<Integer> tokenSequence() {
    int[][] positions = {
      {MIN_COORD, 0},
      {MIN_COORD, 1},
      {MIN_COORD, MAX_Q},
      {-1, -1},
      {-1, 0},
      {-1, 1},
      {-1, MAX_Q},
      {0, MIN_COORD},
      {0, -1},
      {0, 0},
      {0, 1},
      {0, MAX_Q},
      {1, MIN_COORD},
      {1, -1},
      {1, 0},
      {1, 1},
      {MAX_Q, MIN_COORD},
      {MAX_Q, -1},
      {MAX_Q, 0}
    };
    List<Integer> sequence = new ArrayList<>();
    for (int[] pos : positions) {
      Tile tile = board.getTile(pos[0], pos[1]);
      if (tile.getTileType() != TileType.DESERT) {
        sequence.add(tile.getNumberToken());
      }
    }
    return sequence;
  }
}

package board;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EasyMockExtension.class)
class BoardTest {

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

    assertEquals(19, board.getTiles().size());

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
    assertEquals(4, count);

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
    assertEquals(4, count);

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
    assertEquals(4, count);

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
    assertEquals(3, count);

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
    assertEquals(3, count);

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
        Arrays.asList(5, 2, 6, 3, 8, 10, 9, 12, 11, 4, 8, 10, 9, 4, 5, 6, 3, 11);
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

    assertEquals(5, tokenSequence().get(0));

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
    assertEquals(11, seq.get(seq.size() - 1));

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
    assertEquals(18, count);

    verify(shuffler);
  }

  // TC15 – Robber starts on the desert tile
  @Test
  void create_robber_startsOnDesert() {
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
    assertTrue(desert.hasRobber());

    verify(shuffler);
  }

  // TC16 – Non-desert tiles do not have the robber
  @Test
  void create_robber_notOnNonDesertTiles() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long robberCount = 0;
    for (Tile t : board.getTiles()) {
      if (t.getTileType() != TileType.DESERT && t.hasRobber()) {
        robberCount++;
      }
    }
    assertEquals(0, robberCount);

    verify(shuffler);
  }

  // TC17 – Exactly one tile has the robber
  @Test
  void create_robber_exactlyOneTileHasRobber() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long robberCount = 0;
    for (Tile t : board.getTiles()) {
      if (t.hasRobber()) {
        robberCount++;
      }
    }
    assertEquals(1, robberCount);

    verify(shuffler);
  }

  // TC18 – Total vertex count is exactly 54
  // BVA: 54 is the only valid count (boundary from below: 53, from above: 55)
  @Test
  void create_vertexCount_exactlyFiftyFour() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(54, board.getVertices().size());

    verify(shuffler);
  }

  // TC19 – Interior vertex count (3 adjacent tiles) is exactly 24
  // BVA: boundary from below: 23, from above: 25
  @Test
  void create_interiorVertexCount_exactlyTwentyFour() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Vertex v : board.getVertices()) {
      if (v.getAdjacentTiles().size() == 3) {
        count++;
      }
    }
    assertEquals(24, count);

    verify(shuffler);
  }

  // TC20 – Coastal vertex count (fewer than 3 adjacent tiles) is exactly 30
  // BVA: boundary from below: 29, from above: 31
  @Test
  void create_coastalVertexCount_exactlyThirty() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Vertex v : board.getVertices()) {
      if (v.getAdjacentTiles().size() < 3) {
        count++;
      }
    }
    assertEquals(30, count);

    verify(shuffler);
  }

  // TC21 – Total edge count is exactly 72
  // BVA: 72 is the only valid count (boundary from below: 71, from above: 73)
  @Test
  void create_edgeCount_exactlySeventyTwo() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    assertEquals(72, board.getEdges().size());

    verify(shuffler);
  }

  // TC22 – Interior edge count (2 adjacent tiles) is exactly 42
  // BVA: boundary from below: 41, from above: 43
  @Test
  void create_interiorEdgeCount_exactlyFortyTwo() {
    shuffler.shuffle(EasyMock.anyObject());
    expectLastCall().anyTimes();
    replay(shuffler);

    board.create();

    long count = 0;
    for (Edge e : board.getEdges()) {
      if (e.getAdjacentTiles().size() == 2) {
        count++;
      }
    }
    assertEquals(42, count);

    verify(shuffler);
  }

  // TC23 – Coastal edge count (1 adjacent tile) is exactly 30
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
    assertEquals(30, count);

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
      {-2, 0}, {-2, 1}, {-2, 2},
      {-1, -1}, {-1, 0}, {-1, 1}, {-1, 2},
      {0, -2}, {0, -1}, {0, 0}, {0, 1}, {0, 2},
      {1, -2}, {1, -1}, {1, 0}, {1, 1},
      {2, -2}, {2, -1}, {2, 0}
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

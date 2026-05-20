package board;

import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EasyMockExtension.class)
class BoardTest {

    @Mock
    private Shuffler shuffler;
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

        Tile desert = board.getTiles().stream()
                .filter(t -> t.getTileType() == TileType.DESERT)
                .findFirst()
                .orElseThrow();
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

        List<Tile> nonDesert = board.getTiles().stream()
                .filter(t -> t.getTileType() != TileType.DESERT)
                .collect(Collectors.toList());
        for (Tile tile : nonDesert) {
            assertTrue(tile.getNumberToken() >= 2,
                    "Expected token >= 2 but was " + tile.getNumberToken());
        }

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

    private long countByToken(int token) {
        long count = 0;
        for (Tile t : board.getTiles()) {
            if (t.getNumberToken() == token) {
                count++;
            }
        }
        return count;
    }
}
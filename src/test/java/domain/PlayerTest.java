package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerTest {

    private static final String DEFAULT_NAME = "Alice";
    private static final PlayerColor DEFAULT_COLOR = PlayerColor.RED;
    private static final int STARTING_SETTLEMENTS = 5;
    private static final int STARTING_CITIES = 4;
    private static final int STARTING_ROADS = 15;

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(DEFAULT_NAME, DEFAULT_COLOR);
    }

    // BVA TC1
    @Test
    void shouldExposeNameAndColor_whenConstructedWithValidArguments() {
        assertAll(
                () -> assertEquals(DEFAULT_NAME, player.getName()),
                () -> assertEquals(DEFAULT_COLOR, player.getColor())
        );
    }

    // BVA TC2
    @Test
    void shouldThrowNullPointer_whenNameIsNull() {
        assertThrows(NullPointerException.class,
                () -> new Player(null, DEFAULT_COLOR));
    }

    // BVA TC3
    @Test
    void shouldThrowIllegalArgument_whenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("", DEFAULT_COLOR));
    }

    // BVA TC4
    @Test
    void shouldThrowIllegalArgument_whenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("   ", DEFAULT_COLOR));
    }

    // BVA TC5
    @Test
    void shouldThrowNullPointer_whenColorIsNull() {
        assertThrows(NullPointerException.class,
                () -> new Player(DEFAULT_NAME, null));
    }

    // BVA TC6
    @Test
    void shouldStartWithZeroVpAndZeroResourcesAndFullPieces_whenFreshlyConstructed() {
        assertAll(
                () -> assertEquals(0, player.getVictoryPoints()),
                () -> assertEquals(0, player.getResourceCount(Resource.BRICK)),
                () -> assertEquals(0, player.getResourceCount(Resource.LUMBER)),
                () -> assertEquals(0, player.getResourceCount(Resource.WOOL)),
                () -> assertEquals(0, player.getResourceCount(Resource.GRAIN)),
                () -> assertEquals(0, player.getResourceCount(Resource.ORE)),
                () -> assertEquals(STARTING_SETTLEMENTS, player.getRemainingSettlements()),
                () -> assertEquals(STARTING_CITIES, player.getRemainingCities()),
                () -> assertEquals(STARTING_ROADS, player.getRemainingRoads())
        );
    }

    // BVA TC7
    @Test
    void shouldIncreaseResourceCountToOne_whenAddingOneToEmptyHand() {
        player.addResource(Resource.BRICK, 1);
        assertEquals(1, player.getResourceCount(Resource.BRICK));
    }

    // BVA TC8
    @Test
    void shouldLeaveResourceCountUnchanged_whenAddingZero() {
        player.addResource(Resource.BRICK, 0);
        assertEquals(0, player.getResourceCount(Resource.BRICK));
    }

    // BVA TC9
    @Test
    void shouldThrowIllegalArgument_whenAddingNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> player.addResource(Resource.BRICK, -1));
    }

    // BVA TC10
    @Test
    void shouldThrowNullPointer_whenAddingNullResource() {
        assertThrows(NullPointerException.class,
                () -> player.addResource(null, 1));
    }

    // BVA TC11
    @Test
    void shouldAccumulateResourceCount_whenAddingSameResourceMultipleTimes() {
        player.addResource(Resource.BRICK, 3);
        player.addResource(Resource.BRICK, 2);
        assertEquals(5, player.getResourceCount(Resource.BRICK));
    }
}

package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerTest {

    @Test
    void shouldConstructPlayer_whenNameAndColorAreValid() {
        domain.Player player = new domain.Player("Alice", PlayerColor.RED);
        assertAll(
                () -> assertEquals("Alice", player.getName()),
                () -> assertEquals(PlayerColor.RED, player.getColor())
        );
    }

    @Test
    void shouldThrowNullPointer_whenNameIsNull() {
        assertThrows(NullPointerException.class,
                () -> new domain.Player(null, PlayerColor.RED));
    }

    @Test
    void shouldThrowIllegalArgument_whenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new domain.Player("", PlayerColor.RED));
    }

    @Test
    void shouldThrowIllegalArgument_whenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new domain.Player("   ", PlayerColor.RED));
    }

    @Test
    void shouldThrowNullPointer_whenColorIsNull() {
        assertThrows(NullPointerException.class,
                () -> new domain.Player("Alice", null));
    }

    @Test
    void shouldHaveZeroVpAndZeroResourcesAndFullPieces_whenFreshlyConstructed() {
        domain.Player player = new domain.Player("Alice", PlayerColor.RED);
        assertAll(
                () -> assertEquals(0, player.getVictoryPoints()),
                () -> assertEquals(0, player.getResourceCount(Resource.BRICK)),
                () -> assertEquals(0, player.getResourceCount(Resource.LUMBER)),
                () -> assertEquals(0, player.getResourceCount(Resource.WOOL)),
                () -> assertEquals(0, player.getResourceCount(Resource.GRAIN)),
                () -> assertEquals(0, player.getResourceCount(Resource.ORE)),
                () -> assertEquals(5, player.getRemainingSettlements()),
                () -> assertEquals(4, player.getRemainingCities()),
                () -> assertEquals(15, player.getRemainingRoads())
        );
    }
}

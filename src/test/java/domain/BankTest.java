package domain;

import board.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BankTest {

    private static final int INITIAL_STOCK = 19;
    private static final int INITIAL_DEV_CARD_COUNT = 25;
    private static final int KNIGHT_COUNT = 14;
    private static final int VICTORY_POINT_COUNT = 5;
    private static final int ACTION_CARD_COUNT = 2;

    // TC1 - Initial resource stock is 19 per resource
    @Test
    public void initialStockIs19ForEachResource() {
        Bank bank = new Bank(list -> { });
        assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.WOOD));
        assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.BRICK));
        assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.SHEEP));
        assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.WHEAT));
        assertEquals(INITIAL_STOCK, bank.getStock(ResourceType.ORE));
    }

    // TC2 - Initial development card count is 25
    @Test
    public void initialDevCardCountIs25() {
        Bank bank = new Bank(list -> { });
        assertEquals(INITIAL_DEV_CARD_COUNT, bank.getDevCardCount());
    }

    // TC5 - Null resource in getStock throws NullPointerException
    @Test
    public void nullResourceInGetStockThrowsNullPointerException() {
        Bank bank = new Bank(list -> { });
        assertThrows(NullPointerException.class, () -> bank.getStock(null));
    }

    // TC4 - Null shuffler throws NullPointerException
    @Test
    public void nullShufflerThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Bank(null));
    }

    // TC3 - Initial deck composition is correct
    @Test
    public void initialDeckCompositionIsCorrect() {
        Bank bank = new Bank(list -> { });
        Map<DevelopmentCardType, Integer> counts = new EnumMap<>(DevelopmentCardType.class);
        for (int i = 0; i < INITIAL_DEV_CARD_COUNT; i++) {
            DevelopmentCardType type = bank.drawDevelopmentCard().getType();
            counts.merge(type, 1, Integer::sum);
        }
        assertEquals(KNIGHT_COUNT, counts.get(DevelopmentCardType.KNIGHT));
        assertEquals(VICTORY_POINT_COUNT, counts.get(DevelopmentCardType.VICTORY_POINT));
        assertEquals(ACTION_CARD_COUNT, counts.get(DevelopmentCardType.ROAD_BUILDING));
        assertEquals(ACTION_CARD_COUNT, counts.get(DevelopmentCardType.YEAR_OF_PLENTY));
        assertEquals(ACTION_CARD_COUNT, counts.get(DevelopmentCardType.MONOPOLY));
    }
}
package domain;

import board.ResourceType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankTest {

    private static final int INITIAL_STOCK = 19;

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
}
